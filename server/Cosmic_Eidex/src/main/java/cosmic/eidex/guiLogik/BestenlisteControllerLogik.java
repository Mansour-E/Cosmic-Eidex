package cosmic.eidex.guiLogik;

import cosmic.eidex.Service.StompClient;
import cosmic.eidex.gui.ControllerFX.BestenlisteController;
import cosmic.eidex.spielmodell.Spieler;
import javafx.application.Platform;
import javafx.scene.control.ListView;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class BestenlisteControllerLogik extends BestenlisteController {
    public void setStompClient(StompClient stompClient) {
        this.stompClient = stompClient;
    }
    public StompClient getStompClient() {
        return stompClient;
    }
    public void setBestenliste(ListView<String> bestenliste) {
        this.bestenliste = bestenliste;
    }
    /**
     *
     * Initialisiert den Controller.
     * Baut eine WebSocket-Verbindung auf und fordert die Bestenliste vom Server an.
     */
    public void initialize() {
        connectToWebSocket();
        Platform.runLater(() -> stompClient.requestBestenliste());
    }

    /**
     * Baut die WebSocket-Verbindung zum Server auf.
     */
    //Factory Methode zum Testen
    protected StompClient createStompClient() throws Exception {
        URI uri = new URI("ws://localhost:8080/bestenliste");
        return new StompClient(
                uri,
                message -> Platform.runLater(() -> System.out.println(message)),
                top10Liste -> Platform.runLater(() -> {
                    List<String> eintraege = new ArrayList<>();
                    for (int i = 0; i < top10Liste.size(); i++) {
                        Spieler s = top10Liste.get(i);
                        eintraege.add((i + 1) + ". " + s.getNickname() + " – Siege: " + s.getSiege());
                    }
                    bestenliste.getItems().setAll(eintraege);
                }),
                spielraumNamen -> Platform.runLater(() -> {
                    System.out.println(spielraumNamen);
                })
        );
    }
    protected void connectToWebSocket() {
        try {
            stompClient = createStompClient();
            stompClient.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
