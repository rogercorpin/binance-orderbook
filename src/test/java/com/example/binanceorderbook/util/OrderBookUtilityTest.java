package com.example.binanceorderbook.util;

import com.example.binanceorderbook.model.OrderBook;
import com.example.binanceorderbook.model.ParsedOrderBook;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class OrderBookUtilityTest {

    private final String sampleMessage = "{ \"symbol\": \"BTCUSDT\", \"bids\": [[60000.0, 1], [59999.0, 2]], \"asks\": [[60100.0, 1], [60101.0, 2]] }";
    private JsonObject jsonMessage;

    @BeforeEach
    public void setup() {
        jsonMessage = JsonParser.parseString(sampleMessage).getAsJsonObject();
    }

    @Test
    public void testParseOrderBookUpdate() {
        ParsedOrderBook parsedOrderBook = OrderBookUtility.parseOrderBookUpdate(sampleMessage);

        assertEquals("BTCUSDT", parsedOrderBook.getSymbol());
        assertEquals(jsonMessage.getAsJsonArray("bids"), parsedOrderBook.getBids());
        assertEquals(jsonMessage.getAsJsonArray("asks"), parsedOrderBook.getAsks());
    }

    @Test
    public void testUpdateOrderBookFromJson() {
        OrderBook orderBook = new OrderBook();
        JsonArray bids = jsonMessage.getAsJsonArray("bids");
        JsonArray asks = jsonMessage.getAsJsonArray("asks");

        OrderBookUtility.updateOrderBookFromJson(orderBook, bids, asks);

        assertEquals(1, orderBook.getBids().get(60000.0));
        assertEquals(2, orderBook.getBids().get(59999.0));
        assertEquals(1, orderBook.getAsks().get(60100.0));
        assertEquals(2, orderBook.getAsks().get(60101.0));
    }

    @Test
    public void testPrintOrderBooksAndVolumeChange() {
        Map<String, OrderBook> orderBooks = new HashMap<>();
        OrderBook orderBook1 = new OrderBook();
        orderBook1.updateBid(60000.0, 1);
        orderBook1.updateAsk(60100.0, 1);
        orderBooks.put("BTCUSDT", orderBook1);

        OrderBook orderBook2 = new OrderBook();
        orderBook2.updateBid(2000.0, 2);
        orderBook2.updateAsk(2100.0, 2);
        orderBooks.put("ETHUSDT", orderBook2);

        double previousVolume = 50000.0;

        try (MockedStatic<OrderBookPrinter> orderBookPrinterMock = mockStatic(OrderBookPrinter.class);
             MockedStatic<OrderBookCalculator> orderBookCalculatorMock = mockStatic(OrderBookCalculator.class)) {

            orderBookCalculatorMock.when(() -> OrderBookCalculator.calculateTotalVolume(orderBook1))
                    .thenReturn(60000.0 * 1 + 60100.0 * 1);
            orderBookCalculatorMock.when(() -> OrderBookCalculator.calculateTotalVolume(orderBook2))
                    .thenReturn(2000.0 * 2 + 2100.0 * 2);

            double totalVolume = OrderBookUtility.printOrderBooksAndVolumeChange(orderBooks, previousVolume);

            orderBookPrinterMock.verify(() -> OrderBookPrinter.printOrderBook("BTCUSDT", orderBook1), times(1));
            orderBookPrinterMock.verify(() -> OrderBookPrinter.printOrderBook("ETHUSDT", orderBook2), times(1));

            double btcusdtVolume = 60000.0 * 1 + 60100.0 * 1;
            double ethusdtVolume = 2000.0 * 2 + 2100.0 * 2;

            assertEquals(btcusdtVolume + ethusdtVolume, totalVolume);

            double expectedPreviousVolume = 78300.0;
            assertEquals(expectedPreviousVolume, (btcusdtVolume + ethusdtVolume) - previousVolume);
        }
    }
}
