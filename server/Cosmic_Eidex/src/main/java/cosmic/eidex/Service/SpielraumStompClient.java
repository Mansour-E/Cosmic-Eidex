package cosmic.eidex.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cosmic.eidex.DTO.SpielraumDTO;
import cosmic.eidex.Service.SpielNachrichten.*;
import cosmic.eidex.spielmodell.Karte;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * WebSocket-Client für die Kommunikation mit dem Spielraum über STOMP-Protokoll.
 * <p>
 * Dieser Client abonniert 12 Kanäle:
 * <ul>
 *     <li><code>/topic/imspielraum/{raumId}</code> für Spielraum-Updates</li>
 *     <li><code>/topic/spielraumchat/{raumId}</code> für Chatnachrichten</li>
 *     <li><code>/topic/spielraumzug/{raumId}</code> für Spielraum-Karten ausspielen</li>
 *     <li><code>/topic/spielraumstart/{raumId}</code> für Start des Spieles</li>
 *     <li><code>/topic/spielraumtrumpf/{raumId}</code> für Trumpfkarte im Spielraum</li>
 *     <li><code>/topic/spielraumausteilen/{raumId}</code> für Handkarten nach dem Austeilen</li>
 *     <li><code>/topic/spielraumstatus/{raumId}</code> für Spielraum-Status Aenderungen</li>
 *     <li><code>/topic/spielraumdruecken/{raumId}</code> für Gedrueckte Karten</li>
 *     <li><code>/topic/spielraumgueltige/{raumId}</code> für Liste an gueltigen Karten</li>
 *     <li><code>/topic/spielrauminfo/{raumId}</code> für Informationen an den Spielraum Frontend</li>
 *     <li><code>/topic/spielraumrundepunkte/{raumId}</code> für Die Punkte die in einer Runde erzielt wurden</li>
 *     <li><code>/topic/spielraumpartiepunkte/{raumId}</code> für Punkte die in einer Partie erzielt wurden</li>
 * </ul>
 * Er verarbeitet eingehende Nachrichten und sendet Chatnachrichten an den Server.
 */
public class SpielraumStompClient extends WebSocketClient {

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(OffsetDateTime.class, new OffsetDateTimeAdapter())
            .create();
    private final Long raumId;
    private final Consumer<SpielraumDTO> updateHandler;
    private final Consumer<SpielraumChatNachricht> chatMessageHandler;
    private final Consumer<Karte> spielZugHandler;
    private final Consumer<Karte> spielTrumpfHandler;
    private final Consumer<List<Karte>> spielAusteilHandler;
    private final Consumer<Long> spielStartHandler;
    private final Consumer<Spielstatus> spielStatusHandler;
    private final Consumer<Karte> spielDrueckHandler;
    private final Consumer<List<Karte>> spielGueltigeHandler;
    private final Consumer<InfoNachricht> infoHandler;
    private final Consumer<Integer> rundePunkteHandler;
    private final Consumer<Map<String, Integer>> partiePunkteHandler;


    public SpielraumStompClient(Long raumId, Consumer<SpielraumDTO> updateHandler,
                                Consumer<SpielraumChatNachricht> chatMessageHandler,
                                Consumer<Karte> spielZugHandler,
                                Consumer<Long> spielStartHandler,
                                Consumer<Karte> spielTrumpfHandler,
                                Consumer<List<Karte>> spielAusteilHandler,
                                Consumer<Spielstatus> spielStatusHandler,
                                Consumer<Karte> spielDrueckHandler,
                                Consumer<List<Karte>> spielGueltigeHandler,
                                Consumer<InfoNachricht> infoHandler,
                                Consumer<Integer> rundePunkteHandler,
                                Consumer<Map<String, Integer>> partiePunkteHandler
                                ) throws Exception {
        super(new URI("ws://localhost:8080/imspielraum"));
        this.raumId = raumId;
        this.updateHandler = updateHandler;
        this.chatMessageHandler = chatMessageHandler;
        this.spielZugHandler = spielZugHandler;
        this.spielStartHandler = spielStartHandler;
        this.spielTrumpfHandler = spielTrumpfHandler;
        this.spielAusteilHandler = spielAusteilHandler;
        this.spielStatusHandler = spielStatusHandler;
        this.spielDrueckHandler = spielDrueckHandler;
        this.spielGueltigeHandler = spielGueltigeHandler;
        this.infoHandler = infoHandler;
        this.rundePunkteHandler = rundePunkteHandler;
        this.partiePunkteHandler = partiePunkteHandler;
    }

