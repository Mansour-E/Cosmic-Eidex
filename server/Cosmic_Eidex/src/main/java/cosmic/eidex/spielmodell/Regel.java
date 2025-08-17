package cosmic.eidex.spielmodell;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Klasse die die ganze Logik fuer Spielzuege enthaelt
 * Sie berücksichtigt verschiedene Spielmodi wie "Normal", "Obenabe" und "Undenufe"
 * sowie die dazugehörige Bewertung und Regeln der Karten.
 */

public class Regel {


    /**
     * trumpf ist die beim normalen Spiel vorherbestimmte Trumpffarbe. Bei anderen Modi ist trumpf leer
     * regel ist Normal, Obenabe, Undenufe
     * reihenfolgen werden fuer Bewertung der hoeheren Karte verwendet
     */
    public String trumpf;
    public String regel;
    public final List<String> reihenfolgeNormal = List.of("6", "7", "8", "10", "Dame", "Koenig", "Ass", "9", "Bube");
    public final List<String> reihenfolgeObenabe = List.of("6", "7", "8", "9", "10", "Bube", "Dame", "Koenig", "Ass");
    public final List<String> reihenfolgeUndenufe = List.of("Ass", "Koenig", "Dame", "Bube", "10", "9", "8", "7", "6");

    public Regel(String trumpf, String regel) {
        this.trumpf = trumpf;
        this.regel = regel;
    }

    public String getRegel() {
        return regel;
    }

    public String getTrumpf() {
        return trumpf;
    }

    /**
     * Bestimmt anhand der gewählten Karte den Spielmodus.
     * @param karte Karte, mit der der Modus bestimmt wird
     * @return Regel-Objekt mit entsprechendem Spielmodus und Trumpf
     */
    public static Regel entscheideRegel(Karte karte){
        if(karte.getWert().equals("Ass")){
            return new Regel("", "Obenabe");
        }
        if(karte.getWert().equals("6")){
            return new Regel("", "Undenufe");
        }

        return new Regel(karte.getFarbe(), "Normal");
    }

    /**
     * Zieht zufällig eine Trumpfkarte aus dem Deck und bestimmt den Spielmodus.
     * @param deck Kartenstapel
     * @return Regel-Objekt mit entsprechendem Spielmodus
     */

    public static TrumpfAuswahl zieheTrumpf(KartenDeck deck){
        Random rand = new Random();
        deck.shuffle();
        int stelle = rand.nextInt(35);
        Karte trumpfKarte = deck.getKarte(stelle);
        Regel regel = entscheideRegel(trumpfKarte);
        return new TrumpfAuswahl(regel, trumpfKarte);
    }

    /**
     * Vergleicht 2 Karten und sagt ob zweite Karte hoeher ist
     * @param k1 Karte 1 zum vergleichen
     * @param k2 Karte 2 zum vergleichen
     * @param reihenfolge Um ueber Index in Reihenfolge zu schauen welche Karte besser
     * @return true oder false je nachdem ob Karte 2 hoeher
     */
    public Boolean karte2IstHoeher(Karte k1, Karte k2, List<String> reihenfolge){
        return reihenfolge.indexOf(k1.getWert()) <= reihenfolge.indexOf(k2.getWert());
    }



    /**
     * Hilfsfunktion, da redundant. Entscheidet wer Stich gewonnen hat
     * @param stich StichStapel Objekt, ueber welches gesagt werden muss wer es gewonnen hat
     * @param aktuelleReihenfolge Welcher Spielmodi gespielt wird, entscheidet ueber Wertung der Karten
     * @param spielerMitHoechsterKarte Dummy Objekt von Spieler gebraucht fuer Aufruf
     * @return Spieler der den Stich gewonnen hat
     */
    public Spieler stichGewinner(Map<Spieler, Karte> stich,
                                        List<String> aktuelleReihenfolge,
                                        Spieler spielerMitHoechsterKarte){

        Iterator<Map.Entry<Spieler, Karte>> iterator = stich.entrySet().iterator();


        Map.Entry<Spieler, Karte> ersterEintrag = iterator.next();
        Karte hoechsteKarte = ersterEintrag.getValue();
        spielerMitHoechsterKarte = ersterEintrag.getKey();


        while(iterator.hasNext()) {
            Map.Entry<Spieler, Karte> aktuellerEintrag = iterator.next();
            Karte aktuelleKarte = aktuellerEintrag.getValue();

            if(aktuelleKarte.getFarbe().equals(hoechsteKarte.getFarbe())) {
                if (karte2IstHoeher(hoechsteKarte, aktuelleKarte, aktuelleReihenfolge)) {

                    hoechsteKarte = aktuelleKarte;
                    spielerMitHoechsterKarte = aktuellerEintrag.getKey();
                }
            }
        }

        return spielerMitHoechsterKarte;
    }





