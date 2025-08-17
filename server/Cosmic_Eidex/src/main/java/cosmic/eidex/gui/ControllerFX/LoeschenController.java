package cosmic.eidex.gui.ControllerFX;

import cosmic.eidex.Service.SessionManager;
import cosmic.eidex.gui.StageManager;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

/**
 * GUI-Controller für das Loeschen eines Benutzerprofiles.
 */
@Component
public class LoeschenController {


    @FXML
    private PasswordField passwortFeld;


    @FXML
    private Label ueberschrift;

    private final StageManager stageManager;


    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Konstruktor fuer LoeschenController
     * @param stageManager Um JavaFX Szenen zu steuern
     */
    @Autowired
    public LoeschenController(StageManager stageManager) {
        this.stageManager = stageManager;
    }

    /**
     * Initialisiert den Controller und setzt die Überschrift des Fensters.
     */
    @FXML
    public void initialize() {
        ueberschrift.setText("Profil löschen");
    }

    /**
     * Führt den Löschvorgang des Benutzerprofils durch, nachdem der Benutzer sein Passwort eingegeben hat.
     * Bei Erfolg wird zur Login-Szene gewechselt.
     *
     * @param event Das auslösende ActionEvent (z. B. Klick auf den Löschen-Button)
     */
    @FXML
    private void handleLoeschen(ActionEvent event) {
        String passwort = passwortFeld.getText();
        String nickname = SessionManager.getNickname();
        String token = SessionManager.getToken(nickname);

        String url = "http://localhost:8080/login/delete?nickname=" + nickname + "&passwort=" + passwort;


        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        System.out.println(token);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url,
                HttpMethod.DELETE,
                requestEntity,
                String.class);
        System.out.println(response.getBody());
        stageManager.switchScene("/login.fxml");

    }

    /**
     * Bricht den Löschvorgang ab und wechselt zurück zur Profil-Bearbeitungsseite.
     *
     * @param event Das auslösende ActionEvent (z. B. Klick auf den Zurück-Button)
     */
    @FXML
    private void handleZurueck(ActionEvent event) {
        stageManager.switchScene("/bearbeiten.fxml");
    }
}
