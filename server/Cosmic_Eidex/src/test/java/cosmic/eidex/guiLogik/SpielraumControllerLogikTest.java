package cosmic.eidex.guiLogik;

import cosmic.eidex.Service.SessionManager;
import cosmic.eidex.Service.SpielNachrichten.KartenGedruecktNachricht;
import cosmic.eidex.Service.SpielNachrichten.SpielzugNachricht;
import cosmic.eidex.Service.SpielraumStompClient;
import cosmic.eidex.Service.Spielstatus;
import cosmic.eidex.spielmodell.Karte;
import cosmic.eidex.spielmodell.Spieler;
import javafx.application.Platform;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SpielraumControllerLogikTest {

    private SpielraumControllerLogik controller;

    @BeforeAll
    static void initToolkit() {
        Platform.startup(() -> {});
    }

    @BeforeEach
    void setUp() {
        controller = new SpielraumControllerLogik();
        controller.spieler01name = new Label();
        controller.spieler01punkte = new Label();
        controller.spieler01Bereit = new CheckBox();

        controller.spieler02name = new Label();
        controller.spieler02punkte = new Label();
        controller.spieler02Bereit = new CheckBox();
        controller.spieler02Bot = new CheckBox();
        controller.spieler02Bot1 = new CheckBox();

        controller.spieler03name = new Label();
        controller.spieler03punkte = new Label();
        controller.spieler03Bereit = new CheckBox();
        controller.spieler03Bot = new CheckBox();
        controller.spieler03Bot1 = new CheckBox();
    }

    @Test
    void findeKartenBildRessourceVorhanden() {
        String bildDatei = "images/Herz_Ass.png";
        URL url = getClass().getClassLoader().getResource(bildDatei);
        assertNotNull(url, "Bilddatei sollte im Classpath vorhanden sein.");
    }

    @Test
    void setRaumIdSpeichertId() {
        var controller = new SpielraumControllerLogik();
        controller.setRaumId(42L);
        // Zugriff über Reflection:
        Long gespeicherteId = (Long) ReflectionTestUtils.getField(controller, "raumId");
        assertEquals(42L, gespeicherteId);
    }

    @Test
    void getRaumStatusNachSetzen() {
        var controller = new SpielraumControllerLogik();
        controller.setRaumStatus(Spielstatus.WARTET_AUF_SPIELSTART);
        assertEquals(Spielstatus.WARTET_AUF_SPIELSTART, controller.getRaumStatus());
    }


    @Test
    void karteInListe() {
        var controller = new SpielraumControllerLogik();
        var k1 = new Karte("Eichel", "8");
        controller.setGueltige(List.of(k1));

        assertTrue(controller.karteInListe(new Karte("Eichel", "8"), controller.getGueltige()));
        assertFalse(controller.karteInListe(new Karte("Rosen", "9"), controller.getGueltige()));
    }

    @Test
    void verarbeiteGueltigeSetztListe() {
        var controller = new SpielraumControllerLogik();
        var karte = new Karte("Eichel", "7");
        var liste = List.of(karte);

        controller.verarbeiteGueltige(liste);

        assertEquals(liste, controller.getGueltige());
    }


    @Test
    void sendeDruecken() {
        var controller = new SpielraumControllerLogik();
        var karte = new Karte("Herz", "Ass");

        var mockStompClient = Mockito.mock(SpielraumStompClient.class);
        ReflectionTestUtils.setField(controller, "stompClient", mockStompClient);
        ReflectionTestUtils.setField(controller, "raumId", 123L);
        SessionManager.saveToken("Test", "Test");

        Mockito.when(mockStompClient.isOpen()).thenReturn(true);

        controller.sendeDruecken(karte);

        Mockito.verify(mockStompClient).sendMessage(Mockito.argThat(
                msg -> msg instanceof KartenGedruecktNachricht &&
                        ((KartenGedruecktNachricht) msg).getGedrueckteKarte().equals(karte)
        ));

    }

    @Test
    void sendeSpielzug() {var controller = new SpielraumControllerLogik();
        var karte = new Karte("Eidex", "9");

        var mockStompClient = Mockito.mock(SpielraumStompClient.class);
        ReflectionTestUtils.setField(controller, "stompClient", mockStompClient);
        ReflectionTestUtils.setField(controller, "raumId", 456L);
        SessionManager.saveToken("Test", "Test");

        Mockito.when(mockStompClient.isOpen()).thenReturn(true);

        controller.sendeSpielzug(karte);

        Mockito.verify(mockStompClient).sendMessage(Mockito.argThat(
                msg -> msg instanceof SpielzugNachricht &&
                        ((SpielzugNachricht) msg).getGespielteKarte().equals(karte)
        ));

    }

    @Test
    void getUndSetGueltige() {
        var controller = new SpielraumControllerLogik();
        var k1 = new Karte("Eichel", "8");
        controller.setGueltige(List.of(k1));
        assertEquals(List.of(k1), controller.getGueltige());
    }

    @Test
    void toggleBotEasyMitMock() {
        var controller = new SpielraumControllerLogik();
        var mockTemplate = Mockito.mock(RestTemplate.class);
        ReflectionTestUtils.setField(controller, "restTemplate", mockTemplate);
        controller.setRaumId(1L);

        controller.toggleBot(true, "easy", "MockBot", 1);

        Mockito.verify(mockTemplate, Mockito.times(1))
                .postForObject(Mockito.contains("beitretenboteinfach"), Mockito.any(), Mockito.eq(Boolean.class));
    }

    @Test
    void toggleBotHardMitMock() {
        var controller = new SpielraumControllerLogik();
        var mockTemplate = Mockito.mock(RestTemplate.class);
        ReflectionTestUtils.setField(controller, "restTemplate", mockTemplate);
        controller.setRaumId(1L);

        controller.toggleBot(true, "hard", "MockBot", 1);

        Mockito.verify(mockTemplate, Mockito.times(1))
                .postForObject(Mockito.contains("beitretenbotschwer"), Mockito.any(), Mockito.eq(Boolean.class));
    }

    //TODO: noch nicht fertig
    @Test
    void sortiertUndSetztLabelsFuer3Spieler() {
        Spieler s1 = new Spieler("Alice", "1");
        Spieler s2 = new Spieler("EinfacherBot1","1");
        Spieler s3 = new Spieler("Charlie","1");

        s1.setPunkte(10); s1.setBereit(true);
        s2.setPunkte(20); s2.setBereit(true);
        s3.setPunkte(30); s3.setBereit(false);

        Map<Integer, String> platzMap = Map.of(0, "Alice", 1, "EinfacherBot1", 2, "Charlie");
        List<Spieler> spieler = List.of(s1, s2, s3);

        ReflectionTestUtils.setField(controller, "meinPlatz", 0);
        SessionManager.saveToken("Alice", "dummyToken");
        controller.sortiereSpielerUndSetzeLabels(spieler, platzMap);

        assertEquals("Alice", controller.spieler01name.getText());
        assertEquals("Gewinnpunkte: 10", controller.spieler01punkte.getText());
        assertTrue(controller.spieler01Bereit.isSelected());

        assertEquals("EinfacherBot1", controller.spieler02name.getText());
        assertEquals("Gewinnpunkte: 20", controller.spieler02punkte.getText());
        assertTrue(controller.spieler02Bereit.isSelected());
        assertTrue(controller.spieler02Bot.isSelected());
        assertTrue(controller.spieler02Bot1.isDisable());

        assertEquals("Charlie", controller.spieler03name.getText());
        assertEquals("Gewinnpunkte: 30", controller.spieler03punkte.getText());
        assertFalse(controller.spieler03Bereit.isSelected());
        SessionManager.removeToken("Alice");
    }

    @Test
    void behandeltLeerenPlatzMapFallRobust() {
        controller.sortiereSpielerUndSetzeLabels(List.of(), new HashMap<>());
        assertNotNull(controller); // kein Crash
    }

    @Test
    void behandeltNullPlatzMap() {
        controller.sortiereSpielerUndSetzeLabels(List.of(), null);
        assertNotNull(controller); // kein Crash
    }

}