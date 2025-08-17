package cosmic.eidex.spielmodell;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class SpielerTest {

    private Spieler spieler;

    @BeforeEach
    void setUp() {
        spieler = new Spieler("TestUser", "pass123", 25);
    }

    @Test
    void testConstructorWithNicknameAndPassword() {
        Spieler s = new Spieler("Test", "1234");
        assertEquals("Test", s.getNickname());
        assertEquals("1234", s.getPasswort());
        assertEquals(0, s.getSiege());
        assertEquals(0, s.getPunkte());
        assertFalse(s.isBereit());
        assertNull(s.getToken());
        assertNotNull(s.getSpielerHand());
    }

    @Test
    void testConstructorWithAlter() {
        assertEquals("TestUser", spieler.getNickname());
        assertEquals("pass123", spieler.getPasswort());
        assertEquals(0, spieler.getSiege());
        assertEquals(0, spieler.getPunkte());
        assertFalse(spieler.isBereit());
        assertNull(spieler.getToken());
        assertNotNull(spieler.getSpielerHand());
    }

    @Test
    void testSetUndGetNickname() {
        spieler.setNickname("NeuName");
        assertEquals("NeuName", spieler.getNickname());
    }

    @Test
    void testSetUndGetPasswort() {
        spieler.setPasswort("neuesPasswort");
        assertEquals("neuesPasswort", spieler.getPasswort());
    }

    @Test
    void testSetUndGetSiege() {
        spieler.setSiege(5);
        assertEquals(5, spieler.getSiege());
    }

    @Test
    void testSetUndGetPunkte() {
        spieler.setPunkte(42);
        assertEquals(42, spieler.getPunkte());
    }

    @Test
    void testBereit() {
        assertFalse(spieler.isBereit());
        spieler.setBereit(true);
        assertTrue(spieler.isBereit());
    }

    @Test
    void testSetUndGetSpielerHand() {
        SpielerHand neueHand = new SpielerHand(new ArrayList<>(), spieler);
        spieler.setSpielerHand(neueHand);
        assertEquals(neueHand, spieler.getSpielerHand());
    }

    @Test
    void testToken() {
        assertNull(spieler.getToken());
        spieler.setToken("abc123");
        assertEquals("abc123", spieler.getToken());
    }

    @Test
    void testSpielerHandErzeugtWennNull() {
        Spieler neuerSpieler = new Spieler("Test", "pw");
        neuerSpieler.spielerHand = null;
        SpielerHand hand = neuerSpieler.getSpielerHand();
        assertNotNull(hand);
        assertEquals(neuerSpieler, hand.getSpieler());
    }

    @Test
    void testSchreibeNachricht() {
        // Dies ist nur ein einfacher System.out-Aufruf,
        // kein echter Unit-Test möglich – könnte ggf. per Log-Abfang getestet werden.
        assertDoesNotThrow(() -> spieler.schreibeNachricht("Hallo Welt"));
    }
}

