package cosmic.eidex.gui.ControllerFX;

import cosmic.eidex.Service.SessionManager;
import cosmic.eidex.gui.StageManager;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import static cosmic.eidex.gui.util.AlertUtil.*;

/**
 * GUI-Controller zum Bearbeiten des Profils (Nickname/Passwort).
 * Kommuniziert über REST mit dem Server.
 */
@Component
public class BearbeitenController {

    @FXML
    private TextField nicknameFeld;

    @FXML
    private PasswordField passwortFeld;

    private final RestTemplate restTemplate = new RestTemplate();

    private final StageManager stageManager;

    /**
     * Konstruktor für BearbeitenController
     * @param stageManager Um JavaFX Szenen zu steuern
     */
    @Autowired
    public BearbeitenController(StageManager stageManager) {
        this.stageManager = stageManager;
    }

    @FXML
    public void initialize() {
    }

    /**
     * Navigiert zurück zur Lobby-Ansicht.
     */
    @FXML
    private void handleZurueck() {
        stageManager.switchScene("/lobby.fxml");
    }

    /**
     * Navigiert zur Lösch-Ansicht.
     */
    @FXML
    private void handleLoeschen() {
        stageManager.switchScene("/loeschen.fxml");
    }

    /**
     * Bestätigt Änderungen am Profil.
     * - Nickname wird per REST geändert.
     * - Passwort-Änderung öffnet separaten View.
     */
    @FXML
    private void handleBestaetigen() {
        String neuerNick = nicknameFeld.getText().trim();
        String neuesPasswort = passwortFeld.getText().trim();
        String alterNick = SessionManager.getNickname();

        boolean nickGeaendert = !neuerNick.isBlank() && !alterNick.equals(neuerNick);
        boolean passwortGeaendert = !neuesPasswort.isBlank();

        if (!nickGeaendert && !passwortGeaendert) {
            showInfo("Kein Feld ausgefüllt");
            return;
        }

        if (nickGeaendert) {

            SessionManager.printAllActiveSessions();

            String url = "http://localhost:8080/login/changeNickName?nicknameAlt=" + alterNick + "&nicknameNeu=" + neuerNick;
            String token = SessionManager.getToken(alterNick);
            System.out.println("token: " + token);
            System.out.println("alt: " + alterNick);
            System.out.println("neuer: " + neuerNick);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            System.out.println(token);
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url,
                    HttpMethod.POST,
                    requestEntity,
                    String.class);
            System.out.println(response.getBody());
            showInfo("Neuer Nickname: " + neuerNick);
            SessionManager.removeToken(alterNick);
            SessionManager.saveToken(neuerNick, token);

        }

        if (passwortGeaendert) {
            stageManager.switchScene("/aendern.fxml");
        } else {
            stageManager.switchScene("/lobby.fxml");
        }
    }

}
