package cosmic.eidex.Service;

import cosmic.eidex.Bots.Bot;
import cosmic.eidex.Bots.EinfacherBot;
import cosmic.eidex.Bots.SchwererBot;
import cosmic.eidex.DTO.RundeDTO;
import cosmic.eidex.DTO.SpielraumDTO;
import cosmic.eidex.Lobby.Spielraum;
import cosmic.eidex.Service.SpielNachrichten.*;
import cosmic.eidex.gui.StageManager;
import cosmic.eidex.repository.SpielerRepository;
import cosmic.eidex.repository.SpielraumRepository;
import cosmic.eidex.spielmodell.*;
import org.aspectj.weaver.bcel.BcelObjectType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Service zur Verwaltung von {@link Spielraum}-Instanzen.
 * Dieser Service kümmert sich um die Erstellung, den Beitritt, das Verlassen und das Updaten von Spielräumen.
 */
@Service
public class SpielraumService {

    private final SpielraumRepository spielraumRepository;
    private final SpielerRepository spielerRepository;
    private final SpielraumPublisher spielraumPublisher;
    private final SimpMessagingTemplate messagingTemplate;
    private List<Spielraum> spielraumListe = new ArrayList<>();
    private Map<Long, Spielraum> aktiveRaeume = new ConcurrentHashMap<>();

    @Autowired
    public SpielraumService(SpielraumRepository spielraumRepository, SpielraumPublisher spielraumPublisher, SimpMessagingTemplate messagingTemplate, SpielerRepository spielerRepository) {
        this.spielraumRepository = spielraumRepository;
        this.spielraumPublisher = spielraumPublisher;
        this.messagingTemplate = messagingTemplate;
        this.spielerRepository = spielerRepository;
    }

    public void addAlleRaeume(List<Spielraum> liste){
        spielraumListe.clear();
        spielraumListe.addAll(liste);
    }

    /**
     * Erstellt einen neuen Spielraum und speichert ihn.
     * @param name     Name des Spielraums
     * @param passwort optionales Passwort zum Schutz des Spielraums
     * @return der erstellte Spielraum
     * @throws IllegalArgumentException wenn bereits ein Raum mit diesem Namen existiert
     */
    public Spielraum createSpielraum(String name, String passwort) {
        if (spielraumRepository.existsByName(name)) {
            throw new IllegalArgumentException("Ein Spielraum mit diesem Namen existiert bereits.");
        }

        Spielraum raum = new Spielraum(name);
        raum.setPasswort(passwort);
        spielraumListe.add(raum);
        spielraumPublisher.sendSpielraumUpdate(spielraumListe);
        return spielraumRepository.save(raum);
    }

    public List<Spielraum> getAlleSpielraeume() {
        return spielraumRepository.findAll();
    }

    /**
     * Sucht existierenden Spielraum anhand des Namens
     * @param name Name des Spielraums
     * @return Optional Objekt eines Spielraums
     */
    public Optional<Spielraum> getSpielraumByName(String name) {
        return spielraumRepository.findByName(name);
    }

    /**
     * Sucht existierenden Spielraum anhand der ID
     * @param id ID des Spielraums
     * @return Optional Objekt eines Spielraums
     */
    public Optional<Spielraum> getSpielraumById(Long id) {
        Optional<Spielraum> optionalRaum = spielraumRepository.findById(id);
        optionalRaum.ifPresent(raum -> {
            for (Spieler s : raum.getSpieler()) {
                s.setBereit(raum.isBereit(s.getNickname()));
            }
        });
        return optionalRaum;
    }


    /**
     * Fügt einem Spielraum einen Spieler hinzu.
     * @param raumId  ID des Spielraums
     * @param spieler der beitretende Spieler
     * @return true, wenn Beitritt erfolgreich war
     */
    public boolean beitreten(Long raumId, Spieler spieler) {
        Optional<Spielraum> optionalRaum = spielraumRepository.findById(raumId);
        if (optionalRaum.isEmpty()) {
            return false;
        }

        Spielraum raum = optionalRaum.get();
        if (!raum.getPlatzMap().containsValue(spieler.getNickname())) {
            for (int platz = 0; platz < 3; platz++) {
                if (!raum.getPlatzMap().containsKey(platz)) {
                    raum.getPlatzMap().put(platz, spieler.getNickname());
                    break;
                }
            }
        }

        raum.getSpieler().add(spieler);
        spielraumRepository.save(raum);
        sendeUpdate(raumId, raum);
        return true;
    }


