package com.example.binanceorderbook.util;

import com.example.binanceorderbook.model.OrderBook;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderBookCalculatorTest {

    @Test
    public void testCalculateTotalVolumeForOrderBook() {
        OrderBook orderBook = new OrderBook();
        orderBook.updateBid(60000.0, 1);
        orderBook.updateBid(59999.0, 2);
        orderBook.updateAsk(60100.0, 1);
        orderBook.updateAsk(60101.0, 2);

        double totalVolume = OrderBookCalculator.calculateTotalVolume(orderBook);
        double expectedVolume = (60000.0 * 1 + 59999.0 * 2) + (60100.0 * 1 + 60101.0 * 2);

        assertEquals(expectedVolume, totalVolume, 0.0001);
    }

    @Test
    public void testCalculateTotalVolumeForEmptyOrderBook() {
        OrderBook orderBook = new OrderBook();

        double totalVolume = OrderBookCalculator.calculateTotalVolume(orderBook);

        assertEquals(0.0, totalVolume, 0.0001);
    }

    @Test
    public void testCalculateTotalVolumeForPricesAndQuantities() {
        Map<Double, Integer> pricesAndQuantities = Map.of(
                60000.0, 1,
                59999.0, 2,
                60100.0, 1,
                60101.0, 2
        );

        double totalVolume = OrderBookCalculator.calculateTotalVolume(pricesAndQuantities);
        double expectedVolume = (60000.0 * 1) + (59999.0 * 2) + (60100.0 * 1) + (60101.0 * 2);

        assertEquals(expectedVolume, totalVolume, 0.0001);
    }

    @Test
    public void testCalculateTotalVolumeForEmptyPricesAndQuantities() {
        Map<Double, Integer> pricesAndQuantities = Map.of();

        double totalVolume = OrderBookCalculator.calculateTotalVolume(pricesAndQuantities);

        assertEquals(0.0, totalVolume, 0.0001);
    }

    @Test
    public void testCalculateTotalVolumeForNegativePricesAndQuantities() {
        Map<Double, Integer> pricesAndQuantities = Map.of(
                60000.0, -1,
                59999.0, -2
        );

        double totalVolume = OrderBookCalculator.calculateTotalVolume(pricesAndQuantities);
        double expectedVolume = (60000.0 * -1) + (59999.0 * -2);

        assertEquals(expectedVolume, totalVolume, 0.0001);
    }

    @Test
    public void testCalculateTotalVolumeForMixedPricesAndQuantities() {
        Map<Double, Integer> pricesAndQuantities = Map.of(
                60000.0, 1,
                59999.0, -2,
                60100.0, 1,
                60101.0, -2
        );

        double totalVolume = OrderBookCalculator.calculateTotalVolume(pricesAndQuantities);
        double expectedVolume = (60000.0 * 1) + (59999.0 * -2) + (60100.0 * 1) + (60101.0 * -2);

        assertEquals(expectedVolume, totalVolume, 0.0001);
    }
}
