package cosmic.eidex.gui.ControllerFX;

import cosmic.eidex.DTO.SpielraumDTO;
import cosmic.eidex.Service.SessionManager;
import cosmic.eidex.gui.StageManager;
import cosmic.eidex.repository.SpielerRepository;
import cosmic.eidex.spielmodell.Spieler;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static cosmic.eidex.gui.util.AlertUtil.showInfo;

/**
 * GUI-Controller für das Erstellen eines neuen Spielraums.
 * Ermöglicht es dem Benutzer, einen Spielraum zu erstellen
 * und automatisch beizutreten.
 */
@Component
public class ErstellenController {

    @FXML
    protected TextField spielraumName;

    @FXML
    protected PasswordField passwortFeld;

    protected final StageManager stageManager;

    /**
     * Konstruktor für den ErstellenController.
     * @param stageManager das StageManager-Objekt, das für Szenenwechsel in der JavaFX-Anwendung verwendet wird
     */
    @Autowired
    public ErstellenController(StageManager stageManager){
        this.stageManager = stageManager;
    }

    @FXML
    public void initialize() {
    }

    /**
     * Wird aufgerufen, wenn man den "Zurück"-Button drückt.
     * Wechselt zurück zur Lobby-Ansicht.
     */
    @FXML
    private void handleZurueck() {
        stageManager.switchScene("/lobby.fxml");
    }

    /**
     * Wird aufgerufen, wenn der Benutzer den "Bestätigen"-Button drückt.
     * Erstellt einen neuen Spielraum per REST-Aufruf und tritt diesem automatisch bei.
     * Bei Erfolg wird der Benutzer in den Spielraum weitergeleitet.
     * Bei Fehlern wird eine Info-Meldung angezeigt.
     */
    @FXML
    private void handleBestaetigen() {
        String name = spielraumName.getText().trim();
        String passwort = passwortFeld.getText().trim();

        if (name.isEmpty()) {
            showInfo("Bitte Name für Spielraum angeben!");
            return;
        }

        try {
            var restTemplate = new org.springframework.web.client.RestTemplate();
            String url = "http://localhost:8080/spielraum/erstelle?name=" + name;
            if (!passwort.isEmpty()) {
                url += "&passwort=" + passwort;
            }

            SpielraumDTO neuerRaum = restTemplate.postForObject(url, null, SpielraumDTO.class);

            if (neuerRaum != null) {
                // Jetzt automatisch beitreten
                String spToGet = "http://localhost:8080/spielraum/spieler?name=" + SessionManager.getNickname();
                Spieler spieler = restTemplate.postForObject(spToGet, null, Spieler.class);
                String beitretenUrl = "http://localhost:8080/spielraum/beitreten?id=" + neuerRaum.getId();
                Boolean beigetreten = restTemplate.postForObject(beitretenUrl, spieler, Boolean.class);

                if (Boolean.TRUE.equals(beigetreten)) {
                    SpielraumController controller = stageManager.switchSceneAndReturnController("/spielraum.fxml");
                    if (controller != null) {
                        controller.setRaumId(neuerRaum.getId());
                    }
                } else {
                    showInfo("Fehler beim automatischen Beitreten.");
                }
            } else {
                showInfo("Fehler beim Erstellen des Spielraums.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showInfo("Fehler beim Erstellen/Beitreten.");
        }
    }



}
