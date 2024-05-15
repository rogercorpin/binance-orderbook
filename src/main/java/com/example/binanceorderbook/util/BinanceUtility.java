package com.example.binanceorderbook.util;

import java.net.URI;
import java.net.URISyntaxException;

public final class BinanceUtility {

    public static boolean isValidWebSocketUrl(String url) {
        try {
            URI uri = new URI(url);
            return "wss".equalsIgnoreCase(uri.getScheme());
        } catch (URISyntaxException e) {
            return false;
        }
    }
}