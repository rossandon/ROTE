package helpers;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import tradingEngineService.referential.ReferentialInventory;

@Component
public class TestReferentialInitializer implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        var referential = event.getApplicationContext().getBean(ReferentialInventory.class);
        referential.addAsset(TestHelpers.USD);
        referential.addAsset(TestHelpers.SPY);
        referential.addInstrument(TestHelpers.SPYInst);
    }
}
