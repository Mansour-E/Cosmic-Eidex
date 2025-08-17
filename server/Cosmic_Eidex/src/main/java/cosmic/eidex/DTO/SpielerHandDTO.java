package cosmic.eidex.DTO;

import cosmic.eidex.spielmodell.Karte;
import cosmic.eidex.spielmodell.Spieler;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO - Data Transfer Object, das der Controller dem Client übergeben kann.
 * SpielerHandDTO kann verwendet werden um Informationen zur Hand des Spielers weiterzugeben.
 */
public class SpielerHandDTO {

    private List<Karte> hand;
    private List<Karte> gueltigeKarten;
    private Karte gedrueckteKarte;
    private Spieler spieler;

    /**
     * Leerer Konstruktor
     */
    public SpielerHandDTO() {}

    /**
     * Konstruktor für SpielerHandDTO
     * @param hand aktuelle Hnad
     * @param gueltigeKarten die gueltigen Karten
     * @param gedrueckteKarte die gedrueckte Karte
     * @param spieler der Spieler
     */
    public SpielerHandDTO(ArrayList<Karte> hand, List<Karte> gueltigeKarten, Karte gedrueckteKarte, Spieler spieler) {
        this.hand = hand;
        this.gueltigeKarten = gueltigeKarten;
        this.gedrueckteKarte = gedrueckteKarte;
        this.spieler = spieler;
    }


    public List<Karte> getHand() {
        return hand;
    }

    public void setHand(List<Karte> hand) {
        this.hand = hand;
    }

    public Karte getGedrueckteKarte() {
        return gedrueckteKarte;
    }

    public void setGedrueckteKarte(Karte gedrueckteKarte) {
        this.gedrueckteKarte = gedrueckteKarte;
    }

    public List<Karte> getGueltigeKarten() {
        return gueltigeKarten;
    }

    public void setGueltigeKarten(List<Karte> gueltigeKarten) {
        this.gueltigeKarten = gueltigeKarten;
    }

    public Spieler getSpieler() {
        return spieler;
    }

    public void setSpieler(Spieler spieler) {
        this.spieler = spieler;
    }
}
