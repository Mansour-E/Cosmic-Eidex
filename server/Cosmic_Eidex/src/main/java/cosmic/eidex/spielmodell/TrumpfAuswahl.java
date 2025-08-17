package cosmic.eidex.spielmodell;

/**
 * Informationsklasse, welche nach dem Trumpfziehen sowohl die Karte zum anzeigen,
 * als auch die Regel speichert.
 */
public class TrumpfAuswahl {

    private final Regel regel;
    private final Karte trumpfkarte;

    public TrumpfAuswahl(Regel regel, Karte trumpfkarte) {
        this.regel = regel;
        this.trumpfkarte = trumpfkarte;
    }

    public Regel getRegel() {
        return regel;
    }

    public Karte getTrumpfkarte() {
        return trumpfkarte;
    }
}
