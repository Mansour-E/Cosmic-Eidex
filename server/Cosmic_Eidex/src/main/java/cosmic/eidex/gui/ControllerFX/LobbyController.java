package cosmic.eidex.gui.ControllerFX;

import java.io.IOException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import cosmic.eidex.DTO.SpielraumDTO;
import cosmic.eidex.Lobby.Spielraum;
import cosmic.eidex.Service.SessionManager;
import cosmic.eidex.Service.StompClient;
import cosmic.eidex.Lobby.Nachricht;
import cosmic.eidex.gui.StageManager;
import cosmic.eidex.repository.SpielerRepository;
import cosmic.eidex.spielmodell.Spieler;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;



import java.net.URI;
import java.util.stream.Collectors;

import static cosmic.eidex.gui.util.AlertUtil.showInfo;

/**
 * GUI-Controller für die Lobby Ansicht.
 */
@Component
public class LobbyController {

    @FXML
    private ListView<String> raumListe;

    @FXML
    private ListView<String> spielerListe;

    @FXML
    private ListView<String> bestenliste;

    @FXML
    private ListView<Nachricht> chatListView;

    @FXML
    private TextField chatInput;

    @FXML
    private Label loggedinplayer;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

    protected final StageManager stageManager;

    protected RestTemplate restTemplate = new RestTemplate();

    protected StompClient stompClient;

    /**
     * Konstruktor für den LobbyController.
     *
     * @param stageManager Um JavaFX Szenen zu steuern
     */
    @Autowired
    public LobbyController(StageManager stageManager) {
        this.stageManager = stageManager;
    }

    /**
     * Initialisiert die Lobby. Lädt Chat, Spielräume, Bestenliste und verbindet den WebSocket.
     * Setzt außerdem die Listener für die Raum-Auswahl.
     */
    @FXML
    public void initialize() {
        loggedinplayer.setText("Angemeldet: " + SessionManager.getNickname());


        chatListView.setCellFactory(lv -> new ListCell<Nachricht>() {
            @Override
            protected void updateItem(Nachricht item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label msgLabel = new Label(item.getSender() + ": " + item.getInhalt());
                    Label timeLabel = new Label(item.getZeitstempel().atZoneSameInstant(ZoneId.of("Europe/Berlin")).format(formatter));
                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    HBox hbox = new HBox(msgLabel, spacer, timeLabel);
                    setGraphic(hbox);
                }
            }
        });

        ladeChatVerlauf();
        ladeRaumListe();
        connectToWebSocket();
        Platform.runLater(() -> stompClient.requestBestenliste());
        Platform.runLater(() -> stompClient.requestSpielraeume());