    /**
     * Komplette Logik fuer Bestimmung des Stichgewinners
     * @param stich StichStapel Objekt, ueber welches gesagt werden muss wer es gewonnen hat
     * @return Spieler der den Stich gewonnen hat
     */
    public Spieler bestimmeStichGewinner(StichStapel stich){

        Spieler spielerMitHoechsterKarte = null;
        List<String> aktuelleReihenfolge = reihenfolgeNormal;

        if(regel.equals("Normal")){



            Map<Spieler, Karte> habenTrumpf = new LinkedHashMap<>();

            for(Map.Entry<Spieler, Karte> eintrag : stich.gespielteKarten.entrySet()){

                Karte karte = eintrag.getValue();
                if(karte.getFarbe().equals(trumpf)){
                    habenTrumpf.put(eintrag.getKey(), karte);
                }
            }

            if(habenTrumpf.isEmpty()){

                aktuelleReihenfolge = reihenfolgeObenabe;
                spielerMitHoechsterKarte = stichGewinner(stich.gespielteKarten, aktuelleReihenfolge, spielerMitHoechsterKarte);

            }

            if(habenTrumpf.size() == 1){

                Map.Entry<Spieler, Karte> entry = habenTrumpf.entrySet().iterator().next();
                spielerMitHoechsterKarte = entry.getKey();
            }

            if(habenTrumpf.size() > 1){

                spielerMitHoechsterKarte = stichGewinner(habenTrumpf, aktuelleReihenfolge, spielerMitHoechsterKarte);

            }

        }

        if(regel.equals("Obenabe")){

            aktuelleReihenfolge = reihenfolgeObenabe;
            spielerMitHoechsterKarte = stichGewinner(stich.gespielteKarten, aktuelleReihenfolge, spielerMitHoechsterKarte);

        }


        if(regel.equals("Undenufe")){

            aktuelleReihenfolge = reihenfolgeUndenufe;
            spielerMitHoechsterKarte = stichGewinner(stich.gespielteKarten, aktuelleReihenfolge, spielerMitHoechsterKarte);
        }

        return spielerMitHoechsterKarte;

    }


