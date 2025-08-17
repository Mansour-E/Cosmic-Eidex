package cosmic.eidex.spielmodell;

import java.util.List;
import java.util.Objects;

/**
 * Klasse fuer eine einzelne Spielkarte in Cosmic Eidex
 */
public class Karte {

    /**
     * farbe referenziert Herz, Eidex, Stern, Rabe
     * wert referenziert 6 bis Ass
     */
    public String farbe;
    public String wert;

    public Karte(String farbe, String wert){
        this.farbe = farbe;
        this.wert = wert;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Karte karte)) return false;
        return Objects.equals(farbe, karte.farbe) && Objects.equals(wert, karte.wert);
    }
    @Override
    public int hashCode() {
        return Objects.hash(farbe, wert);
    }

    @Override
    public String toString() {
        return farbe + " " + wert;
    }

    /**
     * Getter fuer Farbe
     * @return farbe der Karte
     */
    public String getFarbe() {
        return farbe;
    }

    /**
     * Setter fuer Farbe
     * @param farbe der Karte
     */
    public void setFarbe(String farbe){
        this.farbe = farbe;
    }

    /**
     * Getter fuer Wert
     * @return wert der Karte
     */
    public String getWert() {
        return wert;
    }

    /**
     * Setter fuer Wert
     * @param wert der Karte
     */
    public void setWert(String wert){
        this.wert = wert;
    }

    /**
     * Wird benutzt, um ein Deck zu generieren, Enthaelt alle Farben der Karten
     * @return Liste der Farben der Karten
     */
    public static List<String> getFarbeList(){
        return List.of("Herz", "Eidex", "Rabe", "Stern");
    }

    /**
     * Wird benutzt, um ein Deck zu generieren, Enthaelt alle Werte der Karten
     * @return Liste der Werte der Karten
     */
    public static List<String> getWertList(){
        return List.of("6", "7", "8", "9", "10", "Bube", "Dame", "Koenig", "Ass");
    }
}
