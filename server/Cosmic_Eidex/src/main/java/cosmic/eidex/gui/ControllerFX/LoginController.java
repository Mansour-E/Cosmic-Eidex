package cosmic.eidex.gui.ControllerFX;

import cosmic.eidex.DTO.SpielerDTO;
import cosmic.eidex.Service.SessionManager;
import cosmic.eidex.spielmodell.Spieler;
import cosmic.eidex.gui.StageManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.ScatterChart;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static cosmic.eidex.gui.util.AlertUtil.*;

/**
 * GUI-Controller fuer den Login.
 * Er überprüft die Benutzereingaben, kommuniziert mit dem Server über eine REST-Schnittstelle
 * und verwaltet den Szenenwechsel nach erfolgreicher Authentifizierung.
 */
@Component
public class LoginController {

    private final RestTemplate restTemplate = new RestTemplate();

    @FXML
    private TextField nicknameFeld;

    @FXML
    private PasswordField passwortFeld;

    @FXML
    private Label fehlermeldungLabel;

    private final StageManager stageManager;

    /**
     * Konstruktor fuer LoginController.
     * @param stageManager Um JavaFX Szenen zu steuern.
     */
    @Autowired
    public LoginController(StageManager stageManager) {
        this.stageManager = stageManager;
    }

    /**
     * Handhabt den Login-Vorgang, wenn der Benutzer auf den "Anmelden"-Button klickt.
     * Bei erfolgreicher Anmeldung wird der JWT-Token gespeichert und zur Lobby weitergeleitet.
     * Bei Fehlern wird eine entsprechende Fehlermeldung angezeigt.
     * @param event Das auslösende ActionEvent (z. B. Klick auf den Anmelden-Button)
     */
    @FXML
    private void handleAnmelden(ActionEvent event) {
        String nickname = nicknameFeld.getText();
        String passwort = passwortFeld.getText();
        System.out.println(nickname + " " + passwort);

        if (nickname.isEmpty() || passwort.isEmpty()) {
            fehlermeldungLabel.setText("Bitte Nickname und Passwort eingeben.");
            return;
        }

        String url =  "http://localhost:8080/login/login?nickname=" + nickname + "&passwort=" + passwort;
        try {
            ResponseEntity<SpielerDTO> response = restTemplate.postForEntity(url, null, SpielerDTO.class);
            if (response != null) {
                SpielerDTO currentUser = response.getBody();
                String token = response.getHeaders().getFirst("Authorization");
                if (token != null && token.startsWith("Bearer ")) {
                    token = token.substring(7);
                    SessionManager.saveToken(nickname, token);
                    System.out.println("Token gespeichert: " + token);
                }
                stageManager.switchScene("/lobby.fxml");
            }
        }catch (HttpClientErrorException e) {
            System.out.println("Fehler bei der Anmeldung: " + e.getStatusCode());
            fehlermeldungLabel.setText("Falsche Anmeldedaten.");
        }catch (Exception e) {
            System.out.println("Ein unerwarteter Fehler ist aufgetreten: " + e.getMessage());
            fehlermeldungLabel.setText("Anmeldung momentan nicht möglich.");
        }


    }

    /**
     * Wechselt zur Registrierungsseite, wenn der Benutzer auf "Registrieren" klickt.
     * @param event Das auslösende ActionEvent (z. B. Klick auf den Registrieren-Button)
     */
    @FXML
    private void handleRegistrieren(ActionEvent event) {
        stageManager.switchScene("/register.fxml");
    }
}
