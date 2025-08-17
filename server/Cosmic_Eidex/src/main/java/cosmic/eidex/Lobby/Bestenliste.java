package cosmic.eidex.Lobby;

import java.util.*;
import java.util.stream.Collectors;

import cosmic.eidex.repository.SpielerRepository;
import cosmic.eidex.spielmodell.Spieler;

/**
 * Speichert in Datenbank wie oft Spieler gewonnen haben und macht dies sichtbar.
 */
public class Bestenliste {

    /// Datenbank
    private final SpielerRepository repo;

    public Bestenliste(SpielerRepository repo) {
        this.repo = repo;
    }

    /**
     * Aktualisiert die Sieganzahl eines Spielers und erhöht diesen um 1 oder fügt ihn zur Liste/Map hinzu.
     * @param spieler darf nicht null sein, sollte/muss vorher vermieden werden.
     *                Wenn null, passiert nichts.
     */
    public void aktualisiere(Spieler spieler) {
        if (spieler != null) {
            spieler.setSiege(spieler.getSiege() + 1);
            repo.save(spieler);
        }
    }

    /**
     * Zeigt Liste der bis zu 10 besten Spieler, nach Siegen.
     * @return Eine Liste mit bis zu 10 Einträgen von den Spielern mit den meisten Siegen.
     */
    public List<Spieler> gibTop10() {
        return repo.findAll().stream()
                .filter(s -> !s.isBot())
                .sorted(Comparator.comparingInt(Spieler::getSiege).reversed())
                .limit(10)
                .toList();
    }


    /**
     * Eine Methode für Tests.
     * @return Eine Map mit allen Einträgen der Bestenliste
     */
    public Map<Spieler, Integer> getEintraege() {
        return repo.findAll().
                stream()
                .map(spieler -> new AbstractMap.SimpleEntry<>(spieler, spieler.getSiege()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}

