package com.example.binanceorderbook;

import com.example.binanceorderbook.service.BinanceApiService;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class BinanceOrderBookApplicationTests {

    @Mock
    private WebSocketClient webSocketClient;

    @InjectMocks
    private BinanceApiService binanceApiService;

    @Value("${binance.websocket.url}")
    private String websocketUrl = "wss://binance-websocket-url.com";

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        binanceApiService = spy(new BinanceApiService(websocketUrl));
        binanceApiService.setWebSocketClient(webSocketClient);
        Method postConstruct =  BinanceApiService.class.getDeclaredMethod("init",null);
        postConstruct.setAccessible(true);
        postConstruct.invoke(binanceApiService);
    }

    @Test
    public void testCreateWebSocketClient() throws Exception {
        doReturn(webSocketClient).when(binanceApiService).createWebSocketClient("wss://binance-websocket-url.com");
        WebSocketClient client = binanceApiService.createWebSocketClient(websocketUrl);
        assertNotNull(client);
    }

    @Test
    public void testCreateWebSocketClient_withInvalidUrl()  throws Exception{
        assertThrows(IllegalArgumentException.class, () -> {
        	new BinanceApiService("invalid-url");
        });
    }

    @Test
    public void testWebSocketConnection() throws Exception {
        doReturn(webSocketClient).when(binanceApiService).createWebSocketClient("wss://binance-websocket-url.com");
        WebSocketClient client = binanceApiService.createWebSocketClient(websocketUrl);
        client.onOpen(mock(ServerHandshake.class));
        verify(webSocketClient, times(1)).connect();
        String message = "{\"symbol\":\"BTCUSDT\",\"bids\":[[\"60000\",\"1\"],[\"59999\",\"2\"],[\"59998\",\"3\"]],\"asks\":[[\"60100\",\"1\"],[\"60101\",\"2\"],[\"60102\",\"3\"]]}";
        client.onMessage(message);
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(webSocketClient, times(1)).onMessage(messageCaptor.capture());
        assertEquals(message, messageCaptor.getValue());
    }
}