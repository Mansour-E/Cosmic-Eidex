package cosmic.eidex.DTO;

import java.util.List;

/**
 * DTO - Data Transfer Object, das der Controller dem Client übergeben kann.
 * ChatDTO kann verwendet werden um dem Client neue oder bisherige Nachrichten zu übertragen.
 */
@SuppressWarnings("unused")
public class ChatDTO {

    private List<NachrichtDTO> nachrichten;

    /**
     * Gibt Liste der bisheriger Nachrichten zurück.
     * @return Liste aus NachrichtDTOs mit bisherigen Nachrichten.
     */
    public List<NachrichtDTO> getNachrichten() { return nachrichten; }

    /**
     * Leerer Konstruktor
     */
    public ChatDTO() {}

    /**
     * ChatDTO Konstruktor
     * @param nachrichten Liste von Nachrichten
     */
    public ChatDTO(List<NachrichtDTO> nachrichten) {
        this.nachrichten = nachrichten;
    }
}
