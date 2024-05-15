package com.example.binanceorderbook.model;

import com.google.gson.JsonArray;

public class ParsedOrderBook {
	
    private final String symbol;
    private final JsonArray bids;
    private final JsonArray asks;

    public ParsedOrderBook(String symbol, JsonArray bids, JsonArray asks) {
        this.symbol = symbol;
        this.bids = bids;
        this.asks = asks;
    }

    public String getSymbol() {
        return symbol;
    }

    public JsonArray getBids() {
        return bids;
    }

    public JsonArray getAsks() {
        return asks;
    }
}