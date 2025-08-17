package cosmic.eidex.Service.SpielNachrichten;

/**
 * Nachricht um Spielstart an Server zu senden
 */
public class SpielraumStartNachricht implements SpielraumNachricht {

    private Long raumId;
    private String spieler;

    public SpielraumStartNachricht(Long raumId, String spieler) {
        this.raumId = raumId;
        this.spieler = spieler;
    }

    @Override
    public Long getRaumId() {
        return raumId;
    }

    public void setRaumId(Long raumId) {
        this.raumId = raumId;
    }

    public String getSpieler() {
        return spieler;
    }
}
