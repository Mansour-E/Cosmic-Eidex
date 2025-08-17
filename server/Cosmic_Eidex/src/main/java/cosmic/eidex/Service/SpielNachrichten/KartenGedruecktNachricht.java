package cosmic.eidex.Service.SpielNachrichten;

import cosmic.eidex.spielmodell.Karte;

import java.util.List;

/**
 * Nachricht um gedrueckte Karte an Server zu senden
 */
public class KartenGedruecktNachricht implements SpielraumNachricht{

    private Long raumId;
    private String spieler;
    private Karte gedrueckteKarte;

    public KartenGedruecktNachricht(Long raumId, String spieler, Karte gedrueckteKarte) {
        this.raumId = raumId;
        this.spieler = spieler;
        this.gedrueckteKarte = gedrueckteKarte;
    }

    @Override
    public Long getRaumId() {
        return raumId;
    }

    public void setRaumId(Long raumId) {
        this.raumId = raumId;
    }


    public SpielNachrichtenTyp getTyp() {
        return SpielNachrichtenTyp.KARTEN_GEDRUECKT;
    }

    public String getSpieler() {
        return spieler;
    }

    public Karte getGedrueckteKarte() {
        return gedrueckteKarte;
    }
}
