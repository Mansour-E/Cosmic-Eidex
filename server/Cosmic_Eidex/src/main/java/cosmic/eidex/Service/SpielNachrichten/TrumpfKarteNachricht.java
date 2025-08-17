package cosmic.eidex.Service.SpielNachrichten;

import cosmic.eidex.spielmodell.Karte;

/**
 * Nachricht um Trumpfkarte an Client zu senden
 */
public class TrumpfKarteNachricht implements SpielraumNachricht{

    private Long raumId;
    private Karte trumpfKarte;


    public TrumpfKarteNachricht(Long raumId, Karte trumpfKarte) {
        this.raumId = raumId;
        this.trumpfKarte = trumpfKarte;
    }

    @Override
    public Long getRaumId() {
        return raumId;
    }

    public void setRaumId(Long raumId) {
        this.raumId = raumId;
    }

    public SpielNachrichtenTyp getTyp() {
        return SpielNachrichtenTyp.TRUMPF;
    }

    public Karte getTrumpfKarte() {
        return trumpfKarte;
    }
}
