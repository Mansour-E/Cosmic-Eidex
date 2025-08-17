package cosmic.eidex.gui.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * Helferklasse für das Anzeigen von Alerts bzw. Meldungen im UI
 */
public class AlertUtil {

    /**
     * Zeigt Informationsmeldung.
     *
     * @param message Die Nachricht
     */
    public static void showInfo(String message) {
        showAlert(AlertType.INFORMATION, "Information", message);
    }

    /**
     * Zeigt  Fehlermeldung.
     *
     * @param message Die anzuzeigende Fehlermeldung.
     */
    public static void showError(String message) {
        showAlert(AlertType.ERROR, "Fehler", message);
    }

    /**
     * Methode zum Anzeigen von verschiedenen Alerttypen
     *
     * @param type    Der AlertType (INFORMATION, ERROR, etc.)
     * @param title   Der Titel
     * @param message Die Nachricht
     */
    public static void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
