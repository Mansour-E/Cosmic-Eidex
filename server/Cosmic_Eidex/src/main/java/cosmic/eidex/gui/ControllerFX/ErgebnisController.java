package cosmic.eidex.gui.ControllerFX;

import cosmic.eidex.Service.ErgebnisStompClient;
import cosmic.eidex.Service.SpielNachrichten.TunierErgebnisNachricht;
import cosmic.eidex.Service.SpielraumStompClient;
import cosmic.eidex.gui.StageManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * GUI-Controller für die Ergebnisanzeige nach einem Spiel.
 */
@Component
public class ErgebnisController {

    @FXML
    private GridPane ergebnisGrid;

    protected final StageManager stageManager;
    protected ErgebnisStompClient stompClient;
    protected Long raumId;

    /**
     * Initialisiert den Controller. Wird automatisch nach dem Laden des FXML aufgerufen.
     */
    @FXML
    public void initialize() {
    }

    /**
     * Setzt die Spielraum-ID.
     * @param id Raum-ID
     */
    public void setRaumId(Long id) {
        this.raumId = id;
        if (stompClient != null) {
            stompClient.disconnect();
        }
        try {
            stompClient = new ErgebnisStompClient(id,
                    this::handleErgebnisse
            );
            stompClient.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Konstruktor für den ErgebnisController.
     * @param stageManager Um JavaFX Szenen zu steuern
     */
    @Autowired
    public ErgebnisController(StageManager stageManager) {
        this.stageManager = stageManager;
    }

    public void handleErgebnisse(TunierErgebnisNachricht ergebnisse) {
        Platform.runLater(() -> {setErgebnisse(ergebnisse);});
    }

    /**
     * Setzt die Ergebnisse in das Grid und zeigt für jeden Spieler den Namen und die Punkte an.
     *
     * @param ergebnisNachricht Liste von Paaren mit Spielername und Punktzahl.
     */
    public void setErgebnisse(TunierErgebnisNachricht ergebnisNachricht) {
        ergebnisGrid.getChildren().clear();
        Map<String, Integer> ergebnisse = ergebnisNachricht.getTunierPunkte();

        for (Map.Entry<String, Integer> entry : ergebnisse.entrySet()){
            System.out.println(entry.getKey() + " " + entry.getValue());
        }

        String gewinner = ergebnisNachricht.getGewinner();

        System.out.println("Gewinner: " + gewinner);

        Label gewinnerLabel = new Label("Gewinner: " + gewinner);
        gewinnerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        ergebnisGrid.add(gewinnerLabel, 0, 0, 2, 1);
        gewinnerLabel.setMaxWidth(Double.MAX_VALUE);
        GridPane.setColumnSpan(gewinnerLabel, 2);
        GridPane.setHalignment(gewinnerLabel, HPos.CENTER);

        int i = 1;
        for (Map.Entry<String, Integer> entry : ergebnisse.entrySet()){
            Label name = new Label(entry.getKey());
            Label punkte = new Label(String.valueOf(entry.getValue()));

            ergebnisGrid.add(name, 0, i);
            ergebnisGrid.add(punkte, 1, i);
            i++;
        }
    }

    /**
     * Wechselt zurück zur Lobby.
     */
    public void handleBtnVerlassen() {
        stageManager.switchScene("lobby.fxml");
    }

}
