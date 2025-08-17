package cosmic.eidex.Service.SpielNachrichten;

import java.util.Map;

/**
 * Nachricht um Tunierergebnisse an Client zu senden
 */
public class TunierErgebnisNachricht implements SpielraumNachricht{

    private Long raumId;
    private Map<String, Integer> tunierPunkte;
    private String gewinner;

    public TunierErgebnisNachricht(Long raumId, Map<String, Integer> tunierPunkte, String gewinner) {
        this.raumId = raumId;
        this.tunierPunkte = tunierPunkte;
        this.gewinner = gewinner;
    }

    @Override
    public Long getRaumId() {
        return raumId;
    }

    public void setRaumId(Long raumId) {
        this.raumId = raumId;
    }

    public Map<String, Integer> getTunierPunkte() {
        return tunierPunkte;
    }

    public void setTunierPunkte(Map<String, Integer> partiePunkte) {
        this.tunierPunkte = partiePunkte;
    }

    public String getGewinner() {
        return gewinner;
    }

    public void setGewinner(String gewinner) {
        this.gewinner = gewinner;
    }
}
