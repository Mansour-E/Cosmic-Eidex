package cosmic.eidex.spielmodell;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TurnierTest {

    Turnier turnier;
    Spieler Bob;
    Spieler Alice;
    Spieler Charlie;
    List<Spieler> spielerliste;

    //Testspieler
    private Spieler createMockSpieler(String name) {
        Spieler spieler = mock(Spieler.class, withSettings().useConstructor(name, "psswd"));
        when(spieler.getNickname()).thenReturn(name);
        return spieler;
    }
    private Partie createMockPartie() {
        return mock(Partie.class);
    }

    @BeforeEach
    public void setUp() {
        Bob = createMockSpieler("Bob");
        Alice = createMockSpieler("Alice");
        Charlie = createMockSpieler("Charlie");
        spielerliste = Arrays.asList(Bob, Alice, Charlie);
        turnier = new Turnier(spielerliste);

    }

    @Test
    public void testSetUndGetSpieler() {
        Spieler Max = createMockSpieler("Max");
        List<Spieler> spielerliste2 = List.of(Max);
        turnier.setSpieler(spielerliste2);
        assertEquals(spielerliste2, turnier.getSpieler());
    }

    @Test
    public void testSetUndGetIstAbgeschlossen() {
        turnier.setIstAbgeschlossen(true);
        assertTrue(turnier.getIstAbgeschlossen());
        turnier.setIstAbgeschlossen(false);
        assertFalse(turnier.getIstAbgeschlossen());
    }

    @Test
    public void testSetUndGetSiegpunkteliste() {
        Map<Spieler, Integer> neueMap = new HashMap<>();
        neueMap.put(Alice, 4711);
        turnier.setSiegPunkteListe(neueMap);
        assertEquals(neueMap, turnier.getSiegPunkteListe());
    }

    @Test
    public void testSetUndGetAktuellePartie() {
        Partie p1 = createMockPartie();
        turnier.setAktuellePartie(p1);
        assertEquals(p1, turnier.getAktuellePartie());
    }

    @Test
    public void testSetUndGetAnzahlGespielterPartien() {
        turnier.setAnzahlGespielterPartien(3);
        assertEquals(3, turnier.getAnzahlGespielterPartien());
    }

    @Test
    public void testSetUndGetGewinner() {
        assertNull(turnier.getGewinner());
        turnier.setGewinner(Bob);
        assertEquals(Bob, turnier.getGewinner());
    }

    @Test
    public void testStarteTurnierIstAbgeschlossenTrue() {
        turnier.setIstAbgeschlossen(true);
        turnier.setAnzahlGespielterPartien(4);
        turnier.starteTurnier();
        assertEquals(4, turnier.getAnzahlGespielterPartien());

    }

    @Test
    public void testStarteTurnierUndStarteNaechstePartieIstAbgeschlossenFalse() {
        Partie p1 = createMockPartie();
        turnier.setAnzahlGespielterPartien(4);
        turnier.setAktuellePartie(p1);
        turnier.starteTurnier();
        assertEquals(5, turnier.getAnzahlGespielterPartien());
        assertNotEquals(p1, turnier.getAktuellePartie());
    }

    @Test
    public void testIstBeendet() {
        Map<Spieler, Integer> neueMap = new HashMap<>();
        neueMap.put(Alice, 3);
        neueMap.put(Bob, 3);
        neueMap.put(Charlie, 3);
        turnier.setSiegPunkteListe(neueMap);
        turnier.istBeendet();
        assertFalse(turnier.istAbgeschlossen);
        Map<Spieler, Integer> neueMap2 = new HashMap<>();
        neueMap2.put(Alice, 7);
        neueMap2.put(Bob, 3);
        neueMap2.put(Charlie, 3);
        turnier.setSiegPunkteListe(neueMap2);
        turnier.istBeendet();
        assertTrue(turnier.istAbgeschlossen);
    }

    //Aus dem Partie-Test hierher verlegt, weil Turniereigenschaften geprüft werden

}