package cosmic.eidex.gui.ControllerFX;

import cosmic.eidex.Service.LoginManager;
import cosmic.eidex.Service.SessionManager;
import cosmic.eidex.gui.StageManager;
import cosmic.eidex.spielmodell.Spieler;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import static cosmic.eidex.gui.util.AlertUtil.*;

/**
 * GUI-Controller für das Ändern des Passworts im JavaFX-Client.
 * Kommuniziert über REST mit dem Server.
 */
@Component
public class AendernController {


    @FXML
    private PasswordField altesPasswortFeld;

    @FXML
    private PasswordField neuesPasswortFeld;

    private final RestTemplate restTemplate = new RestTemplate();

    private final StageManager stageManager;

    /**
     * Konstruktor für AendernController
     * @param stageManager Um Kontrolle über die Szenen zu haben
     */
    @Autowired
    public AendernController(StageManager stageManager) {
        this.stageManager = stageManager;
    }

    /**
     * Navigiert zurück zur Bearbeiten-Ansicht.
     */
    @FXML
    private void handleZurueck() {
        stageManager.switchScene("/bearbeiten.fxml");
    }

    /**
     * Sendet eine Passwortänderung an den Server.
     * Überprüft die Eingaben, baut den HTTP-Request und behandelt die Antwort.
     */
    @FXML
    private void handleBestaetigen() {
        String altesPasswort = altesPasswortFeld.getText().trim();
        String neuesPasswort = neuesPasswortFeld.getText().trim();
        String nickname = SessionManager.getNickname();
        String token = SessionManager.getToken(nickname);
        //Spieler aktspieler = loginManager.getSpielerZuToken(token);
        //String aktPassword = aktspieler.getPasswort();

        if (altesPasswort == null || altesPasswort.isEmpty()) {
            showInfo("Bitte gib dein aktuelles Passwort zur Bestätigung ein.");
            return;
        }


        String url = "http://localhost:8080/login/changePassword?passwortNeu=" + neuesPasswort + "&passwortAlt=" + altesPasswort;


        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        System.out.println(token);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url,
                HttpMethod.POST,
                requestEntity,
                String.class);
        System.out.println(response.getBody());
        showInfo("Passwort geandert.");

        // Beispiel: Nach Prüfung zurück zur Lobby
        stageManager.switchScene("/lobby.fxml");
    }
}
