package cosmic.eidex.DTO;

import cosmic.eidex.Lobby.Bestenliste;
import cosmic.eidex.DTO.SpielerDTO;

import java.util.List;

/**
 * DTO - Data Transfer Object, das der Controller dem Client übergeben kann.
 * BestenlisteDTO kann verwendet werden um die Top 10 aus der Datenbank an den Client zu übertragen.
 */
@SuppressWarnings("unused") // DTO
public class BestenlisteDTO {


    private List<SpielerDTO> top10Spieler;

    /**
     * Gibt die Liste der Top 10 aus.
     * @return eine Liste von SpielerDTOs von den Spielern mit den meisten Siegen.
     */
    public List<SpielerDTO> getTop10Spieler() { return top10Spieler; }

    /**
     * Leerer Konstruktor
     */
    public BestenlisteDTO() {}

    /**
     * Konstruktor für BestenlisteDTO
     * @param bestenliste die Bestenliste
     */
    public BestenlisteDTO(Bestenliste bestenliste) {
        top10Spieler = bestenliste.gibTop10()
                .stream()
                .map(SpielerDTO::new)
                .toList();
    }
}
