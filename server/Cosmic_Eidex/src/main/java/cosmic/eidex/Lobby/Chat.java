package cosmic.eidex.Lobby;

import java.util.List;


/**
 * Diese Klasse beschreibt Chat-Objekte.
 */
public class Chat {

    /// Chatverlauf
    private List<Nachricht> nachrichten;

    /**
     * leerer konstruktor
     */
    public Chat() {}

    /**
     * Konstruktor fuer Chat
     * @param nachrichten Liste der Nachrichten
     */
    public Chat(List<Nachricht> nachrichten) {
        this.nachrichten = nachrichten;
    }

    /**
     * @return aktuellen Chatverlauf
     */
    public List<Nachricht> getNachrichten() {
        return nachrichten;
    }

}