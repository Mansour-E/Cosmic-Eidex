package cosmic.eidex.Service.SpielNachrichten;

import cosmic.eidex.spielmodell.Karte;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TrumpfKarteNachrichtTest {
    TrumpfKarteNachricht n;
    Karte k;
    @BeforeEach
    void setUp() {
        n = new TrumpfKarteNachricht(6L,k = new Karte("Herz","Dame"));
    }

    @Test
    void testSetUndGetRaumId() {
        assertEquals(6L, n.getRaumId());
        n.setRaumId(7L);
        assertEquals(7L, n.getRaumId());
    }

    @Test
    void getTyp() {
        assertEquals(SpielNachrichtenTyp.TRUMPF, n.getTyp());
    }

    @Test
    void getTrumpfKarte() {
        assertEquals(k, n.getTrumpfKarte());
    }
}