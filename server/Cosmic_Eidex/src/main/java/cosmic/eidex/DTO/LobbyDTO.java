package cosmic.eidex.DTO;

import cosmic.eidex.Lobby.Spielraum;
import cosmic.eidex.spielmodell.Spieler;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO - Data Transfer Object, das der Controller dem Client übergeben kann.
 * LobbyDTO kann verwendet werden um die Liste von Spielröumen oder angemeldeten Spielern an den Client zu übertragen.
 */
public class LobbyDTO {
    public List<Spielraum> spielraeume;
    public List<Spieler> spieler;

    /**
     * Konstruktor für LobbyDTO.
     */
    public LobbyDTO() {
        this.spielraeume = new ArrayList<Spielraum>();
        this.spieler = new ArrayList<Spieler>();
    }

    /**
     * Gibt die Liste aller Spielräume zurück.
     * @return Liste von Spielräumen.
     */
    public List<Spielraum> getSpielraeume() {
        return spielraeume;
    }

    /**
     * Gibt Liste aller angemeldeten Spieler zurück
     * @return Liste von Spielern
     */
    public List<Spieler> getSpieler() {
        return spieler;
    }

}
