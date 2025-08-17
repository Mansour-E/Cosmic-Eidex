package cosmic.eidex.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cosmic.eidex.DTO.SpielraumDTO;
import cosmic.eidex.Service.SpielNachrichten.*;
import cosmic.eidex.spielmodell.Karte;
import org.java_websocket.handshake.ServerHandshake;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SpielraumStompClientTest {

    private SpielraumStompClient client;
    private Consumer<SpielraumDTO> updateHandler;
    private Consumer<SpielraumChatNachricht> chatMessageHandler;
    private Consumer<Karte> spielZugHandler;
    private Consumer<Long> spielStartHandler;
    private Consumer<Karte> spielTrumpfHandler;
    private Consumer<List<Karte>> spielAusteilHandler;
    private Consumer<Spielstatus> spielStatusHandler;
    private Consumer<Karte> spielDrueckHandler;
    private Consumer<List<Karte>> spielGueltigeHandler;
    private Consumer<InfoNachricht> infoHandler;
    private Consumer<Integer> rundePunkteHandler;
    private Consumer<Map<String, Integer>> partiePunkteHandler;

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(OffsetDateTime.class, new OffsetDateTimeAdapter())
            .create();

    @BeforeEach
    void setUp() throws Exception {
        updateHandler = mock(Consumer.class);
        chatMessageHandler = mock(Consumer.class);
        spielZugHandler = mock(Consumer.class);
        spielStartHandler = mock(Consumer.class);
        spielTrumpfHandler = mock(Consumer.class);
        spielAusteilHandler = mock(Consumer.class);
        spielStatusHandler = mock(Consumer.class);
        spielDrueckHandler = mock(Consumer.class);
        spielGueltigeHandler = mock(Consumer.class);
        infoHandler = mock(Consumer.class);
        rundePunkteHandler = mock(Consumer.class);
        partiePunkteHandler = mock(Consumer.class);


        client = spy(new SpielraumStompClient(
                42L,
                updateHandler,
                chatMessageHandler,
                spielZugHandler,
                spielStartHandler,
                spielTrumpfHandler,
                spielAusteilHandler,
                spielStatusHandler,
                spielDrueckHandler,
                spielGueltigeHandler,
                infoHandler,
                rundePunkteHandler,
                partiePunkteHandler


        ));
        doNothing().when(client).send(any(String.class));
        doNothing().when(client).send(any(byte[].class));
        doNothing().when(client).close();
    }



    @Test
    void testOnOpen() {
        ServerHandshake handshake = mock(ServerHandshake.class);
        client.onOpen(handshake);
        assertNotNull(handshake);
        verify(client, atLeastOnce()).send(any(String.class));
    }

    @Test
    void testOnMessageUpdateSpielraum() {
        SpielraumDTO dto = new SpielraumDTO();
        dto.setName("TestRaum");
        String body = gson.toJson(dto);
        String message = "MESSAGE\ndestination:/topic/imspielraum/42\n\n"+body+"\0";
        client.onMessage(message);
        verify(updateHandler, times(1)).accept(any(SpielraumDTO.class));
    }

    @Test
    void testOnMessageChatMessage() {
        SpielraumChatNachricht chat = new SpielraumChatNachricht(42L,"Alice","Hallo Welt");
        String body = gson.toJson(chat);
        String message = "MESSAGE\ndestination:/topic/spielraumchat/42\n\n"+body+"\0";
        client.onMessage(message);
        verify(chatMessageHandler, times(1)).accept(any(SpielraumChatNachricht.class));

    }

    @Test
    void testOnMessageSpielStart() {
        SpielraumStartNachricht msg = new SpielraumStartNachricht(42L,"Alice, Bob, Charlie");
        String body = gson.toJson(msg);
        String message = "MESSAGE\ndestination:/topic/spielraumstart/42\n\n"+body+"\0";
        client.onMessage(message);
        verify(spielStartHandler, times(1)).accept(42L);
    }

    @Test
    void testOnMessageSpielTrumpf() {
        TrumpfKarteNachricht msg = new TrumpfKarteNachricht(42L,new Karte("Stern","7"));
        String body = gson.toJson(msg);
        String message = "MESSAGE\ndestination:/topic/spielraumtrumpf/42\n\n"+body+"\0";
        client.onMessage(message);
        verify(spielTrumpfHandler).accept(any(Karte.class));
    }

    @Test
    void testOnMessageSpielStatus() {
        SpielStatusNachricht msg = new SpielStatusNachricht(42L,Spielstatus.WARTET_AUF_SPIELZUEGE);
        String body = gson.toJson(msg);
        String message = "MESSAGE\ndestination:/topic/spielraumstatus/42\n\n"+body+"\0";
        client.onMessage(message);
        verify(spielStatusHandler).accept(eq(Spielstatus.WARTET_AUF_SPIELZUEGE));
    }

    @Test
    void testOnMessageSpielraumzug() {
        Karte k = new Karte("Stern","8");
        SpielzugNachricht n = new SpielzugNachricht(42L,"Bob", k );
        String body = gson.toJson(n);
        String m = "MESSAGE\ndestination:/topic/spielraumzug/42\n\n"+body+"\0";
        try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class)) {
            sessionMock.when(SessionManager::getNickname).thenReturn("Alice");
            client.onMessage(m);
            verify(spielZugHandler).accept(eq(n.getGespielteKarte()));
        }

    }

    @Test
    void testOnMessageSpielraumAusteilen() {
        List<Karte> karten = List.of(new Karte("Stern","8"),new Karte("Eidex","7"));
        KartenAusgeteiltNachricht nachricht = new KartenAusgeteiltNachricht(42L,"Alice",karten);
        String body = gson.toJson(nachricht);
        String m = "MESSAGE\ndestination:/topic/spielraumausteilen/42\n\n"+body+"\0";
        try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class)) {
            sessionMock.when(SessionManager::getNickname).thenReturn("Alice");
            client.onMessage(m);
            verify(spielAusteilHandler).accept(eq(karten));
        }
    }

    @Test
    void testOnMessageSpielraumDruecken() {
        KartenGedruecktNachricht n = new KartenGedruecktNachricht(42L,"Alice",new Karte("Stern","8"));
        String body = gson.toJson(n);
        String m = "MESSAGE\ndestination:/topic/spielraumdruecken/42\n\n"+body+"\0";
        try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class)) {
            sessionMock.when(SessionManager::getNickname).thenReturn("Alice");
            client.onMessage(m);
            verify(spielDrueckHandler).accept(eq(n.getGedrueckteKarte()));
        }

    }

    @Test
    void testOnMessageSpielraumGueltige() {
        List<Karte> gueltige = List.of(new Karte("Stern","8"));
        GueltigeKartenNachricht n = new GueltigeKartenNachricht(42L,"Alice",gueltige);
        String body = gson.toJson(n);
        String m = "MESSAGE\ndestination:/topic/spielraumgueltige/42\n\n"+body+"\0";
        try (MockedStatic<SessionManager> sessionMock = mockStatic(SessionManager.class)) {
            sessionMock.when(SessionManager::getNickname).thenReturn("Alice");
            client.onMessage(m);
            verify(spielGueltigeHandler).accept(eq(gueltige));
        }

    }

    @Test
    void testOnMessageSpielraumInfo() {
        InfoNachricht info = new InfoNachricht(42L,"Hinweis","Wichtige Info");
        String body = gson.toJson(info);
        String message = "MESSAGE\ndestination:/topic/spielrauminfo/42\n\n"+body+"\0";
        client.onMessage(message);
        ArgumentCaptor<InfoNachricht> captor = ArgumentCaptor.forClass(InfoNachricht.class);
        verify(infoHandler).accept(captor.capture());
        InfoNachricht actual = captor.getValue();
        assertEquals(info.getRaumId(), actual.getRaumId());
        assertEquals(info.getInfoTyp(), actual.getInfoTyp());
        assertEquals(info.getInfo(), actual.getInfo());
    }

    @Test
    void testOnMessageSpielraumRundePunkte() {

    }

    @Test
    void testOnMessageSpielraumPartiePunkte() {
        Map<String,Integer> punkte = Map.of("Alice",100,"Bob",80);
        PartiePunkteNachricht m = new PartiePunkteNachricht(42L,punkte);
        String body = gson.toJson(m);
        String message = "MESSAGE\ndestination:/topic/spielraumpartiepunkte/42\n\n"+body+"\0";
        client.onMessage(message);
        verify(partiePunkteHandler).accept(eq(punkte));
    }

    @Test
    void testOnMessageInvalid() {
        String brokenMessage = "MESSAGE\ninvalid-header:xyz\n\ninvalidbody\0";
        client.onMessage(brokenMessage);
    }

    @Test
    void testOnClose() {
        client.onClose(1000,"Normal",false);
    }

    @Test
    void testOnError() {
        client.onError(new RuntimeException("Fehler beim Websocket aufgetreten"));
    }

    @Test
    void testSendMessageChatMessage() {
        SpielraumChatNachricht nachricht = new SpielraumChatNachricht(42L,"Alice","Hallo");
        client.sendMessage(nachricht);
        verify(client, times(1)).send(any(byte[].class));
    }

    @Test
    void testSendMessageSpielraumZug() {
        SpielzugNachricht n = new SpielzugNachricht(42L,"Alice",new Karte("Eidex", "6"));
        client.sendMessage(n);
        verify(client, times(1)).send(any(byte[].class));

    }

    @Test
    void testSendMessageSpielraumDruecken() {
        KartenGedruecktNachricht n = new KartenGedruecktNachricht(42L,"Alice",new Karte("Eidex", "6"));
        client.sendMessage(n);
        verify(client, times(1)).send(any(byte[].class));
    }

    @Test
    void testSendMessageSpielraumStart() {
        SpielraumStartNachricht n = new SpielraumStartNachricht(42L,"Alice");
        client.sendMessage(n);
        verify(client, times(1)).send(any(byte[].class));
    }


    @Test
    void testDisconnect() {
        client.disconnect();
        verify(client).send("DISCONNECT\n\n\0");
        verify(client).close();
    }

}