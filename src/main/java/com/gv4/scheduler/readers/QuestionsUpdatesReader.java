package com.gv4.scheduler.readers;

import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ReaderNotOpenException;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import project.entropy.scheduler.config.BinanceApiProperties;
import project.entropy.scheduler.models.BinanceApiCandlestickEntity;

import java.math.BigDecimal;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Configuration
@Component
public class QuestionsUpdatesReader implements ItemReader<BinanceApiCandlestickEntity> {

    private static final Logger logger = LoggerFactory.getLogger(ReaderBinanceApiCandlestick.class);

    private final BinanceApiProperties binanceApiProperties;
    private final WebClient webClient;
    private String activeUri;
    private List<BinanceApiCandlestickEntity> cacheBinanceApiCandlestickEntityList;
    private int counter;

    public QuestionsUpdatesReader(BinanceApiProperties binanceApiProperties) {
        this.webClient = WebClient.builder().build();
        this.counter = 0;
        this.binanceApiProperties = binanceApiProperties;
    }

    @Override
    @SneakyThrows
    public BinanceApiCandlestickEntity read() {

        if (this.cacheBinanceApiCandlestickEntityList == null) {
            counter = 0;
            try{
                this.activeUri = buildFinalRequestUri();
            }
            catch (ConnectException | IllegalArgumentException exception) {
                logger.error(exception.getMessage());
                throw new ReaderNotOpenException("reader interrupted, caused by: " + exception.getClass().toString() + ", " + exception.getMessage());
            }
            this.cacheBinanceApiCandlestickEntityList = fetchEntityFromApi();
        }

        if (counter == cacheBinanceApiCandlestickEntityList.size()) {
            this.cacheBinanceApiCandlestickEntityList = null;
            return null;
        }
        else
            return cacheBinanceApiCandlestickEntityList.get(counter++);
    }

    //FIXME make me fully asynchronous - not blocking
    @SneakyThrows
    private List<BinanceApiCandlestickEntity> fetchEntityFromApi(){

        Optional<BigDecimal[][]> binanceOptional = webClient.get().uri(activeUri).retrieve().bodyToMono(BigDecimal[][].class).blockOptional();
        List<BinanceApiCandlestickEntity> binanceApiCandlestickEntityList = new ArrayList<>();

        for (int i = 0; i < (binanceOptional.map(singleArray -> singleArray.length).orElse(0)) ; i++) {

            BinanceApiCandlestickEntity binanceApiCandlestickEntity = new BinanceApiCandlestickEntity();
            binanceApiCandlestickEntity.setOpenTime(binanceOptional.get()[i][0].toString());
            binanceApiCandlestickEntity.setOpenPrice(binanceOptional.get()[i][1].toString());
            binanceApiCandlestickEntity.setHighPrice(binanceOptional.get()[i][2].toString());
            binanceApiCandlestickEntity.setLowPrice(binanceOptional.get()[i][3].toString());
            binanceApiCandlestickEntity.setClosePrice(binanceOptional.get()[i][4].toString());
            binanceApiCandlestickEntity.setVolume(binanceOptional.get()[i][5].toString());
            binanceApiCandlestickEntity.setCloseTime(binanceOptional.get()[i][6].toString());
            binanceApiCandlestickEntity.setQuoteAssetVolume(binanceOptional.get()[i][7].toString());
            binanceApiCandlestickEntity.setNumberOfTrades(binanceOptional.get()[i][8].toString());
            binanceApiCandlestickEntity.setTakerBuyBaseAssetVolume(binanceOptional.get()[i][9].toString());
            binanceApiCandlestickEntity.setTakerBuyQuoteAssetVolume(binanceOptional.get()[i][10].toString());
            binanceApiCandlestickEntity.setIgnore(binanceOptional.get()[i][11].toString());
            binanceApiCandlestickEntityList.add(binanceApiCandlestickEntity);
        }
      return binanceApiCandlestickEntityList;
    }

    public String getBinanceServer()throws ConnectException{
        Optional<ResponseEntity<String>> optionalResponseEntity;
        try {
            optionalResponseEntity = WebClient.builder().baseUrl(binanceApiProperties.base + binanceApiProperties.ping).build().get().retrieve().toEntity(String.class).blockOptional();
            if (optionalResponseEntity.isPresent() && optionalResponseEntity.get().getStatusCode().is2xxSuccessful())
                return binanceApiProperties.base;
        }
        catch (WebClientException webClientException){logger.error(binanceApiProperties.base + " is not responding, trying to connect to mirror");}
        try {
            optionalResponseEntity = WebClient.builder().baseUrl(binanceApiProperties.base1 + binanceApiProperties.ping).build().get().retrieve().toEntity(String.class).blockOptional();
            if (optionalResponseEntity.isPresent() && optionalResponseEntity.get().getStatusCode().is2xxSuccessful())
                return binanceApiProperties.base1;
        }
        catch (WebClientException webClientException){logger.error(binanceApiProperties.base1 + " is not responding, trying to connect to mirror");}
        try {
            optionalResponseEntity = WebClient.builder().baseUrl(binanceApiProperties.base2 + binanceApiProperties.ping).build().get().retrieve().toEntity(String.class).blockOptional();
            if (optionalResponseEntity.isPresent() && optionalResponseEntity.get().getStatusCode().is2xxSuccessful())
                return binanceApiProperties.base2;
        }
        catch (WebClientException webClientException){logger.error(binanceApiProperties.base2 + " is not responding, trying to connect to mirror");}
        try {
            optionalResponseEntity = WebClient.builder().baseUrl(binanceApiProperties.base3 + binanceApiProperties.ping).build().get().retrieve().toEntity(String.class).blockOptional();
            if (optionalResponseEntity.isPresent() && optionalResponseEntity.get().getStatusCode().is2xxSuccessful())
                return binanceApiProperties.base3;
        }
        catch (WebClientException webClientException){logger.error(binanceApiProperties.base3 + " is not responding, no more mirrors to connect");}

        throw new ConnectException("no binance server available to connect");
    }

    public String buildFinalRequestUri() throws ConnectException, IllegalArgumentException{
        StringBuilder finalRequestUri = new StringBuilder();
        finalRequestUri.append(getBinanceServer());
        finalRequestUri.append(binanceApiProperties.candlestick);

        if(!(binanceApiProperties.symbol.equals("null") || binanceApiProperties.symbol.equals("")))
            finalRequestUri.append("?symbol=").append(binanceApiProperties.symbol);
        else
            throw new IllegalArgumentException("symbol is required");

        if(!(binanceApiProperties.interval.equals("null") || binanceApiProperties.interval.equals("")))
            finalRequestUri.append("&interval=").append(binanceApiProperties.interval);
        else
            throw new IllegalArgumentException("interval is required");

        if(!(binanceApiProperties.startTime.equals("null") || binanceApiProperties.startTime.equals("")))
            finalRequestUri.append("&startTime=").append(binanceApiProperties.startTime);

        if(!(binanceApiProperties.limit.equals("null") || binanceApiProperties.limit.equals("")))
            finalRequestUri.append("&limit=").append(binanceApiProperties.limit);

        if(!(binanceApiProperties.endTime.equals("null") || binanceApiProperties.endTime.equals("")))
            finalRequestUri.append("&endTime=").append(binanceApiProperties.endTime);

        return finalRequestUri.toString();
    }
}
