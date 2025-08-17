package cosmic.eidex.Service.SpielNachrichten;

import cosmic.eidex.spielmodell.Karte;

/**
 * Nachricht um gespielte Karte an Server zu senden
 */
public class SpielzugNachricht implements SpielraumNachricht {

    private Long raumId;
    private String spieler;
    private Karte gespielteKarte;


    public SpielzugNachricht(Long raumId, String spieler, Karte gespielteKarte) {
        this.raumId = raumId;
        this.spieler = spieler;
        this.gespielteKarte = gespielteKarte;
    }

    @Override
    public Long getRaumId() {
        return raumId;
    }

    public void setRaumId(Long raumId) {
        this.raumId = raumId;
    }

    public SpielNachrichtenTyp getTyp() {
        return SpielNachrichtenTyp.SPIELZUG;
    }

    public String getSpieler() {
        return spieler;
    }

    public Karte getGespielteKarte() {
        return gespielteKarte;
    }

}
