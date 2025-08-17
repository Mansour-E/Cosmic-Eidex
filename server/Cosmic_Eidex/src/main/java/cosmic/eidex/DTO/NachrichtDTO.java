package cosmic.eidex.DTO;

import cosmic.eidex.Lobby.Nachricht;

import java.time.OffsetDateTime;

/**
 * Data Transfer Object (DTO) für eine Nachricht.
 * Wird verwendet, um Nachrichten zwischen Client und Server zu übertragen.
 */
@SuppressWarnings("unused") // DTO
public class NachrichtDTO {

    private String sender;
    private String inhalt;
    private OffsetDateTime zeitstempel;

    /**
     * Leerer Konstruktor
     */
    public NachrichtDTO() {}

    /**
     * Konstruktor für NachrichtDTO
     * @param n die Nachricht
     */
    public NachrichtDTO(Nachricht n) {
        this.sender = n.getSender();
        this.inhalt = n.getInhalt();
        this.zeitstempel = n.getZeitstempel();
    }

    public String getSender() { return sender; }
    public String getInhalt() { return inhalt; }
    public OffsetDateTime getZeitstempel() { return zeitstempel; }

}

