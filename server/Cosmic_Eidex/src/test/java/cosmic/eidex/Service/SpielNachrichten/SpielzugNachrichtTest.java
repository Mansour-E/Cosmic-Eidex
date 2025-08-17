package cosmic.eidex.Service.SpielNachrichten;

import cosmic.eidex.spielmodell.Karte;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SpielzugNachrichtTest {
    SpielzugNachricht n;
    Karte k;
    @BeforeEach
    void setUp() {
        n = new SpielzugNachricht(5L,"spieler",k = new Karte("Stern","7"));
    }

    @Test
    void testSetUndGetRaumId() {
        assertEquals(5L,n.getRaumId());
        n.setRaumId(6L);
        assertEquals(6L,n.getRaumId());
    }

    @Test
    void getTyp() {
        assertEquals(SpielNachrichtenTyp.SPIELZUG,n.getTyp());
    }

    @Test
    void getSpieler() {
        assertEquals("spieler",n.getSpieler());
    }

    @Test
    void getGespielteKarte() {
        assertEquals(k,n.getGespielteKarte());
    }
}