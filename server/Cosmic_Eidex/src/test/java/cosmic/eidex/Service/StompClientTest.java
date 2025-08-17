package cosmic.eidex.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cosmic.eidex.Lobby.Nachricht;
import cosmic.eidex.spielmodell.Spieler;
import org.java_websocket.handshake.ServerHandshake;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

class StompClientTest {

    //Testversion des StompClients
    private static class TestableStompClient extends StompClient {
        final List<String> sentFrames = new ArrayList<>();
        boolean closed = false;
        public TestableStompClient(Consumer<Nachricht> chat, Consumer<List<Spieler>> best,Consumer<List<String>> rooms) {
            super(URI.create("ws://test"), chat, best, rooms);
        }
        @Override
        public void send(String message) {
            sentFrames.add(message);
        }
        @Override
        public void send(byte[] bytes) {
            sentFrames.add(new String(bytes));
        }
        @Override
        public void connect() {}
        @Override
        public void close() {closed = true;}
    }

    private TestableStompClient testClient;
    private final List<Nachricht> chatbox = new ArrayList<>();
    private final List<List<Spieler>> bestenlisten = new ArrayList<>();
    private final List<List<String>> spielraumLists = new ArrayList<>();

    private final Gson gson = new GsonBuilder().registerTypeAdapter(OffsetDateTime.class, new OffsetDateTimeAdapter()).create();

    @BeforeEach
    void setUp() {
        chatbox.clear();
        bestenlisten.clear();
        spielraumLists.clear();
        testClient = new TestableStompClient(
                chatbox::add,
                bestenlisten::add,
                spielraumLists::add);

    }
    @Test
    void testOnOpen() {
        testClient.onOpen(new ServerHandshake() {
            @Override public String getHttpStatusMessage() {return "OK";}
            @Override public short getHttpStatus() { return 101;}
            @Override public String getFieldValue(String name) {return null;}
            @Override public boolean hasFieldValue(String name) {return false;}
            @Override public byte[] getContent() {return new byte[0];}
            @Override public Iterator<String> iterateHttpFields() {return null;}
        });
        List<String> frames = testClient.sentFrames;
        assertEquals(4,frames.size());
        assertTrue(frames.getFirst().startsWith("CONNECT"));
        assertEquals("SUBSCRIBE\nid:sub-0\ndestination:/topic/messages\n\n\0", frames.get(1));
        assertEquals("SUBSCRIBE\nid:sub-1\ndestination:/topic/bestenliste\n\n\0", frames.get(2));
        assertEquals("SUBSCRIBE\nid:sub-2\ndestination:/topic/spielraum\n\n\0", frames.get(3));
    }

    @Test
    void testOnMessage() {
        Nachricht msg = new Nachricht("Alice","Hallo");
        String body = gson.toJson(msg);
        String raw = "MESSAGE\ndestination:/topic/messages\n\n" + body + "\0";
        testClient.onMessage(raw);
        assertEquals(1, chatbox.size());
        assertEquals("Alice", chatbox.getFirst().getSender());
        assertEquals("Hallo", chatbox.getFirst().getInhalt());
    }

    @Test
    void testOnMessageMitSpielraumListe() {
        String body = gson.toJson(new String[]{"Raum-A","Raum-B"});
        String raw = "MESSAGE\ndestination:/topic/spielraum\n\n" + body + "\0";
        testClient.onMessage(raw);
        assertEquals(1, spielraumLists.size());
        assertEquals(List.of("Raum-A","Raum-B"), spielraumLists.getFirst());
    }

    @Test
    void testOnMessageMitBestenliste() {
        String body = """
            [{"nickname":"Alice","punkte":123},
             {"nickname":"Bob","punkte":99}]""";

        String raw = "MESSAGE\ndestination:/topic/bestenliste\n\n" + body + "\0";
        testClient.onMessage(raw);
        assertEquals(1, bestenlisten.size());
        assertEquals(2, bestenlisten.getFirst().size());
        assertEquals("Alice", bestenlisten.getFirst().getFirst().getNickname());
    }

    @Test
    void testSendMessage() {
        Nachricht msg = new Nachricht("Alice","Hallo");
        testClient.sendMessage(msg);
        assertEquals(1, testClient.sentFrames.size());
        String frame = testClient.sentFrames.getFirst();
        assertTrue(frame.startsWith("SEND\ndestination:/app/chat"));
        assertTrue(frame.endsWith("\0"));
        String payload = frame.split("\n\n",2)[1].replace("\0","");
        Nachricht nachrichtRoundtrip = gson.fromJson(payload, Nachricht.class);
        assertEquals("Alice", nachrichtRoundtrip.getSender());
    }

    @Test
    void requestBestenliste() {
        testClient.requestBestenliste();
        assertEquals("SEND\ndestination:/app/bestenliste/aktualisieren\n\n\0", testClient.sentFrames.getFirst());
    }

    @Test
    void requestSpielraeume() {
        testClient.requestSpielraeume();
        assertEquals("SEND\ndestination:/app/spielraum/spielraumnamen\n\n\0", testClient.sentFrames.getFirst());
    }

    @Test
    void onClose() {
        assertDoesNotThrow(() -> testClient.onClose(1000,"normal",true));
    }

    @Test
    void disconnect() {
        testClient.disconnect();
        assertFalse(testClient.sentFrames.isEmpty());
        assertTrue(testClient.sentFrames.getFirst().startsWith("DISCONNECT"));
        assertTrue(testClient.sentFrames.getFirst().endsWith("\0"));
        assertTrue(testClient.closed);
    }

    @Test
    void onError() {
        RuntimeException error = new RuntimeException("Test");
        assertDoesNotThrow(() -> testClient.onError(error));
    }
}