    /**
     * Wird aufgerufen, wenn die WebSocket-Verbindung erfolgreich geöffnet wurde.
     *
     * @param handshakedata Handshake-Daten vom Server
     */
    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("[Spielraum] WebSocket verbunden.");
        send("CONNECT\naccept-version:1.2\n\n\0");

        String destination = "/topic/imspielraum/" + raumId;
        String subId = "sub-imspielraum-" + raumId;
        send("SUBSCRIBE\nid:" + subId + "\ndestination:" + destination + "\n\n\0");

        String chatDestination = "/topic/spielraumchat/" + raumId;
        String chatSubId = "sub-spielraumchat-" + raumId;
        send("SUBSCRIBE\nid:" + chatSubId + "\ndestination:" + chatDestination + "\n\n\0");

        String spielZugDestination = "/topic/spielraumzug/" + raumId;
        String spielZugSubId = "sub-spielraumzug-" + raumId;
        send("SUBSCRIBE\nid:" + spielZugSubId + "\ndestination:" + spielZugDestination + "\n\n\0");

        String spielStartDestination = "/topic/spielraumstart/" + raumId;
        String spielStartSubId = "sub-spielraumstart-" + raumId;
        send("SUBSCRIBE\nid:" + spielStartSubId + "\ndestination:" + spielStartDestination + "\n\n\0");

        String spielTrumpfDestination = "/topic/spielraumtrumpf/" + raumId;
        String spielTrumpfSubId = "sub-spielraumtrumpf-" + raumId;
        send("SUBSCRIBE\nid:" + spielTrumpfSubId + "\ndestination:" + spielTrumpfDestination + "\n\n\0");

        String spielAusteilDestination = "/topic/spielraumausteilen/" + raumId;
        String spielAusteilSubId = "sub-spielraumausteilen-" + raumId;
        send("SUBSCRIBE\nid:" + spielAusteilSubId + "\ndestination:" + spielAusteilDestination + "\n\n\0");

        String spielStatusDestination = "/topic/spielraumstatus/" + raumId;
        String spielStatusSubId = "sub-spielraumstatus-" + raumId;
        send("SUBSCRIBE\nid:" + spielStatusSubId + "\ndestination:" + spielStatusDestination + "\n\n\0");

        String spielDrueckenDestination = "/topic/spielraumdruecken/" + raumId;
        String spielDrueckenSubId = "sub-spielraumdruecken-" + raumId;
        send("SUBSCRIBE\nid:" + spielDrueckenSubId + "\ndestination:" + spielDrueckenDestination + "\n\n\0");

        String spielGueltigeDestination = "/topic/spielraumgueltige/" + raumId;
        String spielGueltigeSubId = "sub-spielraumgueltige-" + raumId;
        send("SUBSCRIBE\nid:" + spielGueltigeSubId + "\ndestination:" + spielGueltigeDestination + "\n\n\0");

        String spielInfoDestination = "/topic/spielrauminfo/" + raumId;
        String spielInfoSubId = "sub-spielrauminfo-" + raumId;
        send("SUBSCRIBE\nid:" + spielInfoSubId + "\ndestination:" + spielInfoDestination + "\n\n\0");

        String spielRundePunkteDestination = "/topic/spielraumrundepunkte/" + raumId;
        String spielRundePunkteSubId = "sub-spielraumrundepunkte-" + raumId;
        send("SUBSCRIBE\nid:" + spielRundePunkteSubId + "\ndestination:" + spielRundePunkteDestination + "\n\n\0");

