package cosmic.eidex.Service;

import com.google.gson.*;
import cosmic.eidex.Lobby.Nachricht;
import cosmic.eidex.Lobby.Spielraum;
import cosmic.eidex.spielmodell.Spieler;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * Ein WebSocket-STOMP-Client für die Kommunikation mit dem Server über verschiedene Themen (Topics).
 */
public class StompClient extends WebSocketClient {

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(OffsetDateTime.class, new OffsetDateTimeAdapter())
            .create();
    private final Consumer<Nachricht> chatMessageHandler;
    private final Consumer<List<Spieler>> bestenlisteHandler;
    private final Consumer<List<String>> spielraumHandler;

    public StompClient(URI serverUri, Consumer<Nachricht> chatHandler, Consumer<List<Spieler>> bestenlisteHandler, Consumer<List<String>> spielraumHandler) {
        super(serverUri);
        this.chatMessageHandler = chatHandler;
        this.bestenlisteHandler = bestenlisteHandler;
        this.spielraumHandler = spielraumHandler;
    }

    /**
     * Wird aufgerufen, wenn die WebSocket-Verbindung geöffnet wurde.
     * Abonniert alle relevanten Topics.
     *
     * @param handshakedata Die Handshake-Daten vom Server
     */
    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Connected to WebSocket");
        send("CONNECT\naccept-version:1.2\n\n\0");

        // Abonniere globalen Chat
        send("SUBSCRIBE\nid:sub-0\ndestination:/topic/messages\n\n\0");

        // Abonniere Bestenliste
        send("SUBSCRIBE\nid:sub-1\ndestination:/topic/bestenliste\n\n\0");

        // Abonniere Spielräume
        send("SUBSCRIBE\nid:sub-2\ndestination:/topic/spielraum\n\n\0");
    }

    /**
     * Verarbeitet eingehende STOMP-Messages und ruft die zugehörigen Handler auf.
     *
     * @param message Die empfangene Nachricht
     */
    @Override
    public void onMessage(String message) {
        if (message.contains("MESSAGE")) {
            String destination = extractHeader(message, "destination");
            String body = message.split("\n\n", 2)[1].replace("\u0000", "");
            if ("/topic/messages".equals(destination)) {
                Nachricht nachricht = gson.fromJson(body, Nachricht.class);
                System.out.println("Chat-Nachricht empfangen: " + body);
                chatMessageHandler.accept(nachricht);

            } else if ("/topic/bestenliste".equals(destination)) {
                Spieler[] spielerArray = gson.fromJson(body, Spieler[].class);
                List<Spieler> top10 = Arrays.asList(spielerArray);
                System.out.println("Bestenliste empfangen: " + top10);
                bestenlisteHandler.accept(top10);
            }else if ("/topic/spielraum".equals(destination)) {
                String[] spielraumNamen = gson.fromJson(body, String[].class);
                List<String> namen = Arrays.asList(spielraumNamen);
                System.out.println("Spielraeume empfangen: " + namen);
                spielraumHandler.accept(namen);
            }
        }
    }

    /**
     * Extrahiert einen Header-Wert aus einer STOMP-Nachricht.
     *
     * @param message    Die gesamte STOMP-Nachricht
     * @param headerName Der gesuchte Header-Name
     * @return Der extrahierte Wert oder ein leerer String, falls nicht gefunden
     */
    private String extractHeader(String message, String headerName) {
        String[] lines = message.split("\n");
        for (String line : lines) {
            if (line.startsWith(headerName + ":")) {
                return line.substring((headerName + ":").length());
            }
        }
        return "";
    }


    /**
     * Sendet eine Chatnachricht an den Server über den <code>/app/chat</code>-Endpunkt.
     *
     * @param nachricht Die zu sendende Nachricht
     */
    public void sendMessage(Nachricht nachricht) {
        String json = gson.toJson(nachricht);
        System.out.println("Sending JSON: " + json);
        String stompFrame = "SEND\ndestination:/app/chat\n\n" + json + "\0";
        send(stompFrame.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Fordert die aktuelle Bestenliste vom Server an.
     */
    public void requestBestenliste() {
        String stompFrame = "SEND\ndestination:/app/bestenliste/aktualisieren\n\n\0";
        send(stompFrame.getBytes(StandardCharsets.UTF_8));
        System.out.println("Bestenliste angefordert");
    }

    /**
     * Fordert die Liste der verfügbaren Spielräume vom Server an.
     */
    public void requestSpielraeume() {
        String stompFrame = "SEND\ndestination:/app/spielraum/spielraumnamen\n\n\0";
        send(stompFrame.getBytes(StandardCharsets.UTF_8));
        System.out.println("Spielraeume angefordert");
    }

    /**
     * Wird aufgerufen, wenn die WebSocket-Verbindung geschlossen wurde.
     *
     * @param code   Der Statuscode für das Schließen
     * @param reason Der Grund für das Schließen
     * @param remote Ob das Schließen vom Server initiiert wurde
     */
    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("WebSocket closed: " + reason);
    }

    /**
     * Trennt den Client vom Server und sendet ein DISCONNECT-Frame.
     */
    public void disconnect() {
        send("DISCONNECT\n\n\0");
        close();
        System.out.println("WebSocket disconnected ");
    }

    /**
     * Wird aufgerufen, wenn ein Fehler während der WebSocket-Kommunikation auftritt.
     *
     * @param ex Die aufgetretene Ausnahme
     */
    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }
}
