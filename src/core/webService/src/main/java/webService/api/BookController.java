package webService.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import shared.service.TradingEngineKafkaRequestResponseClient;
import shared.service.TradingEngineServiceRequest;
import webService.api.models.GetBookResponse;
import webService.security.RoteUserContext;

import java.util.concurrent.Future;

@RestController
public class BookController {
        @Autowired
        TradingEngineKafkaRequestResponseClient client;

        @GetMapping("book")
        Future<GetBookResponse> getBooks(RoteUserContext roteUserContext, @RequestParam("instrumentCode") String instrumentCode) throws Exception {
            var future = client.sendAsync(TradingEngineServiceRequest.getBook(instrumentCode));
            var accountId = roteUserContext.getAccountId();

            return future.thenApply(resp -> {
                try {
                    resp.assertOk();
                    return new GetBookResponse(accountId, resp.orderBookSnapshot());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
}
