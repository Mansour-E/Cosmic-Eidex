package cosmic.eidex.Service.SpielNachrichten;

import cosmic.eidex.spielmodell.Runde;
import cosmic.eidex.spielmodell.StichStapel;

/**
 * Nachricht um Stichstapel an Client zu senden
 * Unused wegne Zyklen
 */
public class RundeStichNachricht implements SpielraumNachricht{

    private Long raumId;
    private Runde runde;
    private StichStapel stichStapel;

    public RundeStichNachricht(Long raumId, Runde runde, StichStapel stichStapel) {
        this.raumId = raumId;
        this.runde = runde;
        this.stichStapel = stichStapel;
    }

    @Override
    public Long getRaumId() {
        return raumId;
    }

    public Runde getRunde() {
        return runde;
    }

    public StichStapel getStichStapel() {
        return stichStapel;
    }

    public void setRaumId(Long raumId) {
        this.raumId = raumId;
    }

    public void setRunde(Runde runde) {
        this.runde = runde;
    }

    public void setStichStapel(StichStapel stichStapel) {
        this.stichStapel = stichStapel;
    }
}
