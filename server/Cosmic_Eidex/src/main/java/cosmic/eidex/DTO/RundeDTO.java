package cosmic.eidex.DTO;

import cosmic.eidex.spielmodell.Regel;
import cosmic.eidex.spielmodell.StichStapel;

/**
 * DTO - Data Transfer Object, das der Controller dem Client übergeben kann.
 * RundeDTO kann verwendet werden, um Rundeninformationen an die Clients zu übertragen.
 */
public class RundeDTO {

    public long raumid;
    public Regel regel;
    public Integer runde;
    public String aktuellerSpieler;

    /**
     * Konstruktor für RundeDTO
     * @param raumid der Raum um den es sich handelt
     * @param regel die aktuelle Regel
     * @param runde die aktuelle Runde
     * @param aktuellerSpieler der aktuelle Spieler
     */
    public RundeDTO(long raumid, Regel regel, Integer runde, String aktuellerSpieler) {
        this.raumid = raumid;
        this.regel = regel;
        this.runde = runde;
        this.aktuellerSpieler = aktuellerSpieler;
    }

    public long getRaumid() {
        return raumid;
    }

    public String getAktuellerSpieler() {
        return aktuellerSpieler;
    }

    public Regel getRegel() {
        return regel;
    }

    public Integer getRunde() {
        return runde;
    }

    public void setRaumid(long raumid) {
        this.raumid = raumid;
    }

    public void setRegel(Regel regel) {
        this.regel = regel;
    }

    public void setRunde(Integer runde) {
        this.runde = runde;
    }
}
