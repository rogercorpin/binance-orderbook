package com.example.binanceorderbook.service;

import com.example.binanceorderbook.model.OrderBook;

import org.java_websocket.client.WebSocketClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

public class BinanceApiServiceTest {

	private BinanceApiService binanceApiService;

	@Mock
	private WebSocketClient webSocketClient;

	@BeforeEach
	public void setUp() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
		MockitoAnnotations.initMocks(this);
		binanceApiService = new BinanceApiService("wss://example.com");
		Method postConstruct = BinanceApiService.class.getDeclaredMethod("init", null);
		postConstruct.setAccessible(true);
		postConstruct.invoke(binanceApiService);
	}

	@Test
	public void testCreateWebSocketClient() {
		assertNotNull(binanceApiService.createWebSocketClient("wss://example.com"));
	}

	@Test
	public void testHandleOrderBookUpdate() {
		String message = "{\"symbol\":\"BTCUSDT\",\"bids\":[[\"60000.2\",\"5\"]],\"asks\":[[\"60500.5\",\"73\"]]}";

		binanceApiService.handleOrderBookUpdate(message);
		Map<String, OrderBook> result = binanceApiService.getOrderBooks();
		assertNotNull(result);

		Map.Entry<String, OrderBook> entry = result.entrySet().iterator().next();
		String keyResult = entry.getKey();
		OrderBook valueResult = entry.getValue();
		assertEquals("BTCUSDT", keyResult);

		Map<Double, Integer> bidsExpected = new LinkedHashMap<>();
		bidsExpected.put(60000.2, 5);
		assertEquals(bidsExpected, valueResult.getBids());

		Map<Double, Integer> asksExpected = new LinkedHashMap<>();
		asksExpected.put(60500.5, 73);
		assertEquals(asksExpected, valueResult.getAsks());
	}

	@Test
	public void testPreviousVolumeUpdate() {
		String initialMessage = "{\"symbol\":\"BTCUSDT\",\"bids\":[[\"60000.03\",\"1\"],[\"10009.51\",\"2\"]],\"asks\":[[\"60100.90\",\"1\"],[\"60101.45\",\"2\"]]}";
		binanceApiService.handleOrderBookUpdate(initialMessage);

		binanceApiService.schedulePrintTask();

		double firstPreviousVolume = binanceApiService.getPreviousVolume();

		double expectredFirstPreviousVolume = (60000.03 * 1) + (10009.51 * 2) + (60100.90 * 1) + (60101.45 * 2);
		assertEquals(expectredFirstPreviousVolume, firstPreviousVolume, 0.001);

		// Simulate receiving another order book update
		String updatedMessage = "{\"symbol\":\"BTCUSDT\",\"bids\":[[\"60000.08\",\"2\"],[\"10009.33\",\"3\"]],\"asks\":[[\"60100.97\",\"2\"],[\"60101.01\",\"3\"]]}";
		binanceApiService.handleOrderBookUpdate(updatedMessage);

		// Simulate the passage of another 10 seconds and the schedulePrintTask method
		// being called
		binanceApiService.schedulePrintTask();

		// Store the previousVolume after the second schedulePrintTask call
		double secondPreviousVolume = binanceApiService.getPreviousVolume();

		double expectredSecondPreviousVolume = expectredFirstPreviousVolume + (60000.08 * 2) + (10009.33 * 3)
				+ (60100.97 * 2) + (60101.01 * 3);
		// Ensure that the previousVolume is updated correctly
		assertEquals(expectredSecondPreviousVolume, secondPreviousVolume, 0.001);

		// Ensure that the previousVolume after the first call is different from the
		// previousVolume after the second call
		assertNotEquals(firstPreviousVolume, secondPreviousVolume);
	}

	@Test
	public void testOrderBooksLimit() {
		// message -> we will try to pass over 50 elements for each of bids and asks
		String message = "{\"symbol\":\"BTCUSDT\","
			+ "\"bids\":["
			+ "[\"1001.0\",\"1\"],[\"1002.0\",\"2\"],[\"1003.0\",\"3\"],[\"1004.0\",\"4\"],"
			+ "[\"10005.0\",\"5\"],[\"10006.0\",\"6\"],[\"10007.0\",\"7\"],[\"10008.0\",\"8\"],"
			+ "[\"10009.0\",\"9\"],[\"10010.0\",\"10\"],[\"10011.0\",\"11\"],[\"10012.0\",\"12\"],"
			+ "[\"10013.0\",\"13\"],[\"10014.0\",\"14\"],[\"10015.0\",\"15\"],[\"10016.0\",\"16\"],"
			+ "[\"10017.0\",\"17\"],[\"10018.0\",\"18\"],[\"10019.0\",\"19\"],[\"10020.0\",\"20\"],"
			+ "[\"10021.0\",\"21\"],[\"10022.0\",\"22\"],[\"10023.0\",\"23\"],[\"10024.0\",\"24\"],"
			+ "[\"10025.0\",\"25\"],[\"10026.0\",\"26\"],[\"10027.0\",\"27\"],[\"10028.0\",\"28\"],"
			+ "[\"10029.0\",\"29\"],[\"10030.0\",\"30\"],[\"10031.0\",\"31\"],[\"10032.0\",\"32\"],"
			+ "[\"10033.0\",\"33\"],[\"10034.0\",\"34\"],[\"10035.0\",\"35\"],[\"10036.0\",\"36\"],"
			+ "[\"10037.0\",\"37\"],[\"10038.0\",\"38\"],[\"10039.0\",\"39\"],[\"10040.0\",\"40\"],"
			+ "[\"10041.0\",\"41\"],[\"10042.0\",\"42\"],[\"10043.0\",\"43\"],[\"10044.0\",\"44\"],"
			+ "[\"10045.0\",\"45\"],[\"10046.0\",\"46\"],[\"10047.0\",\"47\"],[\"10048.0\",\"48\"],"
			+ "[\"10049.0\",\"49\"],[\"10050.0\",\"50\"],[\"10051.0\",\"51\"],[\"10052.0\",\"52\"],"
			+ "[\"10053.0\",\"53\"]],"
			+ "\"asks\":["
			+ "[\"1001.0\",\"1\"],[\"1002.0\",\"2\"],[\"1003.0\",\"3\"],[\"4004.0\",\"4\"],"
			+ "[\"10005.0\",\"5\"],[\"10006.0\",\"6\"],[\"10007.0\",\"7\"],[\"10008.0\",\"8\"],"
			+ "[\"10009.0\",\"9\"],[\"10010.0\",\"10\"],[\"10011.0\",\"11\"],[\"10012.0\",\"12\"],"
			+ "[\"10013.0\",\"13\"],[\"10014.0\",\"14\"],[\"10015.0\",\"15\"],[\"10016.0\",\"16\"],"
			+ "[\"10017.0\",\"17\"],[\"10018.0\",\"18\"],[\"10019.0\",\"19\"],[\"10020.0\",\"20\"],"
			+ "[\"10021.0\",\"21\"],[\"10022.0\",\"22\"],[\"10023.0\",\"23\"],[\"10024.0\",\"24\"],"
			+ "[\"10025.0\",\"25\"],[\"10026.0\",\"26\"],[\"10027.0\",\"27\"],[\"10028.0\",\"28\"],"
			+ "[\"10029.0\",\"29\"],[\"10030.0\",\"30\"],[\"10031.0\",\"31\"],[\"10032.0\",\"32\"],"
			+ "[\"10033.0\",\"33\"],[\"10034.0\",\"34\"],[\"10035.0\",\"35\"],[\"10036.0\",\"36\"],"
			+ "[\"10037.0\",\"37\"],[\"10038.0\",\"38\"],[\"10039.0\",\"39\"],[\"10040.0\",\"40\"],"
			+ "[\"10041.0\",\"41\"],[\"10042.0\",\"42\"],[\"10043.0\",\"43\"],[\"10044.0\",\"44\"],"
			+ "[\"10045.0\",\"45\"],[\"10046.0\",\"46\"],[\"10047.0\",\"47\"],[\"10048.0\",\"48\"],"
			+ "[\"10049.0\",\"49\"],[\"10050.0\",\"50\"],[\"10051.0\",\"51\"],[\"10052.0\",\"52\"],"
			+ "[\"10053.0\",\"53\"]]}";
		binanceApiService.handleOrderBookUpdate(message);

		Map<String, OrderBook> resultOrderBooks = binanceApiService.getOrderBooks();
		assertNotNull(resultOrderBooks);

		Map.Entry<String, OrderBook> entry = resultOrderBooks.entrySet().iterator().next();
		String keyResult = entry.getKey();
		OrderBook valueResult = entry.getValue();
		assertEquals("BTCUSDT", keyResult);

		int MAX_LIMIT = 50;
		assertEquals(MAX_LIMIT, valueResult.getBids().size());
		assertEquals(MAX_LIMIT, valueResult.getAsks().size());
		
		//Check the values of first element of the array of bids and asks. 
		//Note: when bids/asks reached its limit(50) and try to add new bid/ask, 
		//     it will be added up in the end and remove the first element.
		Map<Double, Integer> resultBids = valueResult.getBids();
		Map.Entry<Double, Integer> firstBid = resultBids.entrySet()
				  .stream()
				  .findFirst()
				  .get();
		Map<Double, Integer> resultAsks = valueResult.getAsks();
		Map.Entry<Double, Integer> firstAsk = resultAsks.entrySet()
				  .stream()
				  .findFirst()
				  .get();
		assertEquals(1004.0, firstBid.getKey());
		assertEquals(4, firstBid.getValue());
		assertEquals(4004.0, firstAsk.getKey());
		assertEquals(4, firstAsk.getValue());
	}
}