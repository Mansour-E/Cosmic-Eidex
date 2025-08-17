package cosmic.eidex.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cosmic.eidex.Service.SpielNachrichten.PartiePunkteNachricht;
import cosmic.eidex.Service.SpielNachrichten.SpielraumNachricht;
import cosmic.eidex.Service.SpielNachrichten.TunierErgebnisNachricht;
import cosmic.eidex.spielmodell.Spieler;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.function.Consumer;

/**
 * WebSocket-Client für die Kommunikation mit dem Ergebnis Screen über STOMP-Protokoll.
 * <p>
 * Dieser Client abonniert 1 Kanal:
 * <ul>
 *     <li><code>/topic/spielraumendergebnis/{raumId}</code> für die Ergebnisse eines ganzen Tuniers</li>
 * </ul>
 * Er verarbeitet eingehende Nachrichten und sendet Chatnachrichten an den Server.
 */
public class ErgebnisStompClient extends WebSocketClient {

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(OffsetDateTime.class, new OffsetDateTimeAdapter())
            .create();
    private final Long raumId;
    private final Consumer<TunierErgebnisNachricht> TunierEndeHandler;

    public ErgebnisStompClient(Long raumId,
                               Consumer<TunierErgebnisNachricht> TunierEndeHandler
                               )throws Exception{
        super(new URI("ws://localhost:8080/ende"));
        this.raumId = raumId;
        this.TunierEndeHandler = TunierEndeHandler;
    }

    /**
     * Wird aufgerufen, wenn die WebSocket-Verbindung erfolgreich geöffnet wurde.
     *
     * @param handshakedata Handshake-Daten vom Server
     */
    @Override
    public void onOpen(ServerHandshake handshakedata) {
        send("CONNECT\naccept-version:1.2\n\n\0");

        String spielEndeDestination = "/topic/spielraumendergebnis/" + raumId;
        String spielEndeSubId = "sub-spielraumendergebnis-" + raumId;
        send("SUBSCRIBE\nid:" + spielEndeSubId + "\ndestination:" + spielEndeDestination + "\n\n\0");
    }

    /**
     * Wird aufgerufen, wenn eine Nachricht empfangen wurde.
     *
     * @param message Die empfangene Nachricht
     */
    @Override
    public void onMessage(String message) {
        if (message.contains("MESSAGE")) {
            String destination = extractHeader(message, "destination");
            String body = message.split("\n\n", 2)[1].replace("\u0000", "");

            if (destination.startsWith("/topic/spielraumendergebnis/")) {
                TunierErgebnisNachricht nachricht = gson.fromJson(body, TunierErgebnisNachricht.class);
                TunierEndeHandler.accept(nachricht);
            }
        }
    }

    /**
     * Wird aufgerufen, wenn die WebSocket-Verbindung geschlossen wird.
     *
     * @param code   Schließcode
     * @param reason Grund für das Schließen
     * @param remote Ob das Schließen vom Server initiiert wurde
     */
    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("[Spielraum] WebSocket geschlossen: " + reason);
    }

    /**
     * Wird aufgerufen, wenn ein Fehler in der WebSocket-Verbindung auftritt.
     *
     * @param ex Die aufgetretene Exception
     */
    @Override
    public void onError(Exception ex) {
        System.err.println("[Spielraum] WebSocket Fehler:");
        ex.printStackTrace();
    }

    /**
     * Trennt die Verbindung zum WebSocket.
     */
    public void disconnect() {
        send("DISCONNECT\n\n\0");
        close();
        System.out.println("[Spielraum] WebSocket disconnected ");
    }

    /**
     * Hilfsmethode zur Extraktion eines Headers aus einem STOMP-Frame.
     *
     * @param message    Das komplette STOMP-Message-String
     * @param headerName Der Name des Headers
     * @return Der extrahierte Header-Wert oder leerer String, falls nicht gefunden
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
}