        String spielPartiePunkteDestination = "/topic/spielraumpartiepunkte/" + raumId;
        String spielPartiePunkteSubId = "sub-spielraumpartiepunkte-" + raumId;
        send("SUBSCRIBE\nid:" + spielPartiePunkteSubId + "\ndestination:" + spielPartiePunkteDestination + "\n\n\0");
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
            if(destination.startsWith("/topic/imspielraum/")){
                SpielraumDTO dto = gson.fromJson(body, SpielraumDTO.class);
                System.out.println(dto.getName() + " Spielraum empfangen" + body);
                updateHandler.accept(dto);
            }
            else if(destination.startsWith("/topic/spielraumchat/")){
                SpielraumChatNachricht chatNachricht = gson.fromJson(body, SpielraumChatNachricht.class);
                chatMessageHandler.accept(chatNachricht);
            }
            else if(destination.startsWith("/topic/spielraumzug/")){
                SpielzugNachricht spielzugNachricht = gson.fromJson(body, SpielzugNachricht.class);
                if (!spielzugNachricht.getSpieler().equals(SessionManager.getNickname())) {
                    spielZugHandler.accept(spielzugNachricht.getGespielteKarte());
                }
            }
            else if(destination.startsWith("/topic/spielraumstart/")){
                SpielraumStartNachricht startNachricht = gson.fromJson(body, SpielraumStartNachricht.class);
                System.out.println("SpielraumStartNachricht empfangen: Raum-ID: " + startNachricht.getRaumId()
                        + ", Spieler: " + startNachricht.getSpieler());
                spielStartHandler.accept(startNachricht.getRaumId());
            }
            else if(destination.startsWith("/topic/spielraumtrumpf/")) {
                TrumpfKarteNachricht trumpfNachricht = gson.fromJson(body, TrumpfKarteNachricht.class);
                spielTrumpfHandler.accept(trumpfNachricht.getTrumpfKarte());
            }
            else if(destination.startsWith("/topic/spielraumausteilen/")) {
                System.out.println("Karten werden verteilt im Stomp");
                KartenAusgeteiltNachricht austeilNachricht = gson.fromJson(body, KartenAusgeteiltNachricht.class);
                if (austeilNachricht.getEmpfaenger().equals(SessionManager.getNickname())) {
                    System.out.println("karten werden an spieler ausgegeben");
                    spielAusteilHandler.accept(austeilNachricht.getHandkarten());
                }
            }
            else if(destination.startsWith("/topic/spielraumstatus/")) {
                SpielStatusNachricht statusNachricht = gson.fromJson(body, SpielStatusNachricht.class);
                spielStatusHandler.accept(statusNachricht.getStatus());
            }
            else if(destination.startsWith("/topic/spielraumdruecken/")) {
                KartenGedruecktNachricht drueckNachricht = gson.fromJson(body, KartenGedruecktNachricht.class);
                if (drueckNachricht.getSpieler().equals(SessionManager.getNickname())) {
                    spielDrueckHandler.accept(drueckNachricht.getGedrueckteKarte());
                }
            }
            else if(destination.startsWith("/topic/spielraumgueltige/")) {
                GueltigeKartenNachricht nachricht = gson.fromJson(body, GueltigeKartenNachricht.class);
                if(nachricht.getSpieler().equals(SessionManager.getNickname())) {
                    System.out.println("Gueltige Karten: " + nachricht.getGueltigeKarten().size());
                    for(Karte karte1 : nachricht.getGueltigeKarten()){
                        System.out.println(karte1.getFarbe() + karte1.getWert());
                    }
                    spielGueltigeHandler.accept(nachricht.getGueltigeKarten());
                }
            }
            else if(destination.startsWith("/topic/spielrauminfo/")) {
                InfoNachricht info = gson.fromJson(body, InfoNachricht.class);
                infoHandler.accept(info);
            }
            else if(destination.startsWith("/topic/spielraumrundepunkte/")) {
                RundenPunkteNachricht rundePunkteNachricht = gson.fromJson(body, RundenPunkteNachricht.class);
                if (rundePunkteNachricht.getSpieler().equals(SessionManager.getNickname())) {
                    rundePunkteHandler.accept(rundePunkteNachricht.getPunkte());
                }
            }

            else if(destination.startsWith("/topic/spielraumpartiepunkte/")) {
                PartiePunkteNachricht partiePunkteNachricht = gson.fromJson(body, PartiePunkteNachricht.class);
                partiePunkteHandler.accept(partiePunkteNachricht.getPartiePunkte());
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
     * Sendet eine Chatnachricht über den WebSocket-Kanal an den Server.
     *
     * @param nachricht Die zu sendende Nachricht
     */
    public void sendMessage(SpielraumNachricht nachricht) {
        String json = gson.toJson(nachricht);
        System.out.println("Sending JSON: " + json);
        String destination;

        if (nachricht instanceof SpielraumChatNachricht) {
            destination = "/app/spielraumchat/" + nachricht.getRaumId();
        } else if (nachricht instanceof SpielzugNachricht) {
            destination = "/app/spielraumzug/" + nachricht.getRaumId();
        } else if (nachricht instanceof SpielraumStartNachricht) {
        destination = "/app/spielraumstart/" + nachricht.getRaumId();
        } else if (nachricht instanceof KartenGedruecktNachricht) {
            destination = "/app/spielraumdruecken/" + nachricht.getRaumId();
        } else {
            throw new IllegalArgumentException("Unbekannter Nachrichtentyp: " + nachricht.getClass().getName());
        }

        String stompFrame = "SEND\ndestination:" + destination + "\n\n" + json + "\0";
        send(stompFrame.getBytes(StandardCharsets.UTF_8));

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
