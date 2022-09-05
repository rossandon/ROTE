package referential;

import java.util.HashMap;

public class InstrumentInventory {
    private final HashMap<String, Instrument> instrumentsByCode = new HashMap<>();

    public Instrument lookupInstrument(String code) {
        if (!instrumentsByCode.containsKey(code))
            return null;
        return instrumentsByCode.get(code);
    }

    public void addInstrument(String code, Instrument instrument) {
        instrumentsByCode.put(code, instrument);
    }
}
