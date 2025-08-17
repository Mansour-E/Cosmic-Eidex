package cosmic.eidex.gui.ControllerFX;

import cosmic.eidex.Service.LoginManager;
import cosmic.eidex.gui.StageManager;
import cosmic.eidex.repository.SpielerRepository;
import cosmic.eidex.spielmodell.Spieler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static cosmic.eidex.gui.util.AlertUtil.*;

/**
 * GUI-Controller fuer das Registrieren.
 */
@Component
public class RegisterController {

    private final RestTemplate restTemplate = new RestTemplate();

    @FXML
    private TextField nicknameField;

    @FXML
    private PasswordField passwortField;

    @FXML
    private PasswordField passwortWdhField;

    @FXML
    private TextField alterField;

    @FXML
    private Label fehlermeldungLabel;

    private final StageManager stageManager;

    /**
     * Konstruktor fuer RegisterController.
     * @param stageManager Um JavaFX Szenen zu steuern.
     */
    @Autowired
    public RegisterController(StageManager stageManager) {
        this.stageManager = stageManager;
    }

    /**
     * Wechselt zurück zur Login-Seite.
     * @param actionEvent das Auslöse-Event
     */
    @FXML
    public void handleZurueck(ActionEvent actionEvent){
        stageManager.switchScene("/login.fxml");
    }

    /**
     * Verarbeitet die Registrierung eines neuen Spielers.
     * Prüft ob Passwörter übereinstimmen, Alter eine Zahl größer 6 ist und ob der Nickname vergeben ist.
     * Bei erfolgreicher Registrierung wird der Nutzer zur Login-Seite weitergeleitet.
     * @param actionEvent das Auslöse-Event
     */
    @FXML
    public void handleRegistrieren(ActionEvent actionEvent) {
        String nickname = nicknameField.getText().trim();
        String pw = passwortField.getText().trim();
        String pwWdh = passwortWdhField.getText().trim();
        String alterText = alterField.getText().trim();

        if (!pw.equals(pwWdh)) {
            fehlermeldungLabel.setText("Passwörter stimmen nicht überein.");
            return;
        }

        if (pw.isEmpty()) {
            fehlermeldungLabel.setText("Bitte Passwort eingeben.");
            return;
        }

        if (!alterText.matches("\\d+")) {
            fehlermeldungLabel.setText("Alter muss eine Zahl sein.");
            return;
        }

        int alter = Integer.parseInt(alterText);

        if(alter <= 6){
            fehlermeldungLabel.setText("Zu jung");
            return;
        }

        String findurl = "http://localhost:8080/login/findExisting?nickname=" + nickname;
        try{
            boolean check = restTemplate.postForEntity(findurl, null, boolean.class).getBody();
            if(check){
                throw new RuntimeException("Nutzername existiert bereits");
            }
        } catch (Exception e){
            fehlermeldungLabel.setText("Nutzername existiert bereits");
            return;
        }

        String url = "http://localhost:8080/login/register?nickname=" + nickname + "&passwort=" + pw + "&alter=" + alter;

        try {

            Spieler spieler = restTemplate.postForEntity(url, null, Spieler.class).getBody();

            if (spieler == null) {
                showInfo("Registrierung fehlgeschlagen. Prüfen Sie die Eingaben.");
            } else {
                showInfo("Registrierung erfolgreich! Bitte melden Sie sich an!");
                stageManager.switchScene("/login.fxml");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