    /**
     * Fügt einem Spielraum einen Einfachen Bot hinzu.
     * @param raumId  ID des Spielraums
     * @param bot der beitretende Einfache Bot
     * @return true, wenn Beitritt erfolgreich war
     */
    public boolean beitretenBotEinfach(Long raumId, EinfacherBot bot) {
        Optional<Spielraum> optionalRaum = spielraumRepository.findById(raumId);
        if (optionalRaum.isEmpty()) {
            return false;
        }

        Spielraum raum = optionalRaum.get();
        if (!raum.getPlatzMap().containsValue(bot.getNickname())) {
            for (int platz = 0; platz < 3; platz++) {
                if (!raum.getPlatzMap().containsKey(platz)) {
                    raum.getPlatzMap().put(platz, bot.getNickname());
                    break;
                }
            }
        }
        System.out.println(bot.getNickname() + " ist einfacherbot: " + (bot instanceof EinfacherBot));
        raum.getSpieler().add(bot);
        spielraumRepository.save(raum);
        sendeUpdate(raumId, raum);
        return true;
    }

    /**
     * Fügt einem Spielraum einen Schweren Bot hinzu.
     * @param raumId  ID des Spielraums
     * @param bot der beitretende Schwere Bot
     * @return true, wenn Beitritt erfolgreich war
     */
    public boolean beitretenBotSchwer(Long raumId, SchwererBot bot) {
        Optional<Spielraum> optionalRaum = spielraumRepository.findById(raumId);
        if (optionalRaum.isEmpty()) {
            return false;
        }

        Spielraum raum = optionalRaum.get();
        if (!raum.getPlatzMap().containsValue(bot.getNickname())) {
            for (int platz = 0; platz < 3; platz++) {
                if (!raum.getPlatzMap().containsKey(platz)) {
                    raum.getPlatzMap().put(platz, bot.getNickname());
                    break;
                }
            }
        }
        System.out.println(bot.getNickname() + " ist einfacherbot: " + (bot instanceof SchwererBot));
        raum.getSpieler().add(bot);
        for (Spieler s : raum.getSpieler()) {
            if (s instanceof EinfacherBot) {
                System.out.println("EinfacherBot: " + s.getNickname());
            } else if (s instanceof SchwererBot) {
                System.out.println("SchwererBot: " + s.getNickname());
            } else {
                // Menschlicher Spieler
            }
        }
        spielraumRepository.save(raum);
        sendeUpdate(raumId, raum);
        return true;
    }

    /**
     * Entfernt einen Spieler aus einem Spielraum und löscht den Raum, wenn keine Spieler mehr vorhanden sind.
     * @param raumId   ID des Spielraums
     * @param nickname Nickname des Spielers
     * @return true, wenn der Spieler entfernt wurde
     */
    public boolean verlasse(Long raumId, String nickname) {
        Optional<Spielraum> optionalRaum = spielraumRepository.findById(raumId);
        if (optionalRaum.isEmpty()) return false;

        Spielraum raum = optionalRaum.get();
        boolean entfernt = raum.entferneSpieler(nickname);

        if (entfernt) {
            raum.getPlatzMap().entrySet().removeIf(entry -> nickname.equals(entry.getValue()));

            boolean nurBotsUebrig = raum.getSpieler().stream().allMatch(s -> s.getNickname().toLowerCase().contains("bot"));

            if (nurBotsUebrig || raum.getSpieler().isEmpty()) {
                raum.getSpieler().clear();
                spielraumRepository.delete(raum);
                spielraumListe.removeIf(spielraum -> spielraum.getId().equals(raumId));
                aktiveRaeume.remove(raumId);
            } else {
                spielraumRepository.save(raum);
            }

            sendeUpdate(raumId, raum);
            spielraumPublisher.sendSpielraumUpdate(spielraumListe);
        }

        return entfernt;
    }

