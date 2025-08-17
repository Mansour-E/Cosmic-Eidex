package cosmic.eidex.Service.SpielNachrichten;

import cosmic.eidex.spielmodell.Karte;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class KartenAusgeteiltNachrichtTest {
    KartenAusgeteiltNachricht n;
    Karte k;
    @BeforeEach
    void setUp() {
        k = new Karte("Eidex","10");
        n = new KartenAusgeteiltNachricht(2L,"jemand", List.of(k));
    }

    @Test
    void getRaumId() {
        assertEquals(2L, n.getRaumId());
    }

    @Test
    void getTyp() {
        assertEquals(SpielNachrichtenTyp.KARTEN_AUSGETEILT, n.getTyp());
    }

    @Test
    void getHandkarten() {
        assertEquals(List.of(k), n.getHandkarten());
    }

    @Test
    void getEmpfaenger() {
        assertEquals("jemand", n.getEmpfaenger());
    }
}