package com.example.binanceorderbook.util;

import com.example.binanceorderbook.model.OrderBook;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderBookPrinterTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    public void testPrintOrderBook() {
        OrderBook orderBook = new OrderBook();
        orderBook.updateBid(60000.0, 1);
        orderBook.updateBid(59999.0, 2);
        orderBook.updateAsk(60100.0, 1);
        orderBook.updateAsk(60101.0, 2);

        OrderBookPrinter.printOrderBook("BTCUSDT", orderBook);

        String expectedOutput = "Order Book for BTCUSDT\n" +
                "BID: Price=60000.0, Quantity=1\n" +
                "BID: Price=59999.0, Quantity=2\n" +
                "ASK: Price=60100.0, Quantity=1\n" +
                "ASK: Price=60101.0, Quantity=2\n";

        assertEquals(expectedOutput, outContent.toString());
    }

    @Test
    public void testPrintVolumeChange() {
        double volumeChange = 12345.67;

        OrderBookPrinter.printVolumeChange(volumeChange);

        String expectedOutput = "Volume Change: 12345.67 USDT\n\n";

        assertEquals(expectedOutput, outContent.toString());
    }
}