    /**
     * Sucht Spieler in Datenbank anhand Name
     * @param nickname Name des Spielers der gesucht wird
     * @return gefundener Spieler
     */
    public Spieler getSpielerNickname(String nickname) {
        Optional<Spieler> spieler1 = spielerRepository.findByNickname(nickname);
        return spieler1.get();
    }

    /**
     * Sucht in einer Liste von Spielern einen Spieler anhand des Namens
     * @param liste Liste mit Spielern die durchsucht werden
     * @param name Name des gesuchten Spielers
     * @return Spieler wenn gefunden, sonst Exception
     */
    public Spieler findeSpielerMitName(List<Spieler> liste, String name) {
        return liste.stream()
                .filter(s -> s.getNickname().equals(name))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Spieler nicht gefunden: " + name));
    }

    /**
     * Bei Aenderung des Bereit-Status aufgerufen.
     * Setzt fuer jeden Spieler im Raum den Bereitstatus neu und
     * sendet dann ein DTO fuer die Bereitanzeige im GUI
     * @param raumId
     * @param raum
     */
    private void sendeUpdate(Long raumId, Spielraum raum) {
        for (Spieler s : raum.getSpieler()) {
            boolean bereit = raum.isBereit(s.getNickname());
            s.setBereit(bereit);
        }

        SpielraumDTO dto = new SpielraumDTO(raum.getId(), raum.getName(), raum.getSpieler(), raum.getPlatzMap());
        messagingTemplate.convertAndSend("/topic/imspielraum/" + raumId, dto);
    }

    /**
     * Setzt den Bereit-Status eines Spielers im Raum, aktualisiert anschließend die Spieler und sendet ein Update.
     * @param raumId   ID des Spielraums
     * @param nickname Nickname des Spielers
     * @param bereit   neuer Bereit-Status
     */
    public void setBereitStatus(Long raumId, String nickname, boolean bereit) {
        Optional<Spielraum> optionalRaum = spielraumRepository.findById(raumId);
        if (optionalRaum.isPresent()) {
            Spielraum raum = optionalRaum.get();
            System.out.println(raum.getSpieler().size() + " Spieler im Raum");

            raum.getSpieler().stream()
                    .filter(s -> s.getNickname().equals(nickname))
                    .findFirst()
                    .ifPresent(s -> s.setBereit(bereit));

            raum.setBereitStatus(nickname, bereit);

            spielraumRepository.save(raum);
            sendeUpdate(raumId, raum);
        }
    }

    /**
     * Sucht Spielraum in Liste anhand der ID
     * @param id ID des Spielraums der gesucht wird
     * @return Spielraum Objekt wenn gefunden
     */
    public Spielraum findeInSpielraumListe(Long id){
        return aktiveRaeume.get(id);
    }

    /**
     * Entfernt Spielraum aus Datenbank und Server Session
     * @param id ID des Spielraums der geloescht werden soll
     */
    public void entferneSpielraum(Long id){
        Optional<Spielraum> optionalRaum = spielraumRepository.findById(id);
        if(optionalRaum.isPresent()){
            Spielraum raum = optionalRaum.get();
            spielraumRepository.delete(raum);
            spielraumListe.removeIf(spielraum -> spielraum.getId().equals(id));
            spielraumPublisher.sendSpielraumUpdate(spielraumListe);
        }else{
            throw new IllegalArgumentException("Spielraum mit der ID " + id + " existiert nicht.");
        }
    }

    //SPIELFLUSS----------------------------------------------------------------------------------------------------------------

    /**
     * Startet das Spiel fuer einen Spielraum.
     * Serverseitiger Spielstart und verweis an naechste Methode im Spielfluss
     * @param raumId ID des Raums wo das Spiel gestartet werden soll
     */
    public void starteSpiel(Long raumId){

        Optional<Spielraum> optionalRaum = spielraumRepository.findWithSpielerById(raumId);
        Spielraum spielraum = optionalRaum.get();
        spielraum.starteSpiel();
        spielraum.setStatus(Spielstatus.TRUMPF_WIRD_GEZOGEN);
        aktiveRaeume.put(spielraum.getId(), spielraum);
        SpielStatusNachricht statusNachricht = new SpielStatusNachricht(raumId, spielraum.getStatus());
        messagingTemplate.convertAndSend("/topic/spielraumstatus/" + raumId, statusNachricht);
        zieheTrumpfkarte(raumId, spielraum);
    }

