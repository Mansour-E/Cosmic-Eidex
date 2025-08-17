package cosmic.eidex.spielmodell;

import java.util.*;

import static cosmic.eidex.spielmodell.Regel.zieheTrumpf;

/**
 * Klasse fuer eine Partie in Cosmic Eidex
 */
public class Partie {

    // Attribute
    public Turnier turnier;
    public List<Spieler> spieler;
    public Regel regel;
    private Runde aktuelleRunde;
    public int rundenzahl;
    public int maxRunden;
    public SpielStand spielstand;
    public KartenDeck deck;
    public boolean testModus;


    // Konstruktor
    public Partie(Turnier turnier, Regel regel, List<Spieler> spieler, KartenDeck deck, boolean testModus) {
        this.turnier = turnier;
        this.regel = regel;
        this.spieler = spieler;
        rundenzahl = 0;
        this.spielstand = new SpielStand(spieler);
        maxRunden = 11;
        this.deck = deck;
        aktuelleRunde = null;
        this.testModus = testModus;
    }

    //Getter und Setter
    public SpielStand getSpielstand() {
        return spielstand;
    }

    public int getRundenzahl() {
        return rundenzahl;
    }
    public void setRundenzahl(int rundenzahl) {
        this.rundenzahl = rundenzahl;
    }
    public Regel getRegel() {
        return regel;
    }
    public Runde getAktuelleRunde() {
        return aktuelleRunde;
    }
    public int getPunkteZuSpieler(Spieler spieler){
        return spielstand.getPunkte(spieler);
    }

    // Methoden

    /**
     * startet die erste Runde und verteilt Karten
     */
    public void starte() {
        verteileKarten();
        naechsteRunde();
    }


    /**
     * Bestimmt Trumpf und Spielmodus der Partie durch Ziehen einer Trumpfkarte.
     * @return die Trumpfkarte der Partie.
     */
    public Karte entscheideRegelTrumpf(){
        TrumpfAuswahl auswahl = zieheTrumpf(deck);
        this.regel = auswahl.getRegel();
        return auswahl.getTrumpfkarte();
    }


    /**
     * Gibt die SpielerHand eines Spielers zurück.
     * @param nickname
     * @return SpielerHand
     */
    public List<Karte> getHandVonSpieler(String nickname){
        for(Spieler s : spieler){
            if(s.getNickname().equals(nickname)){
                return s.getSpielerHand().getHand();
            }
        }
        return null;
    }

    /**
     * Diese Methode drueckt die Karte auf der Hand eines Spielers.
     * Die Karte wird bei SpielerHand.gedrueckteKarte des jeweiligen Spielers hinterlegt
     * @param nickname Spieler der seine Karte druecken will
     * @param karte die gedrueckt werden soll
     */
    public void drueckeKarte(String nickname, Karte karte){
        for(Spieler s : spieler){
            if(s.getNickname().equals(nickname)){
                s.getSpielerHand().karteDruecken(karte);
            }
        }
    }


    /**
     * Checkt, ob alle Spieler in der Partie gedrueckt haben.
     * @return true, wenn alle gedrueckt haben.
     */
    public boolean alleGedrueckt(){
        for(Spieler s : spieler){
            if(s.spielerHand.getGedrueckteKarte() == null){
                return false;
            }
        }
        return true;
    }





    /**
     * erhöht die Rundenzahl und startet eine neue Runde. Ob bereits 12 Runden gespielt
     * wurden, wird auch geprüft und gegebenenfalls die Partie beendet.
     */
    public void naechsteRunde(){
        if (rundenzahl <= 11) {
            rundenzahl++;
            Spieler ersterSpieler;

            if(rundenzahl == 1){
                ersterSpieler = this.bestimmeErsterSpieler();
            }else {
                ersterSpieler = aktuelleRunde.stichGewinner;
            }

            aktuelleRunde = new Runde(this, regel, rundenzahl, spieler, ersterSpieler);
            aktuelleRunde.startRunde();
        }
    }

    /**
     * Der erste Spieler wird durch Zufall ausgewählt.
     * @return den ersten Spieler.
     */
    public Spieler bestimmeErsterSpieler() {
        Random random = new Random();
        return spieler.get(random.nextInt(spieler.size()));
    }

