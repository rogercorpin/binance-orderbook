package com.example.binanceorderbook.service;

import com.example.binanceorderbook.model.OrderBook;
import com.example.binanceorderbook.model.ParsedOrderBook;
import com.example.binanceorderbook.util.BinanceUtility;
import com.example.binanceorderbook.util.OrderBookUtility;

import jakarta.annotation.PostConstruct;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

@Service
public class BinanceApiService {

	private WebSocketClient webSocketClient;

	private Map<String, OrderBook> orderBooks = new HashMap<>();
	private Double previousVolume = 0.0;

	public void setWebSocketClient(WebSocketClient webSocketClient) {
		this.webSocketClient = webSocketClient;
	}

	public Map<String, OrderBook> getOrderBooks() {
		return orderBooks;
	}

	public Double getPreviousVolume() {
		return this.previousVolume;
	}
	
	public void setOrderBooks(Map<String, OrderBook> orderBooks) {
		this.orderBooks = orderBooks;
	}

	public BinanceApiService(@Value("${binance.websocket.url}") String websocketUrl) {
		if (websocketUrl != null) {
			if (!BinanceUtility.isValidWebSocketUrl(websocketUrl)) {
				throw new IllegalArgumentException("Invalid WebSocket URL: " + websocketUrl);
			}
			webSocketClient = createWebSocketClient(websocketUrl);
		}
	}

	@PostConstruct
	public void init() {
		webSocketClient.connect();
	}

	public void handleOrderBookUpdate(String message) {
		try {
			ParsedOrderBook parsedOrderBook = OrderBookUtility.parseOrderBookUpdate(message);
			OrderBook orderBook = orderBooks.getOrDefault(parsedOrderBook.getSymbol(), new OrderBook());
			OrderBookUtility.updateOrderBookFromJson(orderBook, parsedOrderBook.getBids(), parsedOrderBook.getAsks());
			orderBooks.put(parsedOrderBook.getSymbol(), orderBook);
		} catch (Exception e) {
			System.err.println("Error handling order book update: " + e.getMessage());
		}
	}

	@Scheduled(fixedDelay = 10000, initialDelay = 10000)
	public void schedulePrintTask() {
		previousVolume = OrderBookUtility.printOrderBooksAndVolumeChange(orderBooks, previousVolume);
	}

	public OrderBook getOrderBook(String symbol) {
		return orderBooks.get(symbol);
	}

	public WebSocketClient createWebSocketClient(String websocketUrl) {
		try {
			return new WebSocketClient(new URI(websocketUrl)) {
				@Override
				public void onOpen(ServerHandshake handshakedata) {
					System.out.println("Connected to Binance WebSocket");
				}

				@Override
				public void onMessage(String message) {
					System.out.println("Received message: " + message);
					if (message != null) {
						handleOrderBookUpdate(message);
					}
				}

				@Override
				public void onClose(int code, String reason, boolean remote) {
					System.out.println("Closed: " + reason);
				}

				@Override
				public void onError(Exception ex) {
					System.err.println("Error: " + ex.getMessage());
				}
			};
		} catch (URISyntaxException e) {
			System.err.println("Error creating WebSocket client: " + e.getMessage());
			return null;
		}
	}
}