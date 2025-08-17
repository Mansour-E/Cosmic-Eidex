package cosmic.eidex.cont;

import cosmic.eidex.Lobby.Lobby;
import cosmic.eidex.DTO.LobbyDTO;
import org.springframework.web.bind.annotation.*;

/**
 * REST-Controller für Lobby-Funktionen.
 */
@RestController
@RequestMapping("/lobby")
public class LobbyCont {

    private final Lobby lobby;

    /**
     * Konstruktor fuer LobbyCont
     */
    public LobbyCont() {
        // Leere Spieler-Liste als Startwert
        this.lobby = new Lobby();
    }

    /**
     * Gibt alle Spielräume und Spieler der Lobby zurück.
     * @return LobbyDTO mit Räumen und Spielern
     */
    @GetMapping("/spielraumanzeigen")
    public LobbyDTO getLobbyDaten() {
        LobbyDTO dto = new LobbyDTO();
        dto.spielraeume.addAll(lobby.spielrauemeAnzeigen()); // Methode aus Lobby aufrufen
        dto.spieler.addAll(lobby.spieler); // direkt aus Lobby
        return dto;
    }

    /**
     * Erstellt einen neuen Spielraum mit gegebenem Namen.
     * @param name Name des neuen Spielraums
     */
    @PostMapping("/erzeugeneuenraum")
    public void erzeugeNeuenRaum(@RequestParam String name) {
        lobby.erzeugeNeuenRaum(name);
    }
}