    /**
     * Bestimmt Sieger und Siegpunkte und aktualisiert damit die Siegpunkteliste im Turnier
     */
    public void beende() {
        Map<Spieler, Integer> punkte = new HashMap<>();
        List<Spieler> sieger = new ArrayList<>();

        for (Spieler spieler : spieler) {
            int spielPunkte = spielstand.getPunkte(spieler);
            Karte gedrueckteKarte = spieler.spielerHand.getGedrueckteKarte();
            int gedruecktePunkte = regel.getKartePunkte(gedrueckteKarte);
            int endgueltigePunkte = spielPunkte + gedruecktePunkte;
            punkte.put(spieler, endgueltigePunkte);
        }
        boolean regelAngewendet = false;
        //Regel 1
        for (Map.Entry<Spieler, Integer> entry : punkte.entrySet()) {
            if (entry.getValue() == 157) {
                sieger.add(entry.getKey());
                regelAngewendet = true;
            }
        }
        //Regel 2
        if(!regelAngewendet) {
            for (Map.Entry<Spieler, Integer> entry : punkte.entrySet()) {
                if (entry.getValue() >= 100) {
                    for (Map.Entry<Spieler, Integer> entry2 : punkte.entrySet()) {
                        if (entry2.getValue() < 100) {
                            sieger.add(entry2.getKey());
                        }
                        regelAngewendet = true;
                    }

                }
            }
        }
        //Regel3/4
        if (!regelAngewendet) {
            int max = Collections.max(punkte.values());
            int min = Collections.min(punkte.values());

            List<Spieler> maxSpieler = punkte.entrySet().stream()
                    .filter(e -> e.getValue() == max)
                    .map(Map.Entry::getKey)
                    .toList();

            List<Spieler> minSpieler = punkte.entrySet().stream()
                    .filter(e -> e.getValue() == min)
                    .map(Map.Entry::getKey)
                    .toList();

            if (maxSpieler.size() > 1) {
                sieger.add(minSpieler.getFirst());
            } else if (minSpieler.size() > 1) {
                sieger.add(maxSpieler.getFirst());
            } else {
                    sieger.add(minSpieler.getFirst());
                    sieger.add(maxSpieler.getFirst());
                }
        }

        if (sieger.size() == 1) {
            turnier.aktualisiereSiegpunkteliste(sieger.getFirst(),2);
        }
        else if (sieger.size() == 2) {
            Map<Spieler, Integer> siegpunkte = turnier.getSiegPunkteListe();
            int spieler1punkte = siegpunkte.get(sieger.getFirst());
            int spieler2punkte = siegpunkte.get(sieger.get(1));
            if (spieler1punkte == 6 && spieler2punkte == 6) {
                if (punkte.get(sieger.getFirst()) > punkte.get(sieger.get(1))) {
                    turnier.aktualisiereSiegpunkteliste(sieger.get(1), -1);
                    sieger.remove(1);
                    sieger.removeFirst();
                } else if (punkte.get(sieger.getFirst()) < punkte.get(sieger.get(1))) {
                    turnier.aktualisiereSiegpunkteliste(sieger.getFirst(), -1);
                    sieger.remove(1);
                    sieger.removeFirst();
                }
            } else {
                for (Spieler spieler : sieger) {
                    turnier.aktualisiereSiegpunkteliste(spieler, 1);
                }
            }
        }

        leereGedrueckte(spieler);

        if (turnier.istBeendet()) {
            turnier.setGewinner(turnier.getSieger());
        }
        else turnier.starteNaechstePartie();
    }

    /**
     * Gibt jedem Spieler seine Karten auf die Hand
     */
    public void verteileKarten() {
        //Hat sich für das Test debuggen als sinnvoll erwiesen
        for (Spieler spieler : spieler) {
            SpielerHand spielerHand = spieler.getSpielerHand();
        }

        deck.shuffle();

        for(int i = 0; i < 3; i++){
            for(Spieler spieler : spieler){
                deck.ziehen(spieler.spielerHand);
            }
        }
    }

    /**
     * Kann einem Spieler eine neue Punktzahl im SpielStand zuweisen
     * @param spieler der neue Punkte bekommt
     * @param neuePunkte ist die neue Punktzahl
     */
    public void aktualisierePunkte(Spieler spieler, int neuePunkte) {
        int altePunkte = spielstand.getPunkte(spieler);
        spielstand.setPunkte(spieler, altePunkte + neuePunkte);
    }


    /**
     * Setzt das Attribut SpielerHand.gedrueckteKarte fuer jeden Spieler wieder auf null zurueck.
     * @param spielerListe
     */
    public void leereGedrueckte(List<Spieler> spielerListe) {
        for(Spieler s : spielerListe){
            if(s.spielerHand.getGedrueckteKarte() != null){
                s.spielerHand.gedrueckteKarte = null;
            }
        }
    }


}
