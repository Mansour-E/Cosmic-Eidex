package cosmic.eidex.spielmodell;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SpielerHandTest {

    private SpielerHand spielerHand;
    private Karte karte1;
    private Karte karte2;
    private Karte karte3;
    private Karte karteNichtInHand;
    private Spieler spieler;
    private Spieler spieler2;
    private Regel regel1;
    private Regel regel2;

    @BeforeEach
    public void setup() {
        spielerHand = new SpielerHand();
        karte1 = new Karte("Herz", "9");
        karte2 = new Karte("Eidex", "8");
        karte3 = new Karte("Rabe", "Bube");
        karteNichtInHand = new Karte("Rabe", "10");
        spielerHand.addToHand(List.of(karte1, karte2));
        spieler = new Spieler();
        spieler2 = new Spieler();
        regel1 = new Regel("Rabe","Normal");
        regel2 = new Regel("","Undenufe");
    }

    @Test
    void testSetSpieler() {
        spielerHand.setSpieler(spieler);
        assertEquals(spieler, spielerHand.getSpieler());
        spielerHand.setSpieler(spieler2);
        assertEquals(spieler2, spielerHand.getSpieler());
    }

    @Test
    void testAddToHand() {
        assertEquals(2, spielerHand.getHand().size());
        assertTrue(spielerHand.getHand().contains(karte1));
        assertTrue(spielerHand.getHand().contains(karte2));
    }

    @Test
    void testAddToHandButHandNull() {
        SpielerHand leer = new SpielerHand(null,spieler);
        leer.addToHand(List.of(karteNichtInHand));
        assertNotNull(leer.getHand());

    }

    @Test
    void testLeereHandAdd() {
        SpielerHand neueHand = new SpielerHand();
        neueHand.addToHand(List.of(karte1));
        assertEquals(1, neueHand.getHand().size());
    }

    @Test
    void testGetKarte() {
        assertEquals(karte1, spielerHand.getKarte(0));
        assertEquals(karte2, spielerHand.getKarte(1));
    }

    @Test
    void testKarteAusspielenErfolgreich() {
        Karte gespielt = spielerHand.karteAusspielen(karte1);
        assertEquals(karte1, gespielt);
        assertFalse(spielerHand.getHand().contains(karte1));
        assertEquals(1, spielerHand.getHand().size());
    }

    @Test
    void testKarteAusspielenFehlgeschlagen() {
        Karte gespielt = spielerHand.karteAusspielen(karteNichtInHand);
        assertNull(gespielt);
        assertEquals(2, spielerHand.getHand().size());
    }

    @Test
    void testKarteDruecken_Valid() {
        spielerHand.addToHand(List.of(karte3));
        spielerHand.karteDruecken(karte3);
        assertEquals(karte3.getFarbe(), spielerHand.getGedrueckteKarte().getFarbe());
        assertEquals(karte3.getWert(), spielerHand.getGedrueckteKarte().getWert());
        assertFalse(spielerHand.getHand().contains(karte3));
    }

    @Test
    void testKarteDruecken_Invalid() {
        spielerHand.karteDruecken(karteNichtInHand);
        assertNull(spielerHand.getGedrueckteKarte());
        assertEquals(2, spielerHand.getHand().size());
    }

    @Test
    void testGetAlleKartenWertNull() {
        Regel regel = new Regel("Herz", "Normal");
        Karte nullKarte = new Karte("Eidex", "7");
        spielerHand.addToHand(List.of(nullKarte));

        List<Karte> nullWertKarten = spielerHand.getAlleKartenWertNull(regel);
        assertTrue(nullWertKarten.contains(nullKarte));
        assertFalse(nullWertKarten.contains(karte1));
    }

    @Test
    void testGetKartenPunkteHand() {
        Regel regel = new Regel("Herz", "Normal");
        int punkte = spielerHand.getKartenPunkteHand(regel);
        assertEquals(14, punkte);
    }

    @Test
    void testSetUndGetSpieler() {
        Spieler spieler = new Spieler("Max", "X1");
        spielerHand.setSpieler(spieler);
        assertEquals(spieler, spielerHand.getSpieler());
    }

    @Test
    void testAddToHandWennListeNull() {
        SpielerHand handMitNull = new SpielerHand(null, null, null); // Erzeugt Hand mit null
        Karte karte = new Karte("Rabe", "6");
        handMitNull.addToHand(List.of(karte));
        assertEquals(1, handMitNull.getHand().size());
        assertTrue(handMitNull.getHand().contains(karte));
    }





}
