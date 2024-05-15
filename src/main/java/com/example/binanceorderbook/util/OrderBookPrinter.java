package com.example.binanceorderbook.util;

import com.example.binanceorderbook.model.OrderBook;

public final class OrderBookPrinter {

    public static void printOrderBook(String symbol, OrderBook orderBook) {
        System.out.println("Order Book for " + symbol);
        orderBook.getBids().forEach((price, quantity) -> {
            System.out.println("BID: Price=" + price + ", Quantity=" + quantity);
        });
        orderBook.getAsks().forEach((price, quantity) -> {
            System.out.println("ASK: Price=" + price + ", Quantity=" + quantity);
        });
    }

    public static void printVolumeChange(double volumeChange) {
        System.out.println("Volume Change: " + volumeChange + " USDT\n");
    }
}