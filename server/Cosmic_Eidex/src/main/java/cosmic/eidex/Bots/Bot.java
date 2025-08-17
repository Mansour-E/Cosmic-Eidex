package cosmic.eidex.Bots;

import cosmic.eidex.spielmodell.Karte;
import cosmic.eidex.spielmodell.Regel;
import cosmic.eidex.spielmodell.Spieler;
import cosmic.eidex.spielmodell.StichStapel;

/**
 * Abstrakte Klasse für computergesteuerte Spieler (Bots).
 * Implementierungen müssen Spielzüge und Kartendruck-Logik bereitstellen.
 */
public abstract class Bot extends Spieler {


    public Bot(String nickname) {
        super(nickname, "", 9001);
    }

    public Bot(){
        super("Bot", "", 9001);
    }

    /**
     * Entscheidet eine gültige Karte bzgl. der gegebenen Regel und Stapel.
     * @param regel Aktuelle Regel (Trumpf etc.)
     * @param stich Aktueller StichStapel
     * @return entschiedene Karte
     */
    public abstract Karte zugSpielen(Regel regel, StichStapel stich);

    /**
     * Wähle zufällig eine Karte aus der Hand zum Drücken.
     */
    public abstract Karte drueckeKarte(Regel regel);

    public void entscheideStrategie(Regel regel) {
        System.out.println("Ich bin einfacher bot");
    }
}
