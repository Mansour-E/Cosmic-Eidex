package cosmic.eidex.spielmodell;

import java.util.List;
/**
 * Die Klasse Runde simuliert Eigenschaften und Methoden die zum Spielen einer Runde
 * Cosmix Eidex relevant sind. Jede Runde ist einer Partie zugeordnet.
 */
public class Runde {

    // Attribute
    public Partie partie;
    public Regel regel;  // Spieler im Turnier
    public int rundenzahl;
    public List<Spieler> spieler;
    public Spieler aktuellerSpieler;
    public Spieler stichGewinner;
    public StichStapel stichstapel;
    public int AnzahlSpielzuege;

    // Konstruktor
    public Runde (Partie partie, Regel regel, int rundenzahl, List<Spieler> spieler, Spieler aktuellerSpieler) {
        this.partie = partie;
        this.regel = regel;
        this.rundenzahl = rundenzahl;
        this.spieler = spieler;
        this.aktuellerSpieler = aktuellerSpieler;
        stichGewinner = null;
        stichstapel = new StichStapel();
        AnzahlSpielzuege = 0;

    }

    //Getter und Setter
    public int getRundenzahl() {
        return rundenzahl;
    }

    public Spieler getAktuellerSpieler() {
        return aktuellerSpieler;
    }

    public Spieler getStichGewinner() {
        return stichGewinner;
    }

    public StichStapel getStichstapel() {
        return stichstapel;
    }

    public void setRegel(Regel regel) {
        this.regel = regel;
    }

    //Methoden
    /**
     * bewerteStich bestimmt die Punktezahl des Stiches und ausserdem dessen Gewinner,
     * dieser wird auch in der Variable Stichgewinner gespeichert.
     * @return die Punktzahl des Stiches
     */
    public int bewerteStich() {
        stichGewinner = regel.bestimmeStichGewinner(stichstapel);
        int punkte = 0;
        for (Spieler spieler : spieler) {
            String wert = stichstapel.getStichKarte(spieler).getWert();
            int punktewert = 0;
            switch( regel.getRegel()) {
                case "Normal" -> {
                    if (regel.trumpf.equals(stichstapel.getStichKarte(spieler).getFarbe())) {
                            punktewert = switch (wert) {
                                case "Ass" -> 11;
                                case "Koenig" -> 4;
                                case "Dame" -> 3;
                                case "Bube" -> 20;
                                case "10" -> 10;
                                case "9" -> 14;
                                default -> 0;
                            };
                    }
                    else punktewert = switch (wert) {
                                case "Ass" -> 11;
                                case "Koenig" -> 4;
                                case "Dame" -> 3;
                                case "Bube" -> 2;
                                case "10" -> 10;
                                default -> 0;
                            };

                }
                case "Obenabe" -> punktewert = switch (wert) {
                    case "Ass" -> 11;
                    case "Koenig" -> 4;
                    case "Dame" -> 3;
                    case "Bube" -> 2;
                    case "10" -> 10;
                    default -> 0;
                };
                case "Undenufe" -> punktewert = switch (wert) {
                    case "6" -> 11;
                    case "Koenig" -> 4;
                    case "Dame" -> 3;
                    case "Bube" -> 2;
                    case "10" -> 10;
                    case "8" -> 8;
                    default -> 0;
                };
            }
            punkte += punktewert;
        }
        if (rundenzahl == 11) {
            return (punkte + 5);
        }
        else return punkte;
    }

    /**
     * starteRunde beginnt die Runde mit der Aufforderung zum ersten Spielzug
     * @return Die Aufforderung an den ersten Spieler der Runde, seine Karte zu spielen.
     */
    public String startRunde() {
        return "Runde " + rundenzahl + " startet: Spieler " + aktuellerSpieler.getNickname() + " ist am Zug!";


    }

    /**
     * getNaechsterSpieler bestimmt den Spieler der als Nächstes seine Karte ausspielen muss und setzt ihn als aktuellerSpieler.
     */
    public Spieler getNaechsterSpieler() {
        int index = spieler.indexOf(aktuellerSpieler);
        int neuerIndex = (index + 1) % spieler.size();
        AnzahlSpielzuege +=1;
        if(AnzahlSpielzuege >= 3) {
            return null;
        }
        aktuellerSpieler = spieler.get(neuerIndex);
        return aktuellerSpieler;
    }
}
