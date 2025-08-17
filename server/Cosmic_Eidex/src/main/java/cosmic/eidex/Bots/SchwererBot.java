package cosmic.eidex.Bots;

import cosmic.eidex.spielmodell.*;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;

import java.util.List;
import java.util.Random;

/**
 * Ein Bot mit erweiterter Spielstrategie im Spiel Eidex.
 * Er entscheidet sich vor dem Spiel für eine High- oder Low-Strategie
 * und passt sein Verhalten entsprechend an.
 */
@Entity
@DiscriminatorValue("hard")
public class SchwererBot extends Bot {

    @Transient
    private final Random random = new Random();
    @Transient
    private int counter = 0;
    @Transient
    private String strat;

    public SchwererBot(String nickname) {
        super(nickname);
        this.setTyp("hard");
    }

    public SchwererBot(){
        super();
        this.setTyp("easy");
    }

    public int getCounter(){
        return counter;
    }

    public void setCounter(int punkte){
        counter += punkte;
    }

    public String getStrat(){
        return strat;
    }

    public void setStrat(String strat){
        this.strat = strat;
    }

    public void entscheideStrategie( Regel regel) {
        int handwert = this.getSpielerHand().getKartenPunkteHand(regel);
        if (handwert > 25) {
            setStrat("high");
        }else {
            setStrat("low");
        }
    }

    /**
     * Führt die "low"-Strategie aus, bei der der Bot möglichst wenige Punkte sammeln will.
     * Versucht, als Nichtletzter die niedrigste Karte zu spielen. Ist der Bot als Letzter dran,
     * wird – falls möglich – eine verlierende Karte mit möglichst hohem Wert gespielt,
     * ansonsten eine zufällige.
     *
     * @param regel           Die aktuellen Spielregeln.
     * @param stich           Der aktuelle Stich mit bereits gespielten Karten.
     * @param gueltigeKarten  Alle Karten, die gemäß den Regeln gespielt werden dürfen.
     * @return Die vom Bot gespielte Karte.
     */
    private Karte lowStrategie(Regel regel, StichStapel stich, List<Karte> gueltigeKarten) {

        //Nicht als letzter dran
        if(stich.gespielteKarten.size() < 2){

            Karte karte = regel.getNiedrigsteKarte(gueltigeKarten);
            spielerHand.karteAusspielen(karte);
            return karte;

            //Als letzter dran
        }else {

            List<Karte> verliererKarten = regel.kartenDieStichVerlieren(stich, gueltigeKarten);

            //Stich kann noch verloren werden
            if (!verliererKarten.isEmpty()){

                Karte karte = regel.getHoechsteKarte(verliererKarten);
                spielerHand.karteAusspielen(karte);
                return karte;


                //Stich kann nur gewonnen werden
            }else {

                Karte karte = gueltigeKarten.get(random.nextInt(0, gueltigeKarten.size()));
                spielerHand.karteAusspielen(karte);
                return karte;

            }

        }

    }


    /**
     * Führt die "high"-Strategie aus, bei der der Bot versucht, möglichst viele Punkte
     * zu sammeln, ohne 100 zu überschreiten.
     * Wenn genügend Punkte erreicht wurden, wechselt der Bot zur "low"-Strategie.
     * Ansonsten wird versucht, als Nichtletzter die höchste Karte oder als Letzter
     * eine gewinnende Karte mit niedrigem Wert zu spielen. Falls kein Gewinn möglich ist,
     * wird eine zufällige Karte gespielt.
     *
     * @param regel           Die aktuellen Spielregeln.
     * @param stich           Der aktuelle Stich.
     * @param gueltigeKarten  Die aktuell spielbaren Karten.
     * @return Die vom Bot gespielte Karte.
     */
    private Karte highStrategie(Regel regel, StichStapel stich, List<Karte> gueltigeKarten) {
        //Gewuenschte punkte bereits erreicht
        if(getCounter() >= 79 - regel.getKartePunkte(spielerHand.gedrueckteKarte)){

            return lowStrategie(regel, stich, gueltigeKarten);

        //Noch nicht gewuenschte punkte erreicht
        }else {

            //Nicht als letzter dran
            if(stich.gespielteKarten.size() < 2){

                Karte karte = regel.getHoechsteKarte(gueltigeKarten);
                spielerHand.karteAusspielen(karte);
                setCounter(regel.getKartePunkte(karte));
                return karte;

            //Als letzter dran
            }else{

                List<Karte> gewinnerKarten = regel.kartenDieStichGewinnen(stich, gueltigeKarten);

                //Stich kann noch gewonnen werden
                if(!gewinnerKarten.isEmpty()){

                    Karte karte = regel.getNiedrigsteKarte(gewinnerKarten);
                    spielerHand.karteAusspielen(karte);
                    setCounter(regel.getKartePunkte(karte));
                    return karte;


                //Stich kann nur verloren werden
                }else {

                    Karte karte = gueltigeKarten.get(random.nextInt(0, gueltigeKarten.size()));
                    spielerHand.karteAusspielen(karte);
                    setCounter(regel.getKartePunkte(karte));
                    return karte;

                }

            }

        }

    }

    /**
     * Wähle eine Karte aus der Hand zum Drücken (Hängt mit der Strategie ab).
     */
    public Karte drueckeKarte(Regel regel) {

        //Karte mit meisten Punkten druecken
        if(getStrat().equals("high")){

            Karte karteZuDruecken = regel.getHoechsteKarte(spielerHand.getHand());
            //spielerHand.karteDruecken(karteZuDruecken);
            setCounter(regel.getKartePunkte(karteZuDruecken));
            return karteZuDruecken;

        //Hoechste karte die 0 Punkte wert ist druecken
        } else{

            List<Karte> nuller = spielerHand.getAlleKartenWertNull(regel);
            Karte hoechsteNuller = regel.getHoechsteKarte(nuller);
            //spielerHand.karteDruecken(hoechsteNuller);
            return hoechsteNuller;

        }
    }

    /**
     * Entscheidet mit einer Strategie eine gültige Karte bzgl. der gegebenen Regel und Stapel.
     * @param regel Aktuelle Regel (Trumpf etc.)
     * @param stich Aktueller StichStapel
     * @return entschiedene Karte
     */
    public Karte zugSpielen(Regel regel, StichStapel stich) {

        List<Karte> gueltigeKarten = regel.getGueltigeKarten(spielerHand, stich);

        //Versuchen 79-99 Punkte zu machen
        if(getStrat().equals("high")){

            return highStrategie(regel, stich, gueltigeKarten);

        //Versuchen weniger als 26 punkte zu machen
        } else{

            return lowStrategie(regel, stich, gueltigeKarten);

        }

    }

}