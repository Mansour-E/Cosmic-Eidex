package cosmic.eidex.cont;

import cosmic.eidex.Bots.Bot;
import cosmic.eidex.Bots.EinfacherBot;
import cosmic.eidex.Bots.SchwererBot;
import cosmic.eidex.DTO.SpielraumDTO;
import cosmic.eidex.Lobby.Spielraum;
import cosmic.eidex.Service.SpielNachrichten.KartenGedruecktNachricht;
import cosmic.eidex.Service.SpielNachrichten.TrumpfKarteNachricht;
import cosmic.eidex.Service.SpielraumService;
import cosmic.eidex.Service.SpielNachrichten.SpielraumStartNachricht;
import cosmic.eidex.Service.SpielNachrichten.SpielzugNachricht;
import cosmic.eidex.spielmodell.Spieler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST-Controller für Spielraum-Funktionen wie Erstellen, Beitreten und Spielstart.
 */
@RestController
@RequestMapping("/spielraum")
public class SpielraumCont {

    private final SimpMessagingTemplate messagingTemplate;
    private final SpielraumService spielraumService;

    @Autowired
    public SpielraumCont(SpielraumService spielraumService, SimpMessagingTemplate messagingTemplate) {
        this.spielraumService = spielraumService;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Erstellt einen neuen Spielraum.
     */
    @PostMapping("/erstelle")
    public SpielraumDTO erstelleSpiel(@RequestParam String name, @RequestParam(required = false) String passwort) {
        Spielraum neuerRaum = spielraumService.createSpielraum(name, passwort);
        return new SpielraumDTO(neuerRaum.getId(), neuerRaum.getName(), neuerRaum.getSpieler(), neuerRaum.getPlatzMap());
    }

    /**
     * Spieler tritt einem Spielraum bei.
     */
    @PostMapping("/beitreten")
    public boolean beitreten(@RequestParam Long id, @RequestBody Spieler spieler) {
        return spielraumService.beitreten(id, spieler);
    }

    /**
     * Bot tritt einem Spielraum bei.
     */
    @PostMapping("/beitretenboteinfach")
    public boolean beitretenBotEinfach(@RequestParam Long id, @RequestBody EinfacherBot bot) {
        return spielraumService.beitretenBotEinfach(id, bot);
    }

    /**
     * Bot tritt einem Spielraum bei.
     */
    @PostMapping("/beitretenbotschwer")
    public boolean beitretenBotSchwer(@RequestParam Long id, @RequestBody SchwererBot bot) {
        return spielraumService.beitretenBotSchwer(id, bot);
    }

    /**
     * Startet das Spiel in einem Spielraum.
     */
    @MessageMapping("/spielraumstart/{raumId}")
    @SendTo("/topic/spielraumstart/{raumId}")
    public SpielraumStartNachricht starteSpiel(@DestinationVariable Long raumId,
                                               SpielraumStartNachricht nachricht) {
        nachricht.setRaumId(raumId);
        spielraumService.starteSpiel(raumId);
        return nachricht;
    }


    /**
     * Gibt alle Spielräume als DTO zurück.
     */
    @GetMapping("/alle")
    public List<SpielraumDTO> getAlleSpielraeume() {
        List<Spielraum> alleSpielraeume = spielraumService.getAlleSpielraeume();
        spielraumService.addAlleRaeume(alleSpielraeume);
        return spielraumService.getAlleSpielraeume().stream()
                .map(r -> new SpielraumDTO(r.getId(), r.getName(), r.getSpieler(), r.getPlatzMap()))
                .toList();
    }

    /**
     * Gibt die Namen aller Spielräume zurück.
     */
    @GetMapping("/spielraumnamen")
    public List<String> getSpielraumNames() {
        return spielraumService.getAlleSpielraeume().stream()
                .map(Spielraum::getName)
                .toList();
    }

    /**
     * Gibt einen bestimmten Spielraum per ID zurück.
     */
    @GetMapping
    public SpielraumDTO getSpielraumById(@RequestParam Long id) {
        return spielraumService.getSpielraumById(id)
                .map(raum -> new SpielraumDTO(raum.getId(), raum.getName(), raum.getSpieler(), raum.getPlatzMap()))
                .orElseThrow(() -> new RuntimeException("Spielraum nicht gefunden mit ID: " + id));
    }

    /**
     * Bekommt Spieler aus Datenbank zu Name
     * @param name Name des Spielers der gesucht wird
     * @return Gefundener Spieler
     */
    @PostMapping ("/spieler")
    public Spieler getSpielerMitNickname(@RequestParam String name){
        Spieler spieler = spielraumService.getSpielerNickname(name);
        return spieler;
    }




    /**
     * Sendet alle Spielräume per WebSocket an /topic/spielraum.
     */
    @MessageMapping("/spielraum/alle")
    @SendTo("/topic/spielraum")
    public List<Spielraum> sendeAlleSpielraeume() {
        return spielraumService.getAlleSpielraeume();
    }

    /**
     * Spieler verlässt den Spielraum.
     */
    @DeleteMapping("/verlassen")
    public boolean verlasseSpielraum(@RequestParam Long id, @RequestParam String nickname) {
        return spielraumService.verlasse(id, nickname);
    }

    /**
     * Setzt den Bereit-Status eines Spielers.
     */
    @PostMapping("/bereit")
    public void setBereitStatus(@RequestParam("raumId") Long raumId,
                                @RequestParam String nickname,
                                @RequestParam boolean bereit) {
        spielraumService.setBereitStatus(raumId, nickname, bereit);
    }

    /**
     * Gibt eine SpielzugNachricht weiter und laesst diese verarbeiten
     */
    @MessageMapping("/spielraumzug/{raumId}")
    @SendTo("/topic/spielraumzug/{raumId}")
    public SpielzugNachricht sendeSpielzug(@DestinationVariable Long raumId, SpielzugNachricht spielzug) {
        spielzug.setRaumId(raumId);
        spielraumService.verarbeiteSpielzug(spielzug.getRaumId(), spielzug.getSpieler(), spielzug.getGespielteKarte());
        return spielzug;
    }

    /**
     * Gibt eine DrueckenNachricht weiter und laesst diese verarbeiten
     */
    @MessageMapping("/spielraumdruecken/{raumId}")
    @SendTo("/topic/spielraumdruecken/{raumId}")
    public KartenGedruecktNachricht sendeGedrueckteKarte(@DestinationVariable Long raumId, KartenGedruecktNachricht gedrueckteKarteNachricht) {
        gedrueckteKarteNachricht.setRaumId(raumId);
        spielraumService.verarbeiteGedrueckteKarte(gedrueckteKarteNachricht.getRaumId(), gedrueckteKarteNachricht.getSpieler(), gedrueckteKarteNachricht.getGedrueckteKarte());
        return gedrueckteKarteNachricht;
    }


}
