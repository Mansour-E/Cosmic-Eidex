package cosmic.eidex.guiLogik;

import cosmic.eidex.Bots.EinfacherBot;
import cosmic.eidex.Bots.SchwererBot;
import cosmic.eidex.Service.SpielraumStompClient;
import cosmic.eidex.gui.ControllerFX.SpielraumController;
import cosmic.eidex.spielmodell.Karte;
import cosmic.eidex.spielmodell.Spieler;
import javafx.scene.image.Image;

import java.net.URL;

public class SpielraumControllerLogik extends SpielraumController {

    @Override
    public Image findeKartenBild(Karte karte) {
        String kartenDatei = karte.getFarbe() + "_" + karte.getWert() + ".png";
        URL url = getClass().getClassLoader().getResource("images/" + kartenDatei);
        if (url == null) {
            System.err.println("Bild nicht gefunden: images/" + kartenDatei);
            return null;
        }
        return new Image(url.toExternalForm());
    }

    @Override
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

    @Override
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
}
