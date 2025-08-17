package cosmic.eidex.spielmodell;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Klasse fuer ein Deck in Cosmic Eidex
 */
public class KartenDeck {

    /**
     * deck ist eine ArrayList (einfacheres handling) und enthaelt die Objekte vom Typ Karte
     * deckgroesse ist eine feste Zahl nach Spielregeln 36
     */
    public ArrayList<Karte> deck;
    private final int deckgroesse = 35;
    // TODO : JavaFX Bild fuer Deck bzw Karte


    public KartenDeck(ArrayList<Karte> deck){
        this.deck = deck;
    }

    /**
     * Konstruktor generiert neues Deck mithilfe der static Methoden fuer Farben und Werte
     * aus der Klasse Karte.
     * Fuer jede Farbe und Wert wir ein neues Karte Objekt erstellt und in Liste hinzugefuegt
     */
    public KartenDeck() {

        List<String> farben = Karte.getFarbeList();
        List<String> werte = Karte.getWertList();

        deck = new ArrayList<>();

        for(String farbe: farben){
            for(String wert: werte){
                deck.add(new Karte(farbe, wert));
            }
        }

    }

    public Karte getKarte(int Index){
        return deck.get(Index);
    }

    /**
     * Nur da zum testen fuer Anzeige auf Website
     * @param deck
     * @return Deck Inhalt
     */
    public ArrayList<Karte> getDeck(ArrayList<Karte> deck) {
        return deck;
    }

    /**
     * Wird auf KartenDeck Objekt angewandt
     * Tauscht in mit For-Loop jede Karte mit einer zufaelligen anderen
     * Kein neues KartenDeck Objekt wird erzeugt, sondern in place veraendert
     */
    public void shuffle(){
        Random random = new Random();

        for(int i = 0; i < deckgroesse; i++){

            int r = i + random.nextInt(deckgroesse - i);

            Karte karte_an_r = deck.get(r);
            Karte karte_an_i = deck.get(i);
            deck.set(r, karte_an_i);
            deck.set(i, karte_an_r);
        }
    }

    /**
     * Teilt 4 Karten aus.
     * Ersten 4 Karten werden Spielerhand hinzugefuegt und aus Deck entfernt
     * @param spielerHand ist die uebergebene Spieler-Hand
     * @return hand gibt die modifizierte Spieler-Hand zurueck
     */
    public void ziehen(SpielerHand spielerHand){

        List<Karte> gezogeneKarten = new ArrayList<>(deck.subList(0, 4));

        spielerHand.hand.addAll(gezogeneKarten);

        deck.subList(0, 4).clear();

    }

}
