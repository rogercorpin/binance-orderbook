package com.example.binanceorderbook.util;

import java.util.Map;

import com.example.binanceorderbook.model.OrderBook;

public final class OrderBookCalculator {

	public static double calculateTotalVolume(OrderBook orderBook) {
		double totalVolume = 0.0;
		totalVolume += calculateTotalVolume(orderBook.getBids());
		totalVolume += calculateTotalVolume(orderBook.getAsks());
		return totalVolume;
	}

	public static double calculateTotalVolume(Map<Double, Integer> pricesAndQuantities) {
		return pricesAndQuantities.entrySet().stream().mapToDouble(entry -> entry.getKey() * entry.getValue()).sum();
	}
}