package cosmic.eidex.Service.SpielNachrichten;

import cosmic.eidex.spielmodell.Karte;

import java.util.List;

/**
 * Nachricht um Liste an spielbaren Karten an Client zu senden
 */
public class GueltigeKartenNachricht implements SpielraumNachricht{

    private Long raumId;
    private String spieler;
    private List<Karte> gueltigeKarten;

    public GueltigeKartenNachricht(Long raumId, String spieler, List<Karte> gueltigeKarten) {
        this.raumId = raumId;
        this.spieler = spieler;
        this.gueltigeKarten = gueltigeKarten;
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

    public List<Karte> getGueltigeKarten() {
        return gueltigeKarten;
    }

    public void setGueltigeKarten(List<Karte> gueltigeKarten) {
        this.gueltigeKarten = gueltigeKarten;
    }
}