    /**
     * Startet naechste Partie und zieht die Trumpfkarte.
     * Regel fuer Partie wird festgelegt und Trumpfkarte wird an GUI gesendet.
     * Verweis dann auf naechste Methode im Spielfluss
     * @param raumId Id des Raumes in dem die Trumpfkarte gezogen wird
     * @param spielraum Spielraum in dem Trumpfkarte gezogen wird fuer faster computing ohne lookup
     */
    private void zieheTrumpfkarte(Long raumId, Spielraum spielraum) {
        spielraum.setStatus(Spielstatus.TRUMPF_WIRD_GEZOGEN);
        SpielStatusNachricht statusNachricht = new SpielStatusNachricht(raumId, spielraum.getStatus());
        messagingTemplate.convertAndSend("/topic/spielraumstatus/" + raumId, statusNachricht);

        Partie partie = spielraum.getTurnier().getAktuellePartie();

        Karte trumpfkarte = partie.entscheideRegelTrumpf();
        aktiveRaeume.put(spielraum.getId(), spielraum);
        TrumpfKarteNachricht nachricht = new TrumpfKarteNachricht(raumId, trumpfkarte);
        messagingTemplate.convertAndSend("/topic/spielraumtrumpf/" + raumId, nachricht);
        verteileKarten(raumId, spielraum);
    }

    /**
     * Verteilt Karten serverseitig und schickt diese dann an jeweiligen CLient fuer GUI
     * Verweis auf Methode fuer Bot druecken.
     * @param raumId Id des Raumes in dem die Karten verteilt werden
     * @param spielraum Spielraum in dem Karten verteilt werden fuer faster computing ohne lookup
     */
    private void verteileKarten(Long raumId, Spielraum spielraum) {
        Partie partie = spielraum.getTurnier().getAktuellePartie();
        partie.verteileKarten();
        spielraum.setStatus(Spielstatus.KARTEN_WERDEN_VERTEILT);
        SpielStatusNachricht statusNachricht = new SpielStatusNachricht(raumId, spielraum.getStatus());
        messagingTemplate.convertAndSend("/topic/spielraumstatus/" + raumId, statusNachricht);

        for (Spieler s : spielraum.getSpieler()) {
            List<Karte> hand = partie.getHandVonSpieler(s.getNickname());
            KartenAusgeteiltNachricht nachricht = new KartenAusgeteiltNachricht(raumId, s.getNickname(), hand);
            messagingTemplate.convertAndSend("/topic/spielraumausteilen/" + raumId, nachricht);
        }

        spielraum.setStatus(Spielstatus.WARTE_AUF_DRUECKEN);
        aktiveRaeume.put(spielraum.getId(), spielraum);
        SpielStatusNachricht statusNachricht2 = new SpielStatusNachricht(raumId, spielraum.getStatus());
        messagingTemplate.convertAndSend("/topic/spielraumstatus/" + raumId, statusNachricht2);
        botDruecker(raumId, spielraum);
    }

    /**
     * Falls Bots im Spiel sind entscheidet der schwere Bot seine Strategie
     * und alle Bots druecken ihre Karte mit dieser Methode.
     * Dann verweis an naechste Methode im Spielfluss
     * @param raumId Id des Raumes in welchem nach Bots geschaut wird
     * @param spielraum Spielraum in in welchem nach Bots geschaut wird fuer faster computing ohne lookup
     */
    public void botDruecker(Long raumId, Spielraum spielraum){

        Partie partie = spielraum.getTurnier().getAktuellePartie();
        for(Spieler s : spielraum.getSpieler()){
            if(s instanceof Bot){

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                Bot bot = (Bot) s;
                bot.entscheideStrategie(partie.getRegel());
                Karte card = bot.drueckeKarte(partie.getRegel());
                verarbeiteGedrueckteKarte(raumId, bot.getNickname(), card);
            }

        }
    }

