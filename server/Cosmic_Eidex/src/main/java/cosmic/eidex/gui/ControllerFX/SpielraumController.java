package cosmic.eidex.gui.ControllerFX;

import com.fasterxml.jackson.databind.ObjectMapper;
import cosmic.eidex.DTO.RundeDTO;
import cosmic.eidex.DTO.SpielerHandDTO;
import cosmic.eidex.Bots.EinfacherBot;
import cosmic.eidex.Bots.SchwererBot;
import cosmic.eidex.DTO.SpielraumDTO;
import cosmic.eidex.Service.SessionManager;
import cosmic.eidex.Service.SpielNachrichten.SpielraumChatNachricht;
import cosmic.eidex.Service.SpielraumStompClient;
import cosmic.eidex.Service.*;
import cosmic.eidex.Service.SpielNachrichten.*;
import cosmic.eidex.Service.SpielNachrichten.KartenGedruecktNachricht;
import cosmic.eidex.Service.SpielNachrichten.SpielraumStartNachricht;
import cosmic.eidex.Service.SpielNachrichten.SpielzugNachricht;
import cosmic.eidex.gui.StageManager;
import cosmic.eidex.spielmodell.*;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * GUI-Controller fuer den Spielraum.
 * Verwaltet die Anzeige der Spieler, Karten, das Chat-System und die Spielphasen.
 * Kommuniziert über REST und STOMP mit dem Server.
 */
@Component
public class SpielraumController {

    @FXML
    public CheckBox spieler01Bereit;

    @FXML
    public CheckBox spieler02Bereit;

    @FXML
    public CheckBox spieler03Bereit;

    @FXML
    public Button spielStartenBtn;

    @FXML
    public Label spieler01name;

    @FXML
    public Label spieler02name;

    @FXML
    public Label spieler03name;

    @FXML
    public Label spieler01punkte;

    @FXML
    public Label spieler02punkte;

    @FXML
    public Label spieler03punkte;

    @FXML
    public Label trumpf;

    @FXML
    public ImageView trumpfImage;

    @FXML
    public HBox spieler01GedruecktBox;

    @FXML
    public HBox spieler01KartenBox;

    @FXML
    public VBox spieler02GedruecktBox;

    @FXML
    public VBox spieler02KartenBox;

    @FXML
    public VBox spieler03GedruecktBox;

    @FXML
    public VBox spieler03KartenBox;

    @FXML
    public Pane spielbereich;

    @FXML
    public ListView<String> chatListView;

    @FXML
    public TextField chatTextField;

    @FXML
    public Button sendenButton;

    @FXML
    public CheckBox spieler02Bot;

    @FXML
    public CheckBox spieler03Bot;

    @FXML
    public CheckBox spieler02Bot1;

    @FXML
    public CheckBox spieler03Bot1;

    @FXML
    public Label partie;

    @FXML
    public Label runde;

    @FXML
    public Label status;

    @FXML
    public Label spieleramzug;

    @FXML
    public Label spieler01rundenpunkte;

    @FXML
    public Label spieler02rundenpunkte;

    @FXML
    public Label spieler03rundenpunkte;

    @FXML
    public Label gedruecktlabel01;

    @FXML
    public Label gedruecktlabel02;

    @FXML
    public Label gedruecktlabel03;

    @FXML
    public Label amzuglabel;

    public Long raumId;

    public final RestTemplate restTemplate = new RestTemplate();
    public final ObjectMapper objectMapper = new ObjectMapper();

    public StageManager stageManager;
    public SpielraumStompClient stompClient;

    public int meinPlatz = 0;
    public int kartenImStich = 0;
    public Spielstatus raumStatus;

    public boolean hatGedrueckt = false;
    public boolean hatGespielt = false;
    public int rundenZahl;
    public Regel regel;
    public String istDran;
    public SpielerHand eigeneHand;
    public List<Karte> gueltige;

    /**
     * Einbinden des Stagemanagers zu Verwaltung der Szenen
     * @param stageManager
     */
    @Autowired
    public void setStageManager(StageManager stageManager) {
        this.stageManager = stageManager;
    }

