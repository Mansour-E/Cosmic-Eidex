package cosmic.eidex.Config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.StompWebSocketEndpointRegistration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WebSocketConfigTest {

    private WebSocketConfig webSocketConfig;

    @BeforeEach
    void setUp() {
        webSocketConfig = new WebSocketConfig();
    }

    @Test
    void configureMessageBroker() {
        MessageBrokerRegistry reg = mock(MessageBrokerRegistry.class);
        webSocketConfig.configureMessageBroker(reg);
        verify(reg).enableSimpleBroker("/topic");
        verify(reg).setApplicationDestinationPrefixes("/app");
    }

    @Test
    void registerStompEndpoints() {
        StompEndpointRegistry stompEnd = mock(StompEndpointRegistry.class);
        StompWebSocketEndpointRegistration endpointRegistration = mock(StompWebSocketEndpointRegistration.class);
        when(stompEnd.addEndpoint(anyString())).thenReturn(endpointRegistration);
        when(endpointRegistration.setAllowedOriginPatterns("*")).thenReturn(endpointRegistration);
        webSocketConfig.registerStompEndpoints(stompEnd);
        verify(stompEnd, atLeastOnce()).addEndpoint(anyString());
        verify(endpointRegistration, atLeastOnce()).setAllowedOriginPatterns("*");
    }
}