        raumListe.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                zeigeSpielerFuerRaum(newVal);
            }
        });

    }

    /**
     * Zeigt die Spieler des ausgewählten Spielraums in der Spieler-Liste an.
     * @param raumName Name des ausgewählten Spielraums
     */
    private void zeigeSpielerFuerRaum(String raumName) {
        List<String> spielerNamen = getSpielerImRaum(raumName);

        Platform.runLater(() -> {
            if (spielerNamen.isEmpty()) {
                spielerListe.getItems().clear();
            } else {
                spielerListe.getItems().setAll(spielerNamen);
            }

        });
    }

    /**
     * Ruft alle Spieler für den angegebenen Raum vom Server ab.
     * @param raumName Name des Spielraums
     * @return Liste der Spielernamen im Raum
     */
    private List<String> getSpielerImRaum(String raumName) {

        String url = "http://localhost:8080/spielraum/alle";
        SpielraumDTO[] spielraeume = restTemplate.getForObject(url, SpielraumDTO[].class);
        if(spielraeume != null) {
            for (SpielraumDTO s : spielraeume) {
                if (s.getName().equals(raumName)) {
                    return s.getSpieler().stream().map(
                            Spieler::getNickname)
                            .collect(Collectors.toList());
                }
            }
            return Collections.emptyList();
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * Verbindet sich mit dem WebSocket für Chat, Bestenliste und Spielraum-Updates.
     */
    private void connectToWebSocket() {
        try {
            URI uri = new URI("ws://localhost:8080/chat");
            stompClient = new StompClient(
                    uri,
                    message -> Platform.runLater(() -> {
                        chatListView.getItems().add(message);
                        chatListView.scrollTo(chatListView.getItems().size());
                    }),
                    top10Liste -> Platform.runLater(() -> {
                        List<String> eintraege = new ArrayList<>();
                        for (int i = 0; i < top10Liste.size(); i++) {
                            Spieler s = top10Liste.get(i);
                            eintraege.add((i + 1) + ". " + s.getNickname() + " – Siege: " + s.getSiege());
                        }
                        bestenliste.getItems().setAll(eintraege);
                    }),
                    spielraumNamen -> Platform.runLater(() -> {
                        raumListe.getItems().setAll(spielraumNamen);
                        String ausgewaehlterRaum = raumListe.getSelectionModel().getSelectedItem();
                        if (ausgewaehlterRaum == null || !spielraumNamen.contains(ausgewaehlterRaum)) {
                            spielerListe.getItems().clear();
                        }

                    })
            );
            stompClient.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sendet eine Chat-Nachricht an den Server, wenn der Benutzer auf "Senden" klickt.
     */
    @FXML
    private void handleSendeNachricht() {
        String msg = chatInput.getText().trim();
        if (!msg.isEmpty() && stompClient != null && stompClient.isOpen()) {
            Nachricht nachricht = new Nachricht(SessionManager.getNickname(), msg);
            stompClient.sendMessage(nachricht);
            chatInput.clear();
        }
    }

    /**
     * Lädt den bisherigen Chatverlauf vom Server und zeigt ihn im Chat-ListView an.
     */
    private void ladeChatVerlauf() {
        try {
            String url = "http://localhost:8080/chat/history";
            Nachricht[] verlauf = restTemplate.getForObject(url, Nachricht[].class);

            if (verlauf != null) {
                Platform.runLater(() -> {

                    chatListView.getItems().clear();
                    chatListView.getItems().addAll(verlauf);


                    if (!chatListView.getItems().isEmpty()) {
                        chatListView.scrollTo(chatListView.getItems().size());
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Lädt die Liste der vorhandenen Spielräume vom Server.
     */
    private void ladeRaumListe() {
        try {
            String url = "http://localhost:8080/spielraum/spielraumnamen";
            String[] namen = restTemplate.getForObject(url, String[].class);

            if (namen != null && namen.length > 0) {
                raumListe.getItems().setAll(namen);
            }
            else {
                raumListe.getItems().clear();
                spielerListe.getItems().clear();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Versucht, den aktuell ausgewählten Raum zu betreten.
     * Bei Erfolg wird in die Spielraum-Szene gewechselt.
     */
    @FXML
    private void handleBeitreten() {
        String ausgewaehlterRaum = raumListe.getSelectionModel().getSelectedItem();
        if (ausgewaehlterRaum != null) {
            Long raumId = getRaumIdByName(ausgewaehlterRaum);
            System.out.println("RAUM ID -----  " + raumId);
            if (raumId == null) {
                System.out.println("Raum-ID nicht gefunden.");
                return;
            }

            String url = "http://localhost:8080/spielraum/beitreten?id=" + raumId;
            String spToGet = "http://localhost:8080/spielraum/spieler?name=" + SessionManager.getNickname();
            Spieler spieler = restTemplate.postForObject(spToGet, null, Spieler.class);

            try {
                Boolean beigetreten = restTemplate.postForObject(url, spieler, Boolean.class);
                if (Boolean.TRUE.equals(beigetreten)) {
                    System.out.println("Erfolgreich beigetreten!");
                    if (stompClient != null && stompClient.isOpen()) {
                        stompClient.disconnect();
                    }

                    SpielraumController controller = stageManager.switchSceneAndReturnController("/spielraum.fxml");
                    if (controller != null) {
                        controller.setRaumId(raumId);
                    }
                } else {
                    System.out.println("Beitritt fehlgeschlagen.");
                    showInfo("Dieser Spielraum ist bereits voll.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Gibt die ID eines Raums anhand des Namens zurück.
     * @param name Name des Raums
     * @return Raum-ID oder null, falls nicht gefunden
     */
    private Long getRaumIdByName(String name) {
        String url = "http://localhost:8080/spielraum/alle";
        SpielraumDTO[] raeume = restTemplate.getForObject(url, SpielraumDTO[].class);
        if (raeume != null) {
            for (SpielraumDTO dto : raeume) {
                if (dto.getName().equalsIgnoreCase(name)) {
                    return dto.getId();
                }
            }
        }
        return null;
    }

    /**
     * Wechselt zur Szene zum Erstellen eines neuen Spiels.
     */
    @FXML
    private void handleSpielErstellen() {
        System.out.println("Spiel erstellt");
        if (stompClient != null && stompClient.isOpen()) {
            stompClient.disconnect();
        }
        stageManager.switchScene("erstellen.fxml");
    }

    /**
     * Öffnet ein neues Fenster mit den Spielregeln.
     */
    @FXML
    private void handleSpielregeln() {
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
     * Wechselt zur Szene für das Bearbeiten des Profils.
     */
    @FXML
    private void handleProfilBearbeiten() {

        if (stompClient != null && stompClient.isOpen()) {
            stompClient.disconnect();
        }
        stageManager.switchScene("/bearbeiten.fxml");
    }

    /**
     * Meldet den aktiven Nutzer ab, wenn man auf Abmelden klickt.
     * Wechselt danach auf den Loginsicht.
     */
    @FXML
    private void handleAbmelden() {
        String nickname = SessionManager.getNickname();
        String token = SessionManager.getToken(nickname);
        if (token == null || token.isEmpty()) {
            showInfo("Keine aktive Sitzung.");
            stageManager.switchScene("/login.fxml");
            return;
        }

        try {
            String url = "http://localhost:8080/login/logout";
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            HttpEntity<Void> request = new HttpEntity<>(headers);

            restTemplate.exchange(url, HttpMethod.POST, request, String.class);
            System.out.println("Logout erfolgreich für " + nickname);
        } catch (Exception e) {
            System.out.println("Fehler beim Logout: " + e.getMessage());
        } finally {
            if (stompClient != null && stompClient.isOpen()) {
                stompClient.disconnect();
            }
            SessionManager.removeToken(nickname);
            stageManager.switchScene("/login.fxml");
        }
    }

}
