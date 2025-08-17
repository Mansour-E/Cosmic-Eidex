package cosmic.eidex.Service.SpielNachrichten;

/**
 * Nachricht um OKAY fuer Kartendruecken an Client zu senden
 * Unused
 */
public class DrueckenErlaubtNachricht implements SpielraumNachricht {

    private Long raumId;

    public DrueckenErlaubtNachricht(Long raumId) {
        this.raumId = raumId;
    }

    @Override
    public Long getRaumId() {
        return raumId;
    }


    public SpielNachrichtenTyp getTyp() {
        return SpielNachrichtenTyp.DRUECKEN_ERLAUBT;
    }

}
