package ROTE.referential;

public record Instrument(String code, Asset baseAsset, Asset quoteAsset, Integer id) {}
