package cosmic.eidex.spielmodell;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SpielStandTest {

    private Spieler Alice;
    private Spieler Bob;
    private Spieler Charlie;
    private SpielStand spielstand;

    //TODO: Java Docs
    //Testspieler
    private Spieler createMockSpieler(String name) {
        Spieler spieler = mock(Spieler.class);
        when(spieler.getNickname()).thenReturn(name);
        return spieler;
    }

    @BeforeEach
    void setUp() {
        Alice = createMockSpieler("Alice");
        Bob = createMockSpieler("Bob");
        Charlie= createMockSpieler("Charlie");
        List<Spieler> spielerliste = Arrays.asList(Bob, Alice, Charlie);
        spielstand = new SpielStand(spielerliste);

    }

    @Test
    void testSetUndGetPunkte() {
        assertEquals(0, spielstand.getPunkte(Alice));
        assertEquals(0,spielstand.getPunkte(Bob));
        assertEquals(0,spielstand.getPunkte(Charlie));
        spielstand.setPunkte(Alice, 10);
        spielstand.setPunkte(Bob, 7);
        assertEquals(10, spielstand.getPunkte(Alice));
        assertEquals(7, spielstand.getPunkte(Bob));
        assertEquals(0, spielstand.getPunkte(Charlie));
    }

    @Test
    void zeigeSpielstand() {
        spielstand.setPunkte(Alice, 4);
        spielstand.setPunkte(Bob, 2);
        List<String> ausgabe = spielstand.zeigeSpielstand();
        assertEquals(3, ausgabe.size());
        assertTrue(ausgabe.contains("Alice: 4 Punkte"));
        assertTrue(ausgabe.contains("Bob: 2 Punkte"));
        assertTrue(ausgabe.contains("Charlie: 0 Punkte"));

    }

}