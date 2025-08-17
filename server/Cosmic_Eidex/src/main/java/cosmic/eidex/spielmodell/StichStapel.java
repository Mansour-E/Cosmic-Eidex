package cosmic.eidex.spielmodell;

import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Klasse fuer das StichStapel Objekt
 */
@Component
public class StichStapel {

    /**
     * gespielteKarten speichert je welcher Spieler welche Karte in dieser Runde gespielt hat
     */
    public Map<Spieler, Karte> gespielteKarten = new LinkedHashMap<>();

    public StichStapel(LinkedHashMap<Spieler, Karte> gespielteKarten) {
        this.gespielteKarten = gespielteKarten;
    }

    public StichStapel(){}


    /**
     * Getter fuer gespielteKarten
     * @return LinkedHashMap<Spieler, Karte> mit gespieltenKarten
     */
    public LinkedHashMap<Spieler, Karte> getGespielteKarten() {
        return (LinkedHashMap<Spieler, Karte>) gespielteKarten;
    }

    /**
     * Fuegt eine gespielte Karte zu Stich hinzu
     * @param spieler Wer die Karte gespielt hat
     * @param karte WElche Karte gespielt wurde
     */
    public void addGespielteKarte(Spieler spieler, Karte karte) {
        gespielteKarten.put(spieler, karte);
    }

    /**
     * Getter fuer die Karte von einem bestimmten Spieler
     * @param spieler Key fuer die Map
     * @return Karte die der Spieler ausgespielt hat
     */
    public Karte getStichKarte(Spieler spieler) {
        return gespielteKarten.get(spieler);
    }

    /**
     * Ermittelt ob der Stichstapel voll ist bzw. jeder
     * Spieler gespielt hat.
     * @return True wenn voll sonst False
     */
    public boolean istVoll() {
        return gespielteKarten.size() == 3;
    }


}
