package tradingEngineService.referential;

import org.springframework.stereotype.Component;
import shared.service.TradingEngineServiceErrors;

import java.util.Collection;
import java.util.HashMap;

@Component
public class ReferentialInventory {
    private final HashMap<String, Instrument> instrumentsByCode = new HashMap<>();
    private final HashMap<String, Asset> assetsByCode = new HashMap<>();

    public Collection<Asset> getAllAssets() {
        return assetsByCode.values();
    }

    public Instrument lookupInstrument(String code) {
        if (!instrumentsByCode.containsKey(code))
            return null;
        return instrumentsByCode.get(code);
    }

    public Asset lookupAssetOrThrow(String code) throws Exception {
        if (!assetsByCode.containsKey(code))
            throw new Exception(TradingEngineServiceErrors.UnknownAsset);
        return assetsByCode.get(code);
    }

    public void addAsset(Asset asset) {
        assetsByCode.put(asset.code(), asset);
    }

    public void addInstrument(Instrument instrument) {
        instrumentsByCode.put(instrument.code(), instrument);
    }
}
