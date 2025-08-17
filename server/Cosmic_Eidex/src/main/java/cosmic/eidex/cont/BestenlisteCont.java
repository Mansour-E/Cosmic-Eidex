package cosmic.eidex.cont;

import cosmic.eidex.spielmodell.Spieler;
import cosmic.eidex.Service.BestenlisteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * REST-Controller für die Bestenliste.
 */
@Controller
public class BestenlisteCont {

    private final BestenlisteService bestenlisteService;

    /**
     * Konstruktor für BestenlisteCont
     * @param bestenlisteService
     */
    @Autowired
    public BestenlisteCont(BestenlisteService bestenlisteService) {
        this.bestenlisteService = bestenlisteService;
    }

    /**
     * Sendet die Top 10 Spieler an alle Abonnenten von /topic/bestenliste.
     * @return Liste der besten 10 Spieler
     */
    @MessageMapping("/bestenliste/aktualisieren")
    @SendTo("/topic/bestenliste")
    public List<Spieler> sendeTop10() {
        return bestenlisteService.getTop10Spieler();
    }
}