package cosmic.eidex.spielmodell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Die Klasse Spielstand hält innerhalb einer Partie die Punkte der Spieler fest.
 */
public class SpielStand {

    // Attribute
    private final Map<Spieler, Integer> punkteliste;

    // Konstruktor
    public SpielStand(List<Spieler> spielerListe) {
        punkteliste = new HashMap<>();
        for (Spieler spieler : spielerListe) {
            punkteliste.put(spieler, 0);
        }
    }

    // Getter und Setter
    public int getPunkte(Spieler spieler) {
        return punkteliste.get(spieler);
        }

    public void setPunkte(Spieler spieler, Integer neuePunkte) {
        if (punkteliste.containsKey(spieler)) {
            punkteliste.put(spieler, neuePunkte);
        }

    }

    public Map<Spieler, Integer> getPunkteliste() {
        return punkteliste;
    }

    // Methoden:
    /**
     * Zeigt den gegenwärtigen Spielstand
     * @return eine List<String> die für jeden Spieler dessen Name und Punkte enthält
     */
    public List<String> zeigeSpielstand() {
        List<String> ausgabe = new ArrayList<>();
        for (Map.Entry<Spieler, Integer> entry : punkteliste.entrySet()) {
            ausgabe.add(entry.getKey().getNickname() + ": " + entry.getValue() + " Punkte");
        }
        return ausgabe;
    }
}
