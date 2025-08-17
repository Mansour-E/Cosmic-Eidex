package cosmic.eidex.Service.SpielNachrichten;

import cosmic.eidex.spielmodell.Karte;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class KartenGedruecktNachrichtTest {
    KartenGedruecktNachricht n;
    Karte k;
    @BeforeEach
    void setUp() {
        k = new Karte("Eidex","10");
        n = new KartenGedruecktNachricht(2L,"jemand", k);
    }

    @Test
    void testSetUndGetRaumId() {
        assertEquals(2L, n.getRaumId());
        n.setRaumId(3L);
        assertEquals(3L, n.getRaumId());
    }

    @Test
    void getTyp() {
        assertEquals(SpielNachrichtenTyp.KARTEN_GEDRUECKT, n.getTyp());
    }

    @Test
    void getSpieler() {
        assertEquals("jemand", n.getSpieler());
    }

    @Test
    void getGedrueckteKarte() {
        assertEquals(k, n.getGedrueckteKarte());
    }
}