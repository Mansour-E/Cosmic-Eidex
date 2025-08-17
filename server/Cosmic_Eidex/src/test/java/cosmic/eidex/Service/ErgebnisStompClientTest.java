package cosmic.eidex.Service;

import ch.qos.logback.core.net.server.Client;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cosmic.eidex.DTO.SpielraumDTO;
import cosmic.eidex.Service.SpielNachrichten.SpielraumChatNachricht;
import cosmic.eidex.Service.SpielNachrichten.TunierErgebnisNachricht;
import cosmic.eidex.spielmodell.Spieler;
import org.java_websocket.handshake.ServerHandshake;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doNothing;

class ErgebnisStompClientTest {
    private Consumer<TunierErgebnisNachricht> tunierEndeHandler;
    private ErgebnisStompClient client;

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(OffsetDateTime.class, new OffsetDateTimeAdapter())
            .create();


    @BeforeEach
    void setUp() throws Exception {
        tunierEndeHandler = mock(Consumer.class);
        client = Mockito.spy(new ErgebnisStompClient(42L,tunierEndeHandler));
        doNothing().when(client).send(any(String.class));
        doNothing().when(client).send(any(byte[].class));
        doNothing().when(client).close();
    }

    @Test
    void onOpen() {
        ServerHandshake handshake = mock(ServerHandshake.class);
        client.onOpen(handshake);
        assertNotNull(handshake);
        verify(client, atLeastOnce()).send(any(String.class));
    }

    @Test
    void onMessage() {
        HashMap<String, Integer> m = new HashMap<>();
        m.put("Alice",7);
        m.put("Bob",1);
        m.put("Charlie",2);
        TunierErgebnisNachricht n = new TunierErgebnisNachricht(42L,m,"Alice");
        String body = gson.toJson(n);
        String message = "MESSAGE\ndestination:/topic/spielraumendergebnis/42\n\n"+body+"\0";
        client.onMessage(message);
        verify(tunierEndeHandler, times(1)).accept(any(TunierErgebnisNachricht.class));
    }


    @Test
    void onClose() {
        client.onClose(1000,"Normal",false);
    }

    @Test
    void onError() {
        client.onError(new RuntimeException("Fehler beim Websocket aufgetreten"));
    }

//    @Test
//    void sendMessage() {
//        HashMap<String, Integer> m = new HashMap<>();
//        m.put("Alice",7);
//        m.put("Bob",1);
//        m.put("Charlie",2);
//        TunierErgebnisNachricht n = new TunierErgebnisNachricht(42L,m,"Alice");
//        client.sendMessage(n);
//        verify(client).send(any(byte[].class));
//    }

    @Test
    void disconnect() {
        client.disconnect();
        verify(client).send("DISCONNECT\n\n\0");
        verify(client).close();
    }






}