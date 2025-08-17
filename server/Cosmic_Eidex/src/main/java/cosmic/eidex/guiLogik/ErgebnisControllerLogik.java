package cosmic.eidex.guiLogik;

import cosmic.eidex.Service.ErgebnisStompClient;
import cosmic.eidex.Service.SpielNachrichten.TunierErgebnisNachricht;
import cosmic.eidex.Service.StompClient;
import cosmic.eidex.gui.ControllerFX.ErgebnisController;
import cosmic.eidex.gui.StageManager;
import javafx.application.Platform;

public class ErgebnisControllerLogik extends ErgebnisController {
    /**
     * Konstruktor für den ErgebnisController.
     *
     * @param stageManager Um JavaFX Szenen zu steuern
     */

    public ErgebnisControllerLogik(StageManager stageManager) {
        super(stageManager);
    }
    public ErgebnisStompClient getStompClient() {
        return stompClient;
    }

    public void setStompClient(ErgebnisStompClient client) {
        this.stompClient = client;
    }
    /**
     * Setzt die Spielraum-ID.
     * @param id Raum-ID
     */
    // Factory für Testbarkeit
    protected ErgebnisStompClient createErgebnisStompClient(Long id) throws Exception {
        return new ErgebnisStompClient(id, this::handleErgebnisse);
    }
    public void setRaumId(Long id) {
        this.raumId = id;
        if (stompClient != null) {
            stompClient.disconnect();
        }
        try {
            stompClient = createErgebnisStompClient(id);
            stompClient.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
