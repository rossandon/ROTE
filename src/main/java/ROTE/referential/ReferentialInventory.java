package ROTE.referential;

import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class ReferentialInventory {
    private final HashMap<String, Instrument> instrumentsByCode = new HashMap<>();
    private final HashMap<String, Asset> assetsByCode = new HashMap<>();

    public Instrument lookupInstrument(String code) {
        if (!instrumentsByCode.containsKey(code))
            return null;
        return instrumentsByCode.get(code);
    }

    public Asset lookupAsset(String code) {
        if (!assetsByCode.containsKey(code))
            return null;
        return assetsByCode.get(code);
    }

    public void addAsset(Asset asset) {
        assetsByCode.put(asset.code(), asset);
    }

    public void addInstrument(Instrument instrument) {
        instrumentsByCode.put(instrument.code(), instrument);
    }
}
