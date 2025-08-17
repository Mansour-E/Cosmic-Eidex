package cosmic.eidex.Service.SpielNachrichten;

import java.util.Map;

/**
 * Nachricht um Partieergebnisse an Client zu senden
 */
public class PartiePunkteNachricht implements SpielraumNachricht{

    private Long raumId;
    private Map<String, Integer> partiePunkte;

    public PartiePunkteNachricht(Long raumId, Map<String, Integer> partiePunkte) {
        this.raumId = raumId;
        this.partiePunkte = partiePunkte;
    }

    @Override
    public Long getRaumId() {
        return raumId;
    }

    public void setRaumId(Long raumId) {
        this.raumId = raumId;
    }

    public Map<String, Integer> getPartiePunkte() {
        return partiePunkte;
    }

    public void setPartiePunkte(Map<String, Integer> partiePunkte) {
        this.partiePunkte = partiePunkte;
    }
}
