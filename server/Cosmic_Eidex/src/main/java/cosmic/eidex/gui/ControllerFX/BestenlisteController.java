package cosmic.eidex.gui.ControllerFX;

import cosmic.eidex.Service.StompClient;
import cosmic.eidex.spielmodell.Spieler;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * GUI-Controller für die Bestenliste.
 * Verwaltet die Anzeige der Top-10-Spieler und Kommunikation via WebSocket.
 */
public class BestenlisteController {

    @FXML
    protected ListView<String> bestenliste;

    protected StompClient stompClient;

    /**
     * Initialisiert den Controller.
     * Baut eine WebSocket-Verbindung auf und fordert die Bestenliste vom Server an.
     */
    @FXML
    public void initialize() {
        connectToWebSocket();
        Platform.runLater(() -> stompClient.requestBestenliste());
    }

    /**
     * Baut die WebSocket-Verbindung zum Server auf.
     */
    protected void connectToWebSocket() {
        try {
            URI uri = new URI("ws://localhost:8080/bestenliste");
            stompClient = new StompClient(
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
            stompClient.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Schließt das Fenster der Bestenliste.
     * Wird aufgerufen, wenn der Benutzer das Fenster schließen möchte.
     */
    @FXML
    private void handleSchliessen() {
        Stage stage = (Stage) bestenliste.getScene().getWindow();
        stage.close();
    }

}
