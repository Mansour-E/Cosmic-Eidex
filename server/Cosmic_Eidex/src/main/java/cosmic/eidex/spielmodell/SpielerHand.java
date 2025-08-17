package cosmic.eidex.spielmodell;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Klasse fuer das SpielerHand Objekt
 */
@Component
public class SpielerHand {

    /**
     * hand enthaelt alle Karten die ein Spieler zur verfuegung hat
     * gedrueckteKarte ist anfangs null bis in der Partie eine Karte gedrueckt wird
     */
    public ArrayList<Karte> hand;
    public Karte gedrueckteKarte;
    @JsonBackReference
    private Spieler spieler;

    public SpielerHand(ArrayList<Karte> hand, Karte gedrueckte_karte, Spieler spieler) {
        this.hand = hand;
        this.gedrueckteKarte = gedrueckte_karte;
        this.spieler = spieler;
    }

    public SpielerHand() {
        this.hand = new ArrayList<>();
    }

    public SpielerHand(ArrayList<Karte> hand, Spieler spieler){
        this(hand,null,spieler);
    }

    /**
     * Getter fuer hand
     * @return ArrayList<Karte>
     */
    public ArrayList<Karte> getHand() {
        return hand;
    }

    /**
     * Getter fuer Spieler
     * @return Spieler
     */
    public Spieler getSpieler() {
        return spieler;
    }

    /**
     * Setter fuer Spieler
     * @param spieler
     */
    public void setSpieler(Spieler spieler) {
        this.spieler = spieler;
    }

    /**
     * Fuegt Karten der Hand hinzu. Wenn keine Hand da wird leere initialisiert
     * @param karten Karten die der Hand hinzugefuegt werden sollen
     */
    public void addToHand(List<Karte> karten) {
        if (this.hand == null) {
            this.hand = new ArrayList<>();
        }
        this.hand.addAll(karten);
    }

    /**
     * Getter fuer Karte in der Hand ueber Index
     * @param index Index fuer gesuchte Karte
     * @return Karte an Index
     */
    public Karte getKarte(int index){
        return hand.get(index);
    }

    /**
     * Getter fuer die gedrueckte Karte
     * @return gedreuckte Karte
     */
    public Karte getGedrueckteKarte() {
        return gedrueckteKarte;
    }

    public void setGedrueckteKarte(Karte gedrueckteKarte) {
        this.gedrueckteKarte = gedrueckteKarte;
    }



    /**
     * Methode zum Karten ausspielen. Loescht Karte aus der Hand
     * @param karte Karte die ausgespielt werden soll
     * @return Karte die ausgespielt wurde
     */
    public Karte karteAusspielen(Karte karte){
        String farbe = karte.getFarbe();
        String wert = karte.getWert();

        for(Karte card: hand) {
            if (card.getFarbe().equals(farbe) && card.getWert().equals(wert)) {
                hand.remove(card);
                return card;
            }
        }

        return null;
    }

    /**
     * Methode um eine Karte druecken. Karte wird aus hand geloescht und in gedrueckteKarte gespeichert
     * @param karte Karte die gedreuckt werden soll
     */
    public void karteDruecken(Karte karte){
        String farbe = karte.getFarbe();
        String wert = karte.getWert();

        for(Karte card: hand){
            if(card.getFarbe().equals(farbe) && card.getWert().equals(wert)){
                gedrueckteKarte = card;
                break;

            }
        }
        hand.remove(gedrueckteKarte);

    }


    /**
     * Gibt alle Karten in der Hand zurück, die laut übergebener Regel 0 Punkte wert sind.
     * @param regel Die anzuwendende Regel zur Punktebewertung der Karten
     * @return Liste aller Karten mit einem Punktewert von 0
     */
    public List<Karte> getAlleKartenWertNull(Regel regel) {
        List<Karte> nullKarten = new ArrayList<>();

        for(Karte karte: hand){
            int punkte = regel.getKartePunkte(karte);
            if(punkte == 0){
                nullKarten.add(karte);
            }
        }

        return nullKarten;
    }

    /**
     * Berechnet die Gesamtpunktzahl der Karten in der Hand gemäß der übergebenen Regel.
     * @param regel Die anzuwendende Regel zur Punktebewertung der Karten
     * @return Gesamtpunktzahl der Hand
     */
    public int getKartenPunkteHand(Regel regel) {
        int punkte = 0;

        for(Karte karte: hand){
            int zwischenPunkte = regel.getKartePunkte(karte);
            punkte = punkte + zwischenPunkte;
        }

        return punkte;
    }

}