    /**
     * Wird durch Karten-Click von CLient im GUI aufgerufen oder durch Server fuer Bot
     * Geklickte Karte wird auch auf Server gedrueckt.
     * Wenn alle Spieler gedrueckt haben wird erste Runde gestartet und alle Anzeige-Infos an Client geschickt.
     * Falls die ersten Spieler in der Runde Bots sind werden sie hier abgefangen und machen serverseitig ihren Zug.
     * Falls echter Spieler wird eine Liste an gueltige Karten, die er spielen darf, an ihn gesendet.
     * @param raumId Id des Raumes in welchem die Karte gedrueckt wird
     * @param spieler Name des Spielers der die Karte drueckt
     * @param karte Die zu drueckende Karte
     */
    public void verarbeiteGedrueckteKarte(Long raumId, String spieler, Karte karte){
        Spielraum spielraum = findeInSpielraumListe(raumId);
        Partie partie = spielraum.getTurnier().getAktuellePartie();

        partie.drueckeKarte(spieler, karte);
        aktiveRaeume.put(spielraum.getId(), spielraum);

        InfoNachricht infoNachricht = new InfoNachricht(raumId, "partiezahl", String.valueOf(spielraum.getTurnier().getAnzahlGespielterPartien()));
        messagingTemplate.convertAndSend("/topic/spielrauminfo/" + raumId, infoNachricht);

        if(partie.alleGedrueckt()){
            spielraum.setStatus(Spielstatus.WARTET_AUF_SPIELZUEGE);
            SpielStatusNachricht statusNachricht2 = new SpielStatusNachricht(raumId, spielraum.getStatus());
            messagingTemplate.convertAndSend("/topic/spielraumstatus/" + raumId, statusNachricht2);

            partie.naechsteRunde();
            Runde runde = partie.getAktuelleRunde();
            runde.startRunde();



            InfoNachricht infoNachrichtRundeZahl = new InfoNachricht(raumId, "rundenzahl",String.valueOf(runde.getRundenzahl()));
            messagingTemplate.convertAndSend("/topic/spielrauminfo/" + raumId, infoNachrichtRundeZahl);

            Spieler aktuellerSpieler = runde.getAktuellerSpieler();

            InfoNachricht infoNachrichtSpieler = new InfoNachricht(raumId, "aktuellerspieler", aktuellerSpieler.getNickname());
            messagingTemplate.convertAndSend("/topic/spielrauminfo/" + raumId, infoNachrichtSpieler);



            if(aktuellerSpieler instanceof Bot){
                Bot bot = (Bot) aktuellerSpieler;
                Karte card = bot.zugSpielen(partie.getRegel(), runde.stichstapel);
                verarbeiteSpielzug(raumId, bot.getNickname(), card);
                aktiveRaeume.put(spielraum.getId(), spielraum);
            }else {
                List<Karte> gueltige = partie.regel.getGueltigeKarten(aktuellerSpieler.spielerHand, runde.stichstapel);
                GueltigeKartenNachricht gueltigeKartenNachricht = new GueltigeKartenNachricht(raumId, aktuellerSpieler.getNickname(), gueltige);
                messagingTemplate.convertAndSend("/topic/spielraumgueltige/" + raumId, gueltigeKartenNachricht);
            }
        }
        aktiveRaeume.put(spielraum.getId(), spielraum);
    }

