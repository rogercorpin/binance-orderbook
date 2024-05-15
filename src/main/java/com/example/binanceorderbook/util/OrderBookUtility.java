package com.example.binanceorderbook.util;

import java.util.Map;

import com.example.binanceorderbook.model.OrderBook;
import com.example.binanceorderbook.model.ParsedOrderBook;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public final class OrderBookUtility {

	private static Gson gson = new Gson();

	public static ParsedOrderBook parseOrderBookUpdate(String message) {
		JsonObject jsonMessage = gson.fromJson(message, JsonObject.class);
		String symbol = jsonMessage.get("symbol").getAsString();
		JsonArray bids = jsonMessage.getAsJsonArray("bids");
		JsonArray asks = jsonMessage.getAsJsonArray("asks");
		return new ParsedOrderBook(symbol, bids, asks);
	}

	public static void updateOrderBookFromJson(OrderBook orderBook, JsonArray bids, JsonArray asks) {
		for (JsonElement bid : bids) {
			JsonArray bidArray = bid.getAsJsonArray();
			Double price = bidArray.get(0).getAsDouble();
			Integer quantity = bidArray.get(1).getAsInt();
			orderBook.updateBid(price, quantity);
		}

		for (JsonElement ask : asks) {
			JsonArray askArray = ask.getAsJsonArray();
			Double price = askArray.get(0).getAsDouble();
			Integer quantity = askArray.get(1).getAsInt();
			orderBook.updateAsk(price, quantity);
		}
	}

	public static Double printOrderBooksAndVolumeChange(Map<String, OrderBook> orderBooks, Double previousVolume) {
		double totalVolume = 0.0;
		for (String key : orderBooks.keySet()) {
			OrderBookPrinter.printOrderBook(key, orderBooks.get(key));
			totalVolume += OrderBookCalculator.calculateTotalVolume(orderBooks.get(key));
			double volumeChange = totalVolume - previousVolume;
			OrderBookPrinter.printVolumeChange(volumeChange);
		}
		return totalVolume;
	}
}