    /**
     * Initialisiert den Controller. Setzt Startzustand der GUI.
     */
    @FXML
    public void initialize() {
        // PreSpielPhase -> Nicht alle Elemente sind sichtbar
        trumpf.setVisible(false);
        trumpfImage.setVisible(false);
        spielbereich.setVisible(false);
        spieler01punkte.setVisible(false);
        spieler02punkte.setVisible(false);
        spieler03punkte.setVisible(false);
        spieler01GedruecktBox.setVisible(false);
        spieler01KartenBox.setVisible(false);
        spieler02GedruecktBox.setVisible(false);
        spieler02KartenBox.setVisible(false);
        spieler03GedruecktBox.setVisible(false);
        spieler03KartenBox.setVisible(false);

        spieler01rundenpunkte.setVisible(false);
        spieler02rundenpunkte.setVisible(false);
        spieler03rundenpunkte.setVisible(false);
        gedruecktlabel01.setVisible(false);
        gedruecktlabel02.setVisible(false);
        gedruecktlabel03.setVisible(false);
        amzuglabel.setVisible(false);
        spieleramzug.setVisible(false);
        partie.setVisible(false);
        runde.setVisible(false);
        raumStatus = Spielstatus.WARTET_AUF_SPIELSTART;
        status.setText(raumStatus.toString());



    }

    /**
     * Setzt die Spielraum-ID und verbindet mit dem richtigen Websocket, entsprechend der Id.
     * @param id Raum-ID
     */
    public void setRaumId(Long id) {
        this.raumId = id;
        ladeSpielraumDaten();
        if (stompClient != null) {
            stompClient.disconnect();
        }
        try {
            stompClient = new SpielraumStompClient(id, dto -> javafx.application.Platform.runLater(() ->
                    sortiereSpielerUndSetzeLabels(dto.getSpieler(), dto.getPlatzMap())),
                    this::handleChatMessage,
                    this::zeigeGespielteKarte,
                    this::starteSpiel,
                    this::zeigeTrumpfKarte,
                    this::zeigeHandKarten,
                    this::statusHandler,
                    this::zeigeGedrueckteKarte,
                    this::verarbeiteGueltige,
                    this::infoVerarbeiten,
                    this::zeigeRundenPunkte,
                    this::zeigeGewinnPunkte

            );
            stompClient.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Lädt die Spielraumdaten über REST.
     */
    public void ladeSpielraumDaten() {
        try {
            String url = "http://localhost:8080/spielraum?id=" + raumId;
            SpielraumDTO raum = restTemplate.getForObject(url, SpielraumDTO.class);
            if (raum != null) {
                System.out.println("Spielraum geladen: " + raum.getName());
                sortiereSpielerUndSetzeLabels(raum.getSpieler(), raum.getPlatzMap());
            }
        } catch (Exception e) {
            System.out.println("Fehler beim Laden des Spielraums:");
            e.printStackTrace();
        }
    }

    /**
     * Sortiert die Spieler anhand ihrer Position und updatet die UI um Namen, Punkte, Bereit-Status oder Bot-Status anzuzeigen.
     * @param spielerListe die aktuelle Spielerliste
     * @param platzMap die Platzmap die die Spielern ihre Position zuordnet.
     */
    public void sortiereSpielerUndSetzeLabels(List<Spieler> spielerListe, Map<Integer, String> platzMap) {
        if (platzMap == null || platzMap.isEmpty()) {
            System.err.println("PlatzMap ist leer oder null – keine Spielerzuordnung möglich.");
            platzMap = new HashMap<>();
            return;
        }

        String meinName = SessionManager.getNickname();
        meinPlatz = platzMap.entrySet().stream()
                .filter(e -> e.getValue().equals(meinName))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(0);

        Spieler[] plätze = new Spieler[3];
        for (int platz = 0; platz < 3; platz++) {
            String nick = platzMap.get(platz);
            if (nick != null) {
                for (Spieler s : spielerListe) {
                    if (s.getNickname().equals(nick)) {
                        plätze[platz] = s;
                        break;
                    }
                }
            }
        }

        Spieler ich = plätze[meinPlatz];
        Spieler sp2 = plätze[(meinPlatz + 1) % 3];
        Spieler sp3 = plätze[(meinPlatz + 2) % 3];

        // Spieler 1
        if (ich != null) {
            spieler01name.setText(ich.getNickname());
            spieler01punkte.setText("Gewinnpunkte: " + ich.getPunkte());
            spieler01Bereit.setDisable(false);
            spieler01Bereit.setSelected(ich.isBereit());
        } else {
            spieler01name.setText("leer");
            spieler01punkte.setText("");
            spieler01Bereit.setDisable(true);
            spieler01Bereit.setSelected(false);
        }

        // Spieler 2
        if (sp2 != null) {
            spieler02name.setText(sp2.getNickname());
            spieler02punkte.setText("Gewinnpunkte: " + sp2.getPunkte());
            spieler02Bereit.setDisable(true);
            spieler02Bereit.setSelected(sp2.isBereit());

            boolean istBot = sp2.getNickname().contains("Bot");
            if (!spieler02Bot.isDisable()) {
                spieler02Bot.setDisable(!istBot);
            }
            if (!spieler02Bot1.isDisable()) {
                spieler02Bot1.setDisable(!istBot);
            }

            // gegenseitige Deaktivierung bei Bot auf Platz 2
            if (istBot) {
                boolean isEasy = sp2.getNickname().contains("Einfacher");
                boolean isHard = sp2.getNickname().contains("Schwerer");
                if (isEasy) {
                    spieler02Bot.setSelected(true);
                    spieler02Bot1.setDisable(true);
                } else if (isHard) {
                    spieler02Bot1.setSelected(true);
                    spieler02Bot.setDisable(true);
                }
            }

        } else {
            spieler02name.setText("leer");
            spieler02punkte.setText("");
            spieler02Bereit.setDisable(true);
            spieler02Bereit.setSelected(false);
            if (!spieler02Bot.isDisable()) spieler02Bot.setDisable(false);
            if (!spieler02Bot1.isDisable()) spieler02Bot1.setDisable(false);
        }

        // Spieler 3
        if (sp3 != null) {
            spieler03name.setText(sp3.getNickname());
            spieler03punkte.setText("Gewinnpunkte: " + sp3.getPunkte());
            spieler03Bereit.setDisable(true);
            spieler03Bereit.setSelected(sp3.isBereit());

            boolean istBot = sp3.getNickname().contains("Bot");
            if (!spieler03Bot.isDisable()) {
                spieler03Bot.setDisable(!istBot);
            }
            if (!spieler03Bot1.isDisable()) {
                spieler03Bot1.setDisable(!istBot);
            }

            // gegenseitige Deaktivierung bei Bot auf Platz 3
            if (istBot) {
                boolean isEasy = sp3.getNickname().contains("Einfacher");
                boolean isHard = sp3.getNickname().contains("Schwerer");
                if (isEasy) {
                    spieler03Bot.setSelected(true);
                    spieler03Bot1.setDisable(true);
                } else if (isHard) {
                    spieler03Bot1.setSelected(true);
                    spieler03Bot.setDisable(true);
                }
            }

        } else {
            spieler03name.setText("leer");
            spieler03punkte.setText("");
            spieler03Bereit.setDisable(true);
            spieler03Bereit.setSelected(false);

            boolean spieler2Besetzt = sp2 != null;
            if (spieler2Besetzt) {
                if (spieler03Bot.isDisable()) spieler03Bot.setDisable(false);
                if (spieler03Bot1.isDisable()) spieler03Bot1.setDisable(false);
            } else {
                spieler03Bot.setDisable(true);
                spieler03Bot1.setDisable(true);
            }
        }
    }

    /**
     * Aktualisiert den Raumstatus.
     * @param statustext der aktuelle Status
     */
    public void statusHandler(Spielstatus statustext) {
        raumStatus = statustext;
        if (statustext == Spielstatus.WARTE_AUF_DRUECKEN) {
            //Wenn eine neue Partie beginnt, muss der hatGedrückt-Status zurückgesetzt werden
            hatGedrueckt = false;
        }
        if (statustext == Spielstatus.WARTET_AUF_SPIELZUEGE) {
            //Wenn eine neue Runde beginnt, muss der hatGespielt-Status zurückgesetzt werden
            hatGespielt = false;
        }
        if (statustext == Spielstatus.PARTIE_BEENDET){
            Platform.runLater(() -> {spieler01rundenpunkte.setText("Rundenpunkte: 0");});
        }

        Platform.runLater(() -> {status.setText(raumStatus.toString());});
    }

    /**
     * Hilfsmethode um die entsprechenden Bilder zu den Karten zu finden
     * @param karte Karte zu der man das Bild sucht
     * @return das Bild der Karte
     */
    public Image findeKartenBild(Karte karte) {
        String kartenDatei = karte.getFarbe() + "_" + karte.getWert() + ".png";
        URL url = getClass().getClassLoader().getResource("images/" + kartenDatei);
        if (url == null) {
            System.err.println("Bild nicht gefunden: images/" + kartenDatei);
            return null;
        }
        return new Image(url.toExternalForm());
    }

    /**
     * Zeigt die Hand eines Spielers. Und die Karten der anderen verdeckt.
     * @param kartenListe die Liste der Karten des Spielers
     */
    public void zeigeHandKarten(List<Karte> kartenListe) {
        System.out.println("ZEIGEHANDKARTEN: " + kartenListe.size());
        //eigeneHand für handleKarteClick
        eigeneHand = new SpielerHand((ArrayList<Karte>) kartenListe, null);
        Platform.runLater(() -> {
            zeigeStartkarten(spieler01GedruecktBox, spieler01KartenBox, kartenListe);
            zeigeStartkartenVerdeckt(spieler02GedruecktBox, spieler02KartenBox);
            zeigeStartkartenVerdeckt(spieler03GedruecktBox, spieler03KartenBox);
        });
    }

    /**
     * Zeigt die Startkarten eines Spielers an (sichtbar für sich selbst).
     * @param gedruecktBox Box für gedrückte Karten
     * @param handkartenBox Box für Handkarten
     * @param handkarten Liste der Bilddateinamen der Karten
     */
    public void zeigeStartkarten(HBox gedruecktBox, HBox handkartenBox, List<Karte> handkarten) {
        gedruecktBox.getChildren().clear();
        handkartenBox.getChildren().clear();

        for (Karte karte : handkarten) {
            Image image = findeKartenBild(karte);
            ImageView iv = new ImageView(image);
            iv.setFitHeight(80);
            iv.setPreserveRatio(true);
            iv.setUserData(karte);
            iv.setOnMouseClicked(e -> handleKarteClick(karte));
            handkartenBox.getChildren().add(iv);
        }
    }

    /**
     * Zeigt die verdeckten Karten für die Gegner an.
     * @param gedruecktBox Box für gedrückte Karten
     * @param handkartenBox Box für verdeckte Handkarten
     */
    public void zeigeStartkartenVerdeckt(VBox gedruecktBox, VBox handkartenBox) {
        gedruecktBox.getChildren().clear();
        handkartenBox.getChildren().clear();

        URL rueckenUrl = getClass().getClassLoader().getResource("images/card_back.png");
        if (rueckenUrl == null) return;
        Image ruecken = new Image(rueckenUrl.toExternalForm());

        for (int i = 0; i < 11; i++) {
            ImageView iv = new ImageView(ruecken);
            iv.setFitHeight(80);
            iv.setPreserveRatio(true);
            handkartenBox.getChildren().add(iv);
        }
    }

    /**
     * Zeigt die gedrueckte Karte an.
     * @param gedruecktBox die Box die man ansprechen will
     * @param karte die Karte die gedrueckt wird
     */
    public void zeigeGedrueckteSelbst(HBox gedruecktBox, Karte karte) {
        Image image = findeKartenBild(karte);
        ImageView gedrueckt = new ImageView(image);
        gedrueckt.setFitHeight(80);
        gedrueckt.setPreserveRatio(true);
        gedruecktBox.getChildren().add(gedrueckt);
    }

    /**
     * Zeigt die gedrueckten Karten der anderen Spieler verdeckt.
     * @param gedruecktBox die Box die man ansprechen will.
     */
    public void zeigeGedruecktVerdeckt(VBox gedruecktBox) {
        URL rueckenUrl = getClass().getClassLoader().getResource("images/card_back.png");
        if (rueckenUrl == null) return;
        Image ruecken = new Image(rueckenUrl.toExternalForm());
        ImageView gedrueckt = new ImageView(ruecken);
        gedrueckt.setFitHeight(80);
        gedrueckt.setPreserveRatio(true);
        gedruecktBox.getChildren().add(gedrueckt);
    }

    /**
     * Setzt das Bild des aktuellen Trumpfes.
     * @param image Dateiname des Trumpf-Bildes
     */
    public void setzeTrumpfBild(Image image) {
        if (image != null) {
            trumpfImage.setImage(image);
        } else {
            System.err.println("Trumpfbild nicht gefunden");
        }
    }

    /**
     * Hilfsmethode zum Anzeigen des Trumpfs.
     * @param karte die Karte die angezeigt werden soll.
     */
    public void zeigeTrumpfKarte(Karte karte) {
        Image image = findeKartenBild(karte);
        setzeTrumpfBild(image);
    }

    public Spielstatus getRaumStatus() {
        return raumStatus;
    }

    /**
     * Setzt die gueltigen Karten, die es bekommt.
     * @param gueltige die Liste der gueltigen Karten
     */
    public void verarbeiteGueltige(List<Karte> gueltige){
        System.out.println("Gueltige Karten: " + gueltige.size());
        for(Karte karte1 : gueltige){
            System.out.println(karte1.getFarbe() + karte1.getWert());
        }
        setGueltige(gueltige);
    }

    /**
     * Behandelt das Klicken auf eine Karte (legt sie in den Stich).
     * @param karte Dateiname der Karte
     */
    public void handleKarteClick(Karte karte) {
        System.out.println("HANDLEKARTENCLICK:  Karte geklickt: " + karte.getWert() + karte.getFarbe());
        Iterator<Node> iterator = spieler01KartenBox.getChildren().iterator();
        while (iterator.hasNext()) {
            Node node = iterator.next();
            if (node instanceof ImageView iv && karte.equals(iv.getUserData())) {
                if (raumStatus.equals(Spielstatus.WARTE_AUF_DRUECKEN)) {
                    if (hatGedrueckt) {
                        System.out.println("Du hast bereits gedrückt");
                        return;
                    }
                    iterator.remove();
                    sendeDruecken(karte);
                    hatGedrueckt = true;
                    return;
                } else if (raumStatus.equals(Spielstatus.WARTET_AUF_SPIELZUEGE)) {
                    if (!SessionManager.getNickname().equals(istDran)) {
                        System.out.println("Du bist nicht dran!");
                        return;
                    }
                    if (karteInListe(karte, gueltige)) {
                        iterator.remove();
                        spielZugHandler(karte);
                        sendeSpielzug(karte);
                        return;
                    } else {
                        System.out.println("Diese Karte ist nicht Regelkonform!");
                    }
                }
                break;
            }
        }
    }

    /**
     * Vergleicht Karten mit den gueltigen und gibt true zurück falls diese in der Liste ist.
     * @param karte die geprüft werden soll.
     * @param gueltige Liste der gueltigen Karten.
     * @return
     */
    public boolean karteInListe(Karte karte, List<Karte> gueltige){
        for(Karte karte1 : gueltige){
            if(karte1.getFarbe().equals(karte.getFarbe()) && karte1.getWert().equals(karte.getWert())){
                return true;
            }
        }
        return false;
    }

    /**
     * Verarbeitet Rundeninfonachrichten und reagiert entsprechend.
     * Bei Stichstapel leeren wir Spielbereich und machen einen Delay.
     * Bei Rundenzahl wird Anzeige aktualisiert.
     * Bei Partiezahl wird Anzeige aktualisiert.
     * Bei aktuellerspieler wird Anzeige aktualisiert.
     * Bei Information wird ein PopUp angezeigt für 2 Sekunden.
     * Bei partieanzeige wird ein PopUp angezeigt für 5 Sekunden.
     * Bei Turnierende wird die Websocketverbindung getrennt und wir wechseln in die Ergebnissicht.
     * @param nachricht Nachricht die verarbeitet werden soll.
     */
    public void infoVerarbeiten(InfoNachricht nachricht){
        String typ = nachricht.getInfoTyp();
        System.out.println("INFOVERARBEITEN: " + typ);

        if ("StichStapel".equals(typ)) {
            Platform.runLater(() -> {
                PauseTransition delay = new PauseTransition(Duration.seconds(1));
                delay.setOnFinished(event -> spielbereich.getChildren().clear());
                delay.play();
            });
        }

        else if ("rundenzahl".equals(typ)) {
            Platform.runLater(() -> runde.setText("Runde " + nachricht.getInfo()));
        }

        else if ("partiezahl".equals(typ)) {
            Platform.runLater(() -> partie.setText("Partie " + nachricht.getInfo()));
        }

        else if ("aktuellerspieler".equals(typ)) {
            this.istDran = nachricht.getInfo();
            Platform.runLater(() -> spieleramzug.setText(nachricht.getInfo() + " ist dran!"));
        }

        else if ("Information".equalsIgnoreCase(typ)) {
            Stage stage = (Stage) spieler01name.getScene().getWindow();
            zeigeInfoPopup(stage, "Spielinformation", nachricht.getInfo(), 2);
        }

        else if ("partieanzeige".equals(typ)) {
            Stage stage = (Stage) spieler01name.getScene().getWindow();
            zeigeInfoPopup(stage, "Partiepunkte", nachricht.getInfo(), 5);
        }

        else if("tunierende".equals(typ)){
            Platform.runLater(() -> {
                if (stompClient != null && stompClient.isOpen()) {
                    stompClient.disconnect();
                }

                ErgebnisController controller = stageManager.switchSceneAndReturnController("/ergebnis.fxml");
                if (controller != null) {
                    controller.setRaumId(raumId);
                }
            });
        }
    }

    /**
     * Zeigt die Punkte der aktuellen Runde.
     * @param punkte Anzahl der Punkte
     */
    public void zeigeRundenPunkte(int punkte){
        Platform.runLater(() -> {spieler01rundenpunkte.setText("Rundenpunkte: " + punkte);});
    }

    /**
     * Zeigt die Gewinnpunkte an.
     * @param punkte Punkte und zu wem sie gehören.
     */
    public void zeigeGewinnPunkte(Map<String, Integer> punkte){

        for (Map.Entry<String, Integer> entry : punkte.entrySet()) {
            Integer punkt = entry.getValue();
            String nickname = entry.getKey();
            if (punkt != null) {
                Platform.runLater(() -> {
                    if(nickname.equals(SessionManager.getNickname())){
                        spieler01punkte.setText("Gewinnpunkte: " + punkt);
                    }
                    else if(nickname.equals(spieler02name.getText())){
                        spieler02punkte.setText("Gewinnpunkte: " + punkt);
                    }
                    else if(nickname.equals(spieler03name.getText())){
                        spieler03punkte.setText("Gewinnpunkte: " + punkt);
                    }
                });
            }
        }
    }

    /**
     * Zeigt gedrueckte Karte an.
     * @param karte die gedrueckt werden soll.
     */
    public void zeigeGedrueckteKarte(Karte karte) {
        Platform.runLater(() -> {
            zeigeGedrueckteSelbst(spieler01GedruecktBox, karte);
            zeigeGedruecktVerdeckt(spieler02GedruecktBox);
            zeigeGedruecktVerdeckt(spieler03GedruecktBox);
        });
    }

    /**
     * Sendet eine Nachricht an den Websocket wenn gedrueckt wurde.
     * @param karte Karte die gedrueckt wurde.
     */
    public void sendeDruecken(Karte karte) {
        if (stompClient != null && stompClient.isOpen()) {
            KartenGedruecktNachricht gedruecktNachricht = new KartenGedruecktNachricht(raumId, SessionManager.getNickname(), karte);

            stompClient.sendMessage(gedruecktNachricht);
            System.out.println("SENDEDRUECKEN:  Gedrueckt gesendet: " + karte.getWert() + karte.getFarbe());
        } else {
            System.out.println("WebSocket nicht verbunden!");
        }
    }

    /**
     * Sendet eine Chatnachricht per Websocket.
     */
    @FXML
    public void handleChatSenden() {
        String msg = chatTextField.getText().trim();
        if (!msg.isEmpty() && stompClient != null && stompClient.isOpen()) {
            SpielraumChatNachricht nachricht = new SpielraumChatNachricht(raumId, SessionManager.getNickname(), msg);
            stompClient.sendMessage(nachricht);
            chatTextField.clear();
        }
    }

    /**
     * Empfängt eine Chatnachricht und fügt sie der Chat-ListView hinzu.
     * @param nachricht Chatnachricht
     */
    public void handleChatMessage(SpielraumChatNachricht nachricht) {
        Platform.runLater(() -> {
            String messageText = String.format("%s: %s",
                    nachricht.getSender(),
                    nachricht.getInhalt());
            chatListView.getItems().add(messageText);
        });
    }

    /**
     * Zeigt die gespielte Karte
     * @param karte gespielte Karte
     */
    public void zeigeGespielteKarte(Karte karte) {
        Platform.runLater(() -> {
            spielZugHandler(karte);
        });
    }

    /**
     * Verarbeitet den Spielzug wenn eine Karte gelegt wurde
     * @param karte die gespielte Karte
     */
    public void spielZugHandler(Karte karte) {
        System.out.println("SPIELZUGHANDLER: Karte wird auf das Stichfeld gelegt: " + karte.getWert() + karte.getFarbe());

        Image image = findeKartenBild(karte);

        ImageView kopie = new ImageView(image);
        kopie.setFitHeight(100);
        kopie.setPreserveRatio(true);

        double offsetX = (spielbereich.getWidth() / 2.0) - 50 + (kartenImStich * 30);
        double offsetY = (spielbereich.getHeight() / 2.0) - 70;

        kopie.setLayoutX(offsetX);
        kopie.setLayoutY(offsetY);

        spielbereich.getChildren().add(kopie);

        kartenImStich = (kartenImStich + 1) % 3;

    }

    /**
     * Sendet den Spielzug an den Websocket
     * @param karte gespielte Karte
     */
    public void sendeSpielzug(Karte karte) {
        if (stompClient != null && stompClient.isOpen()) {
            SpielzugNachricht spielzugNachricht = new SpielzugNachricht(raumId, SessionManager.getNickname(), karte);

            stompClient.sendMessage(spielzugNachricht);
            System.out.println("SENDESPIELZUG:  Spielzug gesendet: " + karte.getWert() + karte.getFarbe());
        } else {
            System.out.println("WebSocket nicht verbunden!");
        }
    }

    /**
     * Verlässt den Spielraum und wechselt zur Lobby zurück.
     */
    @FXML
    public void handleVerlassen() {
        try {
            String nickname = SessionManager.getNickname();
            String url = "http://localhost:8080/spielraum/verlassen?id=" + raumId + "&nickname=" + nickname;
            restTemplate.delete(url);
        } catch (Exception e) {
            System.out.println("Fehler beim Verlassen des Spielraums:");
            e.printStackTrace();
        }
        if (stompClient != null) {
            stompClient.disconnect();
            stompClient = null;
        }
        stageManager.switchScene("/lobby.fxml");
    }

    /**
     * Öffnet das Fenster für die Spielregeln.
     */
    @FXML
    public void handleSpielregeln() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/regeln.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Spielregeln");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Öffnet das Fenster für die Bestenliste.
     */
    @FXML
    public void handleBestenliste() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/bestenliste.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Bestenliste");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Wird ausgefuehrt wenn das Spiel gestartet wird um die richtigen UI Elemente ein- und auszublenden.
     * @param spielraumId die Raum-Id
     */
    public void starteSpiel(Long spielraumId) {

        Platform.runLater(() -> {
            spielStartenBtn.setDisable(true);
            spieler01Bereit.setVisible(false);
            spieler02Bereit.setVisible(false);
            spieler03Bereit.setVisible(false);

            spieler02Bot.setVisible(false);
            spieler02Bot1.setVisible(false);
            spieler03Bot.setVisible(false);
            spieler03Bot1.setVisible(false);


            trumpf.setVisible(true);
            trumpfImage.setVisible(true);
            spielbereich.setVisible(true);
            spieler01punkte.setVisible(true);
            Platform.runLater(() -> {spieler01punkte.setText("Gewinnpunkte: 0");});
            spieler02punkte.setVisible(true);
            Platform.runLater(() -> {spieler02punkte.setText("Gewinnpunkte: 0");});
            spieler03punkte.setVisible(true);
            Platform.runLater(() -> {spieler03punkte.setText("Gewinnpunkte: 0");});
            spieler01GedruecktBox.setVisible(true);
            spieler01KartenBox.setVisible(true);
            spieler02GedruecktBox.setVisible(true);
            spieler02KartenBox.setVisible(true);
            spieler03GedruecktBox.setVisible(true);
            spieler03KartenBox.setVisible(true);

            spieler01rundenpunkte.setVisible(true);
            Platform.runLater(() -> {spieler01rundenpunkte.setText("Rundenpunkte: 0");});
            gedruecktlabel01.setVisible(true);
            gedruecktlabel02.setVisible(true);
            gedruecktlabel03.setVisible(true);
            amzuglabel.setVisible(true);
            spieleramzug.setVisible(true);
            partie.setVisible(true);
            Platform.runLater(() -> {partie.setText("Partie 0");});
            runde.setVisible(true);
            Platform.runLater(() -> {runde.setText("Runde 0");});
        });
    }

    /**
     * Startet das Spiel, wenn alle Spieler bereit sind, oder genug Bots hinzugefügt wurden.
     */
    @FXML
    public void handleSpielStarten() {
        boolean spieler1Bereit = spieler01Bereit.isSelected();
        boolean spieler2BereitOderBot = spieler02Bereit.isSelected() || spieler02Bot.isSelected() || spieler02Bot1.isSelected();
        boolean spieler3BereitOderBot = spieler03Bereit.isSelected() || spieler03Bot.isSelected() || spieler03Bot1.isSelected();

        if (spieler1Bereit && spieler2BereitOderBot && spieler3BereitOderBot) {
            if (stompClient != null && stompClient.isOpen()) {
                SpielraumStartNachricht nachricht = new SpielraumStartNachricht(raumId, SessionManager.getNickname());
                stompClient.sendMessage(nachricht);
                starteSpiel(raumId);
            }
        }
    }


    /**
     * Setzt den eigenen "Bereit"-Status auf dem Server.
     */
    @FXML
    public void handleBereit1() {
        boolean istBereit = spieler01Bereit.isSelected();
        String nickname = SessionManager.getNickname();

        try {
            String url = String.format("http://localhost:8080/spielraum/bereit?raumId=" + raumId + "&nickname=" + nickname + "&bereit=" + istBereit,
                    raumId, nickname, istBereit);
            restTemplate.postForObject(url, null, Void.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Platzhalter für Spieler 2 "Bereit" Event.
     */
    @FXML
    public void handleBereit2() {
    }

    /**
     * Platzhalter für Spieler 3 "Bereit" Event.
     */
    @FXML
    public void handleBereit3() {
    }

    public List<Karte> getGueltige(){
        return gueltige;
    }

    public void setGueltige(List<Karte> neueGueltige){
        this.gueltige = neueGueltige;
    }

    /**
     * Wird ausgeführt wenn man die entsprechende Checkbox für einen Bot auswählt und lässt diesen einfuegen.
     */
    @FXML
    public void handleSpieler02BotEasy() {
        boolean aktiviert = spieler02Bot.isSelected();
        spieler02Bot1.setDisable(aktiviert); // Gegenseitige Deaktivierung
        if (!aktiviert) {
            spieler02Bot1.setDisable(false); // Reaktivieren, falls Easy-Bot abgewählt
        }

        int zielPlatz = (meinPlatz + 1) % 3;
        toggleBot(aktiviert, "easy", "EinfacherBot1", zielPlatz);
    }

    /**
     * Wird ausgeführt wenn man die entsprechende Checkbox für einen Bot auswählt und lässt diesen einfuegen.
     */
    @FXML
    public void handleSpieler02BotHard() {
        boolean aktiviert = spieler02Bot1.isSelected();
        spieler02Bot.setDisable(aktiviert);
        if (!aktiviert) {
            spieler02Bot.setDisable(false);
        }

        int zielPlatz = (meinPlatz + 1) % 3;
        toggleBot(aktiviert, "hard", "SchwererBot1", zielPlatz);
    }

    /**
     * Wird ausgeführt wenn man die entsprechende Checkbox für einen Bot auswählt und lässt diesen einfuegen.
     */
    @FXML
    public void handleSpieler03BotEasy() {
        boolean aktiviert = spieler03Bot.isSelected();
        spieler03Bot1.setDisable(aktiviert);
        if (!aktiviert) {
            spieler03Bot1.setDisable(false);
        }

        int zielPlatz = (meinPlatz + 2) % 3;
        toggleBot(aktiviert, "easy", "EinfacherBot2", zielPlatz);
    }

    /**
     * Wird ausgeführt wenn man die entsprechende Checkbox für einen Bot auswählt und lässt diesen einfuegen.
     */
    @FXML
    public void handleSpieler03BotHard() {
        boolean aktiviert = spieler03Bot1.isSelected();
        spieler03Bot.setDisable(aktiviert);
        if (!aktiviert) {
            spieler03Bot.setDisable(false);
        }

        int zielPlatz = (meinPlatz + 2) % 3;
        toggleBot(aktiviert, "hard", "SchwererBot2", zielPlatz);
    }

    /**
     * Aktiviert einen Bot entsprechend der Checkbox die man gewaehlt hat.
     * @param aktiviert Status der Checkbox
     * @param typ typ des Bots
     * @param nickname Name des Bots
     * @param platz Platz auf dem der Bot hinzugefügt wird.
     */
    public void toggleBot(boolean aktiviert, String typ, String nickname, int platz) {
        try {
            String deleteurl = "http://localhost:8080/spielraum/verlassen" + "?id=" + raumId;
            String url = "";

            if (aktiviert) {
                Spieler bot;
                if ("easy".equalsIgnoreCase(typ)) {
                    bot = new EinfacherBot(nickname);
                    url = "http://localhost:8080/spielraum/beitretenboteinfach" + "?id=" + raumId;
                } else if ("hard".equalsIgnoreCase(typ)) {
                    bot = new SchwererBot(nickname);
                    url = "http://localhost:8080/spielraum/beitretenbotschwer" + "?id=" + raumId;
                } else {
                    System.err.println("Unbekannter Bot-Typ: " + typ);
                    return;
                }
                url += "&platz=" + platz;
                restTemplate.postForObject(url, bot, Boolean.class);
            } else {
                deleteurl += "&nickname=" + nickname;
                restTemplate.delete(deleteurl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Zeigt ein InfoPopUp an für Spielinfos.
     * @param window View in der es angezeigt wird
     * @param titel Titel des PopUps
     * @param nachricht Nachricht des PopUps
     * @param sekunden Dauer bis das PopUp schliesst
     */
    public void zeigeInfoPopup(Window window, String titel, String nachricht, int sekunden) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(titel);
            alert.setHeaderText(null);
            alert.setContentText(nachricht);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.initOwner(window);
            alert.show();

            PauseTransition delay = new PauseTransition(Duration.seconds(sekunden));
            delay.setOnFinished(event -> alert.close());
            delay.play();
        });
    }

    //Nur für Tests
    public void setRaumStatus(Spielstatus spielstatus) {
        this.raumStatus = spielstatus;
    }
}