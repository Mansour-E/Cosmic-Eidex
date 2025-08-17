package cosmic.eidex.Service.SpielNachrichten;

import cosmic.eidex.spielmodell.Karte;

import java.util.List;

/**
 * Nachricht um ausgeteilte Karten an Client zu senden
 */
public class KartenAusgeteiltNachricht implements SpielraumNachricht{

    private Long raumId;
    private String empfaenger;
    private List<Karte> handkarten;

    public KartenAusgeteiltNachricht(Long raumId, String empfaenger, List<Karte> handkarten) {
        this.raumId = raumId;
        this.empfaenger = empfaenger;
        this.handkarten = handkarten;
    }

    @Override
    public Long getRaumId() {
        return raumId;
    }

    public SpielNachrichtenTyp getTyp() {
        return SpielNachrichtenTyp.KARTEN_AUSGETEILT;
    }

    public List<Karte> getHandkarten() {
        return handkarten;
    }

    public String getEmpfaenger() { return empfaenger;}

}
