package cosmic.eidex.Service.SpielNachrichten;

import cosmic.eidex.Service.Spielstatus;

/**
 * Nachricht um Status des Spielraums an Client zu senden
 */
public class SpielStatusNachricht implements SpielraumNachricht{

    private Long raumId;
    private Spielstatus status;

    public SpielStatusNachricht(Long raumId, Spielstatus status) {
        this.raumId = raumId;
        this.status = status;
    }

    @Override
    public Long getRaumId() {
        return raumId;
    }

    public Spielstatus getStatus() {
        return status;
    }


    public SpielNachrichtenTyp getTyp() {
        return SpielNachrichtenTyp.DRUECKEN_ERLAUBT;
    }
}
