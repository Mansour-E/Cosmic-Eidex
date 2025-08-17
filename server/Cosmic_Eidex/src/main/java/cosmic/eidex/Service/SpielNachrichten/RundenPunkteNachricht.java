package cosmic.eidex.Service.SpielNachrichten;

/**
 * Nachricht um Rundenergebnisse an Client zu senden
 */
public class RundenPunkteNachricht implements SpielraumNachricht{

    private Long raumId;
    private String spieler;
    private int punkte;

    public RundenPunkteNachricht(Long raumId, String spieler, int punkte) {
        this.raumId = raumId;
        this.spieler = spieler;
        this.punkte = punkte;
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

    public void setSpieler(String spieler) {
        this.spieler = spieler;
    }

    public int getPunkte() {
        return punkte;
    }

    public void setPunkte(int punkte) {
        this.punkte = punkte;
    }
}
