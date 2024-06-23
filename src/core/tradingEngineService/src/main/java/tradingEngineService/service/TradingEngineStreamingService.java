package tradingEngineService.service;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import shared.kafka.RoteKafkaProducer;
import shared.orderBook.LimitOrderResultStatus;
import shared.orderBook.OrderBookLimitOrderResult;
import shared.service.TradingEngineServiceConsts;
import shared.service.TradingEngineServiceRequest;
import shared.service.TradingEngineServiceRequestType;
import shared.service.TradingEngineServiceResponse;
import shared.service.results.*;
import tradingEngineService.referential.Instrument;
import tradingEngineService.referential.ReferentialInventory;
import tradingEngineService.tradingEngine.Account;
import tradingEngineService.tradingEngine.LimitOrder;
import tradingEngineService.tradingEngine.TradingEngine;

import java.io.Closeable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.concurrent.Future;

@Component
public class TradingEngineStreamingService implements Runnable, Closeable {
    private static final Logger log = Logger.getLogger(TradingEngineStreamingService.class);

    private final TradingEngineConsumer consumer;
    private final RoteKafkaProducer<String, OrderBookSnapshot> orderBookSnapshotProducer;
    private final RoteKafkaProducer<String, Trade> tradeProducer;
    private final TradingEngine tradingEngine;
    private final ReferentialInventory referentialInventory;

    public TradingEngineStreamingService(TradingEngineConsumer requestConsumer,
                                         RoteKafkaProducer<String, OrderBookSnapshot> orderBookSnapshotProducer,
                                         RoteKafkaProducer<String, Trade> tradeProducer,
                                         TradingEngine tradingEngine,
                                         ReferentialInventory referentialInventory) {
        this.consumer = requestConsumer;
        this.orderBookSnapshotProducer = orderBookSnapshotProducer;
        this.tradeProducer = tradeProducer;
        this.tradingEngine = tradingEngine;
        this.referentialInventory = referentialInventory;

        this.consumer.addHandler(TradingEngineServiceRequestType.GetBalance, this::handleGetBalanceRequest);
        this.consumer.addHandler(TradingEngineServiceRequestType.GetBalances, this::handleGetBalancesRequest);
        this.consumer.addHandler(TradingEngineServiceRequestType.GetBook, this::handleGetBookRequest);
        this.consumer.addHandler(TradingEngineServiceRequestType.LimitOrder, this::handleLimitOrderRequest);
        this.consumer.addHandler(TradingEngineServiceRequestType.AdjustBalance, this::handleAdjustBalanceRequest);
        this.consumer.addHandler(TradingEngineServiceRequestType.Cancel, this::handleCancelRequest);
        this.consumer.addHandler(TradingEngineServiceRequestType.CancelAll, this::handleCancelAllRequest);
        this.consumer.addHandler(TradingEngineServiceRequestType.Error, this::handleErrorRequest);
    }

    public void run() {
        consumer.run();
    }

    public Future<Object> snapshot() {
        return consumer.snapshot();
    }

    private TradingEngineServiceResponse handleGetBookRequest(TradingEngineServiceRequest request) throws Exception {
        var instrument = referentialInventory.lookupInstrumentOrThrow(request.instrumentCode());
        var book = getOrderBookSnapshot(instrument);
        return new TradingEngineServiceResponse(book);
    }

    private TradingEngineServiceResponse handleGetBalanceRequest(TradingEngineServiceRequest request) throws Exception {
        var asset = referentialInventory.lookupAssetOrThrow(request.assetCode());
        var balance = tradingEngine.getBalance(request.accountId(), asset);
        return new TradingEngineServiceResponse(new GetBalanceResult(balance));
    }

    private TradingEngineServiceResponse handleGetBalancesRequest(TradingEngineServiceRequest request) {
        var allAssets = referentialInventory.getAllAssets();
        var map = new HashMap<String, BigDecimal>();
        for (var asset : allAssets) {
            var balance = tradingEngine.getBalance(request.accountId(), asset);
            map.put(asset.code(), balance);
        }
        return new TradingEngineServiceResponse(new GetBalancesResult(map));
    }

    private TradingEngineServiceResponse handleAdjustBalanceRequest(
            TradingEngineServiceRequest request) throws Exception {
        var asset = referentialInventory.lookupAssetOrThrow(request.assetCode());
        tradingEngine.adjustBalance(new Account(request.accountId()), asset, request.amount());
        return new TradingEngineServiceResponse();
    }

    private TradingEngineServiceResponse handleLimitOrderRequest(TradingEngineServiceRequest request) throws Exception {
        var instrument = referentialInventory.lookupInstrumentOrThrow(request.instrumentCode());
        var limitOrder = new LimitOrder(instrument, new Account(request.accountId()), request.amount(), request.price(), request.side());
        var limitOrderResult = tradingEngine.limitOrder(limitOrder);
        if (limitOrderResult.type() == LimitOrderResultStatus.Ok) {
            sendOrderBookSnapshotUpdate(instrument);
            sendTradeUpdates(instrument, limitOrderResult.result());
        }
        return new TradingEngineServiceResponse(limitOrderResult);
    }

    private void sendTradeUpdates(Instrument instrument, OrderBookLimitOrderResult result) {
        if (result.trades() == null)
            return;
        for (var trade : result.trades()) {
            var marketDataTrade = new Trade(instrument.code(), LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME), trade.id(), trade.size(), trade.price(), trade.takerSide());
            tradeProducer.produce(TradingEngineServiceConsts.TradeDataTopic, instrument.code(), marketDataTrade, null, true);
        }
    }

    private void sendOrderBookSnapshotUpdate(Instrument instrument) {
        var book = getOrderBookSnapshot(instrument);
        orderBookSnapshotProducer.produce(TradingEngineServiceConsts.OrderBookDataTopic, instrument.code(), book, null, true);
    }

    private OrderBookSnapshot getOrderBookSnapshot(Instrument instrument) {
        var orderBook = tradingEngine.ensureOrderBook(instrument);
        return new OrderBookSnapshot(instrument.code(), orderBook.orderBook().orderSequence, orderBook.orderBook().bids, orderBook.orderBook().asks);
    }

    private TradingEngineServiceResponse handleCancelRequest(TradingEngineServiceRequest request) throws Exception {
        var instrument = referentialInventory.lookupInstrumentOrThrow(request.instrumentCode());
        var isCancelled = tradingEngine.cancel(new Account(request.accountId()), instrument, request.orderId());
        if (isCancelled) sendOrderBookSnapshotUpdate(instrument);
        return new TradingEngineServiceResponse(new CancelOrderResult(isCancelled));
    }

    private TradingEngineServiceResponse handleCancelAllRequest(TradingEngineServiceRequest request) throws Exception {
        var instrument = referentialInventory.lookupInstrumentOrThrow(request.instrumentCode());
        var isCancelled = tradingEngine.cancelAll(new Account(request.accountId()), instrument);
        if (isCancelled) sendOrderBookSnapshotUpdate(instrument);
        return new TradingEngineServiceResponse(new CancelOrderResult(isCancelled));
    }

    private TradingEngineServiceResponse handleErrorRequest(TradingEngineServiceRequest request) throws Exception {
        throw new Exception("ping");
    }

    @Override
    public void close() {
        consumer.close();
        orderBookSnapshotProducer.close();
        tradeProducer.close();
    }
}