    /**
     * Iteriert ueber Stich Stapel und schaut ob bereits eine Trumpf Karte gespielt wurde
     * @param stich Stich der zu ueberpruefen ist
     * @return true oder false je nachdem ob Trumpf bereits in Stich
     */
    public boolean stichHatTrumpf(StichStapel stich){

        if(!stich.gespielteKarten.isEmpty()){
            for(Map.Entry<Spieler, Karte> eintrag : stich.gespielteKarten.entrySet()){

                Karte karte = eintrag.getValue();
                if(karte.getFarbe().equals(trumpf)){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Iteriert ueber Spieler Hand und schaut ob eine Karte auf der Hand ist die die uebergebene Farbe hat
     * @param spielerHand Spieler Hand die zu ueberpruefen ist
     * @param farbe Farbe die in der Hand gesucht wird
     * @return true oder false je nachdem ob Farbe in Hand
     */
    public static boolean handHatFarbe(SpielerHand spielerHand, String farbe){

        for(Karte karte : spielerHand.getHand()){
            if(karte.getFarbe().equals(farbe)){
                return true;
            }
        }
        return false;
    }

    /**
     * Ermittelt ob Spieler nur den Trumpf-Buben in seiner Hand hat.
     * Wichtig fuer besondere Trumpf-Buben Regel
     * @param hand Die Spielerhand welche durchsucht wird
     * @return True wenn nur TrumpfBube als Trumpf sonst False
     */
    private boolean hatNurTrumpfBubeAlsTrumpf(SpielerHand hand) {
        List<Karte> trumpfkarten = hand.getHand().stream()
                .filter(k -> k.getFarbe().equals(trumpf))
                .collect(Collectors.toList());

        if (trumpfkarten.size() == 1) {
            Karte einzige = trumpfkarten.getFirst();
            return einzige.getWert().equals("Bube");
        }
        return false;
    }

    /**
     * Entscheidet ob eine Karte guetig zum spielen ist, anhand der restlichen hand und dem Stich Stapel
     * @param hand Spieler Hand aus der die Karte stammt
     * @param karte Karte die zu ueberpruefen ist
     * @param stich Stich auf den gelegt werden soll
     * @return true oder false je nachdem ob es gueltig ist die Karte zu spielen
     */
    public boolean istGueltigeKarte(SpielerHand hand, Karte karte, StichStapel stich){


        if (stich.getGespielteKarten().isEmpty()) {
            return true;
        }

        Karte ersteStichKarte = stich.gespielteKarten.entrySet().iterator().next().getValue();
        String farbeZuBedienen = ersteStichKarte.getFarbe();
        boolean spielerHatFarbeZuBedienen = handHatFarbe(hand, farbeZuBedienen);


        if(regel.equals("Normal")){

            if (karte.getFarbe().equals(trumpf)) {
                return true;
            }

            boolean trumpfImStich = this.stichHatTrumpf(stich);
            boolean spielerHatTrumpf = handHatFarbe(hand, trumpf);
            boolean nurTrumpfBube = hatNurTrumpfBubeAlsTrumpf(hand);

            if(trumpfImStich){

                if(spielerHatTrumpf && !nurTrumpfBube){
                    return karte.getFarbe().equals(trumpf);
                }else{
                    return true;
                }

            }else{

                if(spielerHatFarbeZuBedienen){
                    return karte.getFarbe().equals(farbeZuBedienen);
                }else{
                    return true;
                }
            }
        }

        if(regel.equals("Obenabe") || regel.equals("Undenufe")){

            if(spielerHatFarbeZuBedienen){
                return karte.getFarbe().equals(farbeZuBedienen);
            }else{
                return true;
            }
        }
        return false;
    }


    /**
     * Benutzt istGueltigeKarte um jede Karte in der Spielerhand zu bewerten. Wenn gueltig kommt sie in Liste
     * @param hand Spielerhand die zu ueberpruefen ist
     * @param stich Stich auf den gespielt werden soll
     * @return List<Karte> mit den gueltigen Karten fuer diesen Zug eines Spielers
     */
    public List<Karte> getGueltigeKarten(SpielerHand hand, StichStapel stich){

        List<Karte> gueltigeKarten = new ArrayList<>();

        for(Karte karte : hand.getHand()){

            if(this.istGueltigeKarte(hand, karte, stich)){
                gueltigeKarten.add(karte);
            }
        }
        return gueltigeKarten;
    }

    /**
     * Gibt alle Karten zurück, die den Stich verlieren würden.
     * @param stich aktueller Stich
     * @param hand  Handkarten
     * @return Liste verlierender Karten
     */
    public List<Karte> kartenDieStichVerlieren(StichStapel stich, List<Karte> hand){

        List<Karte> valideKarten = new ArrayList<>();

        List<String> reihenfolge = switch(this.getRegel()) {
            case "Obenabe" -> reihenfolgeObenabe;
            case "Undenufe" -> reihenfolgeUndenufe;
            default -> reihenfolgeNormal;
        };

        Spieler aktuellBester = bestimmeStichGewinner(stich);
        Karte hoechsteKarte = stich.getStichKarte(aktuellBester);

        for(Karte karte: hand){
            if(karte2IstHoeher(karte, hoechsteKarte, reihenfolge)){
                valideKarten.add(karte);
            }
        }
        return valideKarten;

    }

    /**
     * Gibt alle Karten zurück, die den Stich gewinnen würden.
     * @param stich aktueller Stich
     * @param hand  Handkarten
     * @return Liste gewinnender Karten
     */
    public List<Karte> kartenDieStichGewinnen(StichStapel stich, List<Karte> hand){

        List<Karte> valideKarten = new ArrayList<>();

        List<String> reihenfolge = switch(this.getRegel()) {
            case "Obenabe" -> reihenfolgeObenabe;
            case "Undenufe" -> reihenfolgeUndenufe;
            default -> reihenfolgeNormal;
        };

        Spieler aktuellBester = bestimmeStichGewinner(stich);
        Karte hoechsteKarte = stich.getStichKarte(aktuellBester);

        for(Karte karte: hand){
            if(karte2IstHoeher(hoechsteKarte, karte, reihenfolge)){
                valideKarten.add(karte);
            }
        }
        return valideKarten;

    }


    /**
     * Gibt die niedrigste Karte aus einer Liste zurück.
     * @param karten Kartenliste
     * @return niedrigste Karte
     */
    public Karte getNiedrigsteKarte(List<Karte> karten){

        List<String> reihenfolge = switch(this.getRegel()) {
            case "Obenabe" -> reihenfolgeObenabe;
            case "Undenufe" -> reihenfolgeUndenufe;
            default -> reihenfolgeNormal;
        };

        Iterator<Karte> iterator = karten.iterator();
        Karte niedrigsteKarte = iterator.next();

        while(iterator.hasNext()) {
            Karte aktuelleKarte = iterator.next();

            if(!karte2IstHoeher(niedrigsteKarte, aktuelleKarte, reihenfolge)){
                niedrigsteKarte = aktuelleKarte;

            }
        }

        return niedrigsteKarte;

    }

    /**
     * Gibt die höchste Karte aus einer Liste zurück.
     * @param karten Kartenliste
     * @return höchste Karte
     */
    public Karte getHoechsteKarte(List<Karte> karten){

        List<String> reihenfolge = switch(this.getRegel()) {
            case "Obenabe" -> reihenfolgeObenabe;
            case "Undenufe" -> reihenfolgeUndenufe;
            default -> reihenfolgeNormal;
        };

        Iterator<Karte> iterator = karten.iterator();
        Karte hoechsteKarte = iterator.next();

        while(iterator.hasNext()) {
            Karte aktuelleKarte = iterator.next();

            if(karte2IstHoeher(hoechsteKarte, aktuelleKarte, reihenfolge)){
                hoechsteKarte = aktuelleKarte;

            }
        }

        return hoechsteKarte;
    }

    /**
     * Gibt die Punktzahl einer bestimmten Karte je nach Modus zurück.
     * @param karte zu bewertende Karte
     * @return Punktewert der Karte
     */
    public int getKartePunkte(Karte karte){
        int punkte = 0;

        switch( this.getRegel()) {
            case "Normal" -> {
                String wert = karte.getWert();
                String farbe = karte.getFarbe();
                if(this.trumpf.equals(farbe)){
                    punkte = switch (wert) {
                        case "Ass" -> 11;
                        case "Koenig" -> 4;
                        case "Dame" -> 3;
                        case "Bube" -> 20;
                        case "10" -> 10;
                        case "9" -> 14;
                        default -> 0;
                    };
                }else {
                    punkte = switch (wert) {
                        case "Ass" -> 11;
                        case "Koenig" -> 4;
                        case "Dame" -> 3;
                        case "Bube" -> 2;
                        case "10" -> 10;
                        default -> 0;
                    };
                }

            }
            case "Obenabe" -> {
                String wert = karte.getWert();
                punkte = switch (wert) {
                    case "Ass" -> 11;
                    case "Koenig" -> 4;
                    case "Dame" -> 3;
                    case "Bube" -> 2;
                    case "10" -> 10;
                    case "8" -> 8;
                    default -> 0;
                };
            }

            case "Undenufe" -> {
                String wert = karte.getWert();
                punkte = switch (wert) {
                    case "6" -> 11;
                    case "Koenig" -> 4;
                    case "Dame" -> 3;
                    case "Bube" -> 2;
                    case "10" -> 10;
                    case "8" -> 8;
                    default -> 0;
                };

            }
        }
        return punkte;
    }

}