    /**
     * Methode in 2 Teilen:
     * 1. Wenn Spieler ein Client ist wird Karte die geklickt wurde serverseitig ausgespielt,
     *    Wenn Spieler ein Bot ist, wird Karte serverseitig ausgespielt und dann an die Clients geschickt
     *    fuer GUI Anzeige.
     *    Wenn der Stichstapel noch nicht voll ist, wird naechster Spieler der dran ist ermittelt.
     *    Wenn naechster Spieler ein Bot ist, entscheidet er sich fuer Karte und ruft selbe Methode erneut auf,
     *    Wenn naechster Spieler ein Client ist, dann werden die gueltigen Karten an Client geschickt.
     * 2. Wenn Stichstapel voll ist, wird Runde beendet und ausgewertet. Informationen an Clients geschickt fuer Anzeige.
     *    Wenn maximale Runenzahl noch nicht erreicht wird neue Runde gestartet und ueberprueft ob erster Spieler ein Bot ist.
     *    Wenn maximale Rundenzahl erreicht, wird Partie beendet und ausgewertet. Info fuer GUI Anzeige wird an Clients geschickt.
     *    Dann verweis auf naechste Methode im Spielfluss
     *
     * @param raumId Id des Raumes in welchem die Karte ausgespielt wird
     * @param spielerName Name des Spielers der die Karte ausgespielt
     * @param karte Die zu spielende Karte
     */
    public void verarbeiteSpielzug(Long raumId, String spielerName, Karte karte) {
        Spielraum spielraum = findeInSpielraumListe(raumId);
        Partie partie = spielraum.getTurnier().getAktuellePartie();
        Runde runde = partie.getAktuelleRunde();

        Spieler spieler = findeSpielerMitName(spielraum.getSpieler(), spielerName);
        if(spieler instanceof Bot){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            Bot bot = (Bot) spieler;
            runde.stichstapel.addGespielteKarte(bot, karte);
            SpielzugNachricht spielzugNachricht = new SpielzugNachricht(raumId, bot.getNickname(), karte);
            messagingTemplate.convertAndSend("/topic/spielraumzug/" + raumId, spielzugNachricht);
        }else {
            Karte gespielteKarte = spieler.spielerHand.karteAusspielen(karte);
            runde.stichstapel.addGespielteKarte(spieler, gespielteKarte);
        }

        aktiveRaeume.put(spielraum.getId(), spielraum);


        if (!runde.stichstapel.istVoll()) {
            Spieler naechster = runde.getNaechsterSpieler();

            InfoNachricht infoNachrichtSpieler = new InfoNachricht(raumId, "aktuellerspieler", naechster.getNickname());
            messagingTemplate.convertAndSend("/topic/spielrauminfo/" + raumId, infoNachrichtSpieler);

            if(naechster instanceof Bot){
                Bot bot = (Bot) naechster;
                Karte card = bot.zugSpielen(partie.getRegel(), runde.stichstapel);
                verarbeiteSpielzug(raumId, bot.getNickname(), card);
                aktiveRaeume.put(spielraum.getId(), spielraum);
            }else {
                List<Karte> gueltige = partie.regel.getGueltigeKarten(naechster.spielerHand, runde.stichstapel);
                GueltigeKartenNachricht gueltigeKartenNachricht = new GueltigeKartenNachricht(raumId, naechster.getNickname(), gueltige);
                messagingTemplate.convertAndSend("/topic/spielraumgueltige/" + raumId, gueltigeKartenNachricht);
            }

        } else {
            spielraum.setStatus(Spielstatus.RUNDE_BEENDET);
            SpielStatusNachricht statusNachricht = new SpielStatusNachricht(raumId, spielraum.getStatus());
            messagingTemplate.convertAndSend("/topic/spielraumstatus/" + raumId, statusNachricht);

            int punkte = runde.bewerteStich();
            Spieler gewinner = runde.getStichGewinner();

            String message = "Spieler " + gewinner.getNickname() + " hat den Stich gewonnen!";
            InfoNachricht nachricht = new InfoNachricht(raumId, "Information", message);
            messagingTemplate.convertAndSend("/topic/spielrauminfo/" + raumId, nachricht);

            partie.aktualisierePunkte(gewinner, punkte);

            RundenPunkteNachricht rundenPunkteNachricht = new RundenPunkteNachricht(raumId, gewinner.getNickname(), partie.getPunkteZuSpieler(gewinner));
            messagingTemplate.convertAndSend("/topic/spielraumrundepunkte/" + raumId, rundenPunkteNachricht);


            // neue Runde oder Partie vorbei
            if (partie.getRundenzahl() == partie.maxRunden) {

                spielraum.setStatus(Spielstatus.PARTIE_BEENDET);
                SpielStatusNachricht statusNachricht2 = new SpielStatusNachricht(raumId, spielraum.getStatus());
                messagingTemplate.convertAndSend("/topic/spielraumstatus/" + raumId, statusNachricht2);

                InfoNachricht infoNachrichtRundeZahl = new InfoNachricht(raumId, "rundenzahl", "0");
                messagingTemplate.convertAndSend("/topic/spielrauminfo/" + raumId, infoNachrichtRundeZahl);

                InfoNachricht infoNachrichtSpieler = new InfoNachricht(raumId, "aktuellerspieler", "");
                messagingTemplate.convertAndSend("/topic/spielrauminfo/" + raumId, infoNachrichtSpieler);

                SpielStand alterStand = partie.spielstand;
                Map<Spieler, Integer> echteRundenPunkte = new HashMap<>();

                for (Map.Entry<Spieler, Integer> entry : alterStand.getPunkteliste().entrySet()) {
                    Spieler s = entry.getKey();
                    int spielPunkte = entry.getValue();
                    Karte gedrueckteKarte = s.spielerHand.getGedrueckteKarte();
                    int gedruecktePunkte = partie.getRegel().getKartePunkte(gedrueckteKarte);
                    int endgueltigePunkte = spielPunkte + gedruecktePunkte;
                    echteRundenPunkte.put(s, endgueltigePunkte);
                }

                Map<Spieler, Integer> alteSiegPunkte = new HashMap<>(spielraum.getTurnier().getSiegPunkteListe());

                partie.beende();

                Map<Spieler, Integer> partieGeholtePunkte = new HashMap<>();
                Map<Spieler, Integer> neueSiegPunkte = spielraum.getTurnier().getSiegPunkteListe();


                for (Map.Entry<Spieler, Integer> entry : neueSiegPunkte.entrySet()) {
                    int sumPunkte = entry.getValue();
                    Spieler soo = entry.getKey();
                    int altePunkt = alteSiegPunkte.get(soo);
                    int geholte = sumPunkte - altePunkt;
                    partieGeholtePunkte.put(soo, geholte);
                }

                StringBuilder stringBuilder = new StringBuilder();
                Map<String, Integer> partiePunkte = new HashMap<>();
                for (Map.Entry<Spieler, Integer> entry : spielraum.getTurnier().getSiegPunkteListe().entrySet()) {
                    partiePunkte.put(entry.getKey().getNickname(), entry.getValue());
                    stringBuilder
                            .append("Spieler: ").append(entry.getKey().getNickname())
                            .append(" hat ").append(partieGeholtePunkte.get(entry.getKey())).append(" Tunierpunkte gemacht.\n")
                            .append("Mit ").append(echteRundenPunkte.get(entry.getKey())).append(" Punkten in dieser Partie. \n")
                            .append("\n");
                }

                String partieText = stringBuilder.toString();
                InfoNachricht partieTextNachricht = new InfoNachricht(raumId, "partieanzeige", partieText);
                messagingTemplate.convertAndSend("/topic/spielrauminfo/" + raumId, partieTextNachricht);


                PartiePunkteNachricht partiePunkteNachricht = new PartiePunkteNachricht(raumId, partiePunkte);
                messagingTemplate.convertAndSend("/topic/spielraumpartiepunkte/" + raumId, partiePunkteNachricht);

                InfoNachricht infoNachrichtStichLeer = new InfoNachricht(raumId, "StichStapel","leeren");
                messagingTemplate.convertAndSend("/topic/spielrauminfo/" + raumId, infoNachrichtStichLeer);


                aktiveRaeume.put(spielraum.getId(), spielraum);

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                verarbeiteNeuePartie(raumId);

            } else {

                // neue Runde
                partie.naechsteRunde();
                Runde neueRunde = partie.getAktuelleRunde();
                neueRunde.startRunde();

                spielraum.setStatus(Spielstatus.WARTET_AUF_SPIELZUEGE);
                SpielStatusNachricht statusNachricht2 = new SpielStatusNachricht(raumId, spielraum.getStatus());
                messagingTemplate.convertAndSend("/topic/spielraumstatus/" + raumId, statusNachricht2);

                InfoNachricht infoNachrichtRundeZahl = new InfoNachricht(raumId, "rundenzahl",String.valueOf(neueRunde.getRundenzahl()));
                messagingTemplate.convertAndSend("/topic/spielrauminfo/" + raumId, infoNachrichtRundeZahl);

                InfoNachricht infoNachrichtStichLeer = new InfoNachricht(raumId, "StichStapel","leeren");
                messagingTemplate.convertAndSend("/topic/spielrauminfo/" + raumId, infoNachrichtStichLeer);

                Spieler aktuellerSpieler = neueRunde.getAktuellerSpieler();

                InfoNachricht infoNachrichtSpieler = new InfoNachricht(raumId, "aktuellerspieler", aktuellerSpieler.getNickname());
                messagingTemplate.convertAndSend("/topic/spielrauminfo/" + raumId, infoNachrichtSpieler);

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                if(aktuellerSpieler instanceof Bot){
                    Bot bot = (Bot) aktuellerSpieler;
                    Karte card = bot.zugSpielen(partie.getRegel(), neueRunde.stichstapel);
                    verarbeiteSpielzug(raumId, bot.getNickname(), card);
                    aktiveRaeume.put(spielraum.getId(), spielraum);
                }else {
                    List<Karte> gueltige = partie.getRegel().getGueltigeKarten(aktuellerSpieler.spielerHand, neueRunde.stichstapel);
                    GueltigeKartenNachricht gueltigeKartenNachricht = new GueltigeKartenNachricht(raumId, aktuellerSpieler.getNickname(), gueltige);
                    messagingTemplate.convertAndSend("/topic/spielraumgueltige/" + raumId, gueltigeKartenNachricht);
                }

                aktiveRaeume.put(spielraum.getId(), spielraum);
            }
        }
    }

