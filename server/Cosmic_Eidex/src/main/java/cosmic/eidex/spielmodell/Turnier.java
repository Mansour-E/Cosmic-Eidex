package cosmic.eidex.spielmodell;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Klasse fuer das Turnier Objekt
 */
public class Turnier {

    // Attribute
    public List<Spieler> spieler;// Spieler im Turnier
    public Spieler gewinner;
    public boolean istAbgeschlossen;
    public Map<Spieler, Integer> siegPunkteListe;
    private Partie aktuellePartie;
    private int anzahlGespielterPartien;

    // Konstruktor
    public Turnier(List<Spieler> spieler) {
        this.spieler = spieler;
        this.istAbgeschlossen = false;
        this.siegPunkteListe = new HashMap<>(spieler.size());
            for (Spieler s : spieler) {
                this.siegPunkteListe.put(s, 0);
            }
        this.anzahlGespielterPartien = 0;
        starteTurnier();
    }

    //Getter und Setter
    public void setSpieler(List<Spieler> spieler) {
        this.spieler = spieler;
    }

    public void setGewinner (Spieler gewinner) {
        this.gewinner = gewinner;
    }

    public void setIstAbgeschlossen(boolean wert) {
        this.istAbgeschlossen = wert;
    }

    public void setSiegPunkteListe(Map<Spieler, Integer> siegPunkteListe) {
        this.siegPunkteListe = siegPunkteListe;
    }

    public void setAktuellePartie(Partie aktuellePartie) {
        this.aktuellePartie = aktuellePartie;
    }

    public void setAnzahlGespielterPartien(int anzahlGespielterPartien) {
        this.anzahlGespielterPartien = anzahlGespielterPartien;
    }

    public List<Spieler> getSpieler() {
        return spieler;
    }

    public Spieler getGewinner () {
        return gewinner;
    }

    public boolean getIstAbgeschlossen() {
        return istAbgeschlossen;
    }

    public Map<Spieler, Integer> getSiegPunkteListe() {
        return siegPunkteListe;
    }

    public Partie getAktuellePartie() {
        return aktuellePartie;
    }

    public int getAnzahlGespielterPartien() {
        return anzahlGespielterPartien;
    }

    //Methoden

    /**
     * starteTurnier ruft NaechstePartie auf, um die erste Partie zu erstellen
     */
    public void starteTurnier() {
        if (istAbgeschlossen) return;
        starteNaechstePartie();
        System.out.println("Turnier wurde gestartet!");
    }

    /**
     * Erhöht die Anzahl der Partien und startet eine Neue
     */
    public void starteNaechstePartie() {
        KartenDeck deck = new KartenDeck();
        deck.shuffle();
        aktuellePartie = new Partie(this, null, spieler, deck, false);
        anzahlGespielterPartien++;
    }

    /**
     * Prüft, ob das Turnier beendet ist, d.h. ob ein Spieler 7 Siegpunkte erreicht hat.
     * @return true, wenn die Bedingung erfüllt ist.
     */
    public boolean istBeendet() {
        for (Map.Entry<Spieler, Integer> s : siegPunkteListe.entrySet()) {
            if (s.getValue() >= 7) {
                setIstAbgeschlossen(true);
            }
        }
        return istAbgeschlossen;
    }


    /**
     * Sieger bestimmen und ausgeben
     * @return sen Spieler der das Turnier gewonnen hat.
     */
    public Spieler getSieger() {
        for (Map.Entry<Spieler, Integer> s : siegPunkteListe.entrySet()) {
            if (s.getValue() >= 7) {
                return s.getKey();
            }
        }
        throw new IllegalArgumentException("Es gibt keinen Sieger in der Siegpunkteliste");
    }

    /**
     * Kann die Siegpunkte eines einzelnen Spielers aktualisieren
     * @param spieler, der neue Punkte bekommt
     * @param punkte, die er bekommt
     */
    public void aktualisiereSiegpunkteliste(Spieler spieler, int punkte) {
        int aktuellePunkte = siegPunkteListe.get(spieler);
        siegPunkteListe.put(spieler, aktuellePunkte + punkte);

    }


}
