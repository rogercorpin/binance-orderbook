package com.example.binanceorderbook.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class OrderBook {

	private static final int MAX_DEPTH = 50;

	private Map<Double, Integer> bids = new LinkedHashMap<Double, Integer>(MAX_DEPTH, 0.75f, true) {
		@Override
		protected boolean removeEldestEntry(Map.Entry<Double, Integer> eldest) {
			return size() > MAX_DEPTH;
		}
	};
	
	private Map<Double, Integer> asks = new LinkedHashMap<Double, Integer>(MAX_DEPTH, 0.75f, true) {
		@Override
		protected boolean removeEldestEntry(Map.Entry<Double, Integer> eldest) {
			return size() > MAX_DEPTH;
		}
	};

	public void updateBid(Double price, Integer quantity) {
		bids.put(price, quantity);
	}

	public void updateAsk(Double price, Integer quantity) {
		asks.put(price, quantity);
	}

	public Map<Double, Integer> getBids() {
		return bids;
	}

	public Map<Double, Integer> getAsks() {
		return asks;
	}
}