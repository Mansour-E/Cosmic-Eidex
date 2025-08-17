package cosmic.eidex.Lobby;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Repraesentiert eine Nachricht, die auch in der Datenbank gespeichert wird
 */
@Entity
@Table(name = "chat_history")
public class Nachricht {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name =  "messageid")
    private Long id;

    /**
     * Wer diese Nachricht gesendet hat.
     */
    @Column(name =  "senderName")
    private String sender;

    /**
     * Inhalt der Nachricht
     */
    @Column(name =  "messageText", columnDefinition = "text")
    private String inhalt;
    /**
     * Sender generiertes Datum für die Nachricht
     */
    @Column(name = "created_at")
    private OffsetDateTime zeitstempel = OffsetDateTime.now();

    /**
     * leerer Konstruktor
     */
    public Nachricht() {}

    /**
     * Konstruktor fuer Nachricht
     * @param sender Absender der Nachricht
     * @param inhalt Inhalt der Nachricht
     */
    public Nachricht(String sender, String inhalt) {
        this.sender = sender;
        this.inhalt = inhalt;
        this.zeitstempel = OffsetDateTime.now();
    }

    /// Getter Methoden

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getInhalt() {
        return inhalt;
    }

    public void setInhalt(String inhalt) {
        this.inhalt = inhalt;
    }

    public OffsetDateTime getZeitstempel() {
        return zeitstempel;
    }

    public void setZeitstempel(OffsetDateTime zeitstempel) {
        this.zeitstempel = zeitstempel;
    }
}
