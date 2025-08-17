package cosmic.eidex.Service.SpielNachrichten;

/**
 * Nachricht um diverse Spielraum Informationen an Client zu senden
 */
public class InfoNachricht implements SpielraumNachricht{

    private Long raumId;
    private String infoTyp;
    private String info;

    public InfoNachricht(Long raumId, String infoTyp, String info) {
        this.raumId = raumId;
        this.infoTyp = infoTyp;
        this.info = info;
    }

    @Override
    public Long getRaumId() {
        return raumId;
    }

    public void setRaumId(Long raumId) {
        this.raumId = raumId;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getInfoTyp() {
        return infoTyp;
    }

    public void setInfoTyp(String infoTyp) {
        this.infoTyp = infoTyp;
    }
}