    /**
     * Entscheidet nach Partieende ob Tunier zuende ist oder neue Partie gestartet wird.
     * Wenn Tunier noch nicht zueende wird Methode zieheTrumpfkarte aufgerufen -> Startpunkt Partie
     * Wenn Tunier zuende wird Tunier ausgewertet, dem Sieger die Siege um eins erhoeht, alle Informationen ueber Tunierbewertung
     * an {@link ErgebnisStompClient} gesendet und dann Spielraum geloescht sowie die Bots aus der Datenbank entfernt
     * @param raumId Id des Raumes in welchem ueber neue Partie entschieden wird
     */
    private void verarbeiteNeuePartie(Long raumId) {
        Spielraum spielraum = findeInSpielraumListe(raumId);
        if (!spielraum.getTurnier().istBeendet()) {
            InfoNachricht nachricht = new InfoNachricht(raumId, "Information", "Naechste Partie beginnt");
            messagingTemplate.convertAndSend("/topic/spielrauminfo/" + raumId, nachricht);

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            zieheTrumpfkarte(raumId, spielraum);
        }else {
            spielraum.setStatus(Spielstatus.TURNIER_BEENDET);
            SpielStatusNachricht statusNachricht = new SpielStatusNachricht(raumId, spielraum.getStatus());
            messagingTemplate.convertAndSend("/topic/spielraumstatus/" + raumId, statusNachricht);

            InfoNachricht infoNachricht = new InfoNachricht(raumId, "tunierende", "");
            messagingTemplate.convertAndSend("/topic/spielrauminfo/" + raumId, infoNachricht);


            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            Spieler gewinner = spielraum.getTurnier().getGewinner();
            int alteSiege = gewinner.getSiege();
            gewinner.setSiege(alteSiege + 1);
            spielerRepository.save(gewinner);

            Map<String, Integer> siegPunkteListe = spielraum.getTurnier().getSiegPunkteListe().
                    entrySet().stream()
                    .collect(Collectors.toMap(entry -> entry.getKey().getNickname(), Map.Entry::getValue));
            TunierErgebnisNachricht tunierErgebnisse = new TunierErgebnisNachricht(raumId, siegPunkteListe, gewinner.getNickname());
            messagingTemplate.convertAndSend("/topic/spielraumendergebnis/" + raumId, tunierErgebnisse);

            aktiveRaeume.remove(spielraum.getId());
            spielerRepository.deleteAllNonSpielerFromOldSSpielraum(raumId);
            spielerRepository.updateSpielerNachSpielende(raumId);
            spielraumRepository.deleteById(raumId);

        }
    }



}
