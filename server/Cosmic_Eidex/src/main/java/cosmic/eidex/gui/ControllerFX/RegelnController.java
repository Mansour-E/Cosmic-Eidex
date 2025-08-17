package cosmic.eidex.gui.ControllerFX;

import javafx.fxml.FXML;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

/**
 * GUI-Controller für Regeln ist für die Anzeige der Spielregeln im PDF-Format zuständig.
 */
@Component
public class RegelnController {

    @FXML
    private WebView pdfViewer;

    /**
     * Initialisiert den Controller.
     * Die Datei "spielregeln-cosmic-eidex.pdf" wird dabei über den lokalen Server geladen.
     */
    @FXML
    public void initialize() {
        pdfViewer.getEngine().setJavaScriptEnabled(true);
        String url = "http://localhost:8080/pdfjs/viewer.html?file=spielregeln-cosmic-eidex.pdf";
        pdfViewer.getEngine().load(url);
    }

    /**
     * Schließt das aktuelle Fenster, wenn der Benutzer auf den "Schließen"-Button klickt.
     */
    @FXML
    private void handleSchliessen() {
        Stage stage = (Stage) pdfViewer.getScene().getWindow();
        stage.close();
    }
}
