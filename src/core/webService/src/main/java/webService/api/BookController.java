package webService.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import shared.service.TradingEngineKafkaRequestResponseClient;
import shared.service.TradingEngineServiceRequest;
import webService.api.models.GetBookResponse;

import java.security.Principal;
import java.util.concurrent.Future;

@RestController
public class BookController {
        @Autowired
        TradingEngineKafkaRequestResponseClient client;

        @GetMapping("book")
        Future<GetBookResponse> getBooks(Principal principal) throws Exception {
            var future = client.sendAsync(TradingEngineServiceRequest.getBook("SPY"));
            return future.thenApply(resp -> {
                try {
                    resp.assertOk();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return new GetBookResponse(principal.getName().hashCode(), resp.orderBookSnapshot());
            });
        }
}
