package cosmic.eidex.Service.SpielNachrichten;

import cosmic.eidex.spielmodell.Karte;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GueltigeKartenNachrichtTest {
    GueltigeKartenNachricht n;
    Karte k1;
    Karte k2;
    ArrayList<Karte> karten = new ArrayList<>();
    ArrayList<Karte> karten1 = new ArrayList<>();

    @BeforeEach
    void setUp() {
        karten.add(k1);
        karten1.add(k2);
        karten1.add(k1);
        n = new GueltigeKartenNachricht(123L, "Alice", karten);
    }

    @Test
    void getUndSetRaumId() {
        assertEquals(123L, n.getRaumId());
        n.setRaumId(456L);
        assertEquals(456L, n.getRaumId());
    }

    @Test
    void getUndSetSpieler() {
        assertEquals("Alice", n.getSpieler());
        n.setSpieler("Alice, Bob");
        assertEquals("Alice, Bob", n.getSpieler());
    }

    @Test
    void getUndSetGueltigeKarten() {
        assertEquals(karten, n.getGueltigeKarten());
        n.setGueltigeKarten(karten1);
        assertEquals(karten1, n.getGueltigeKarten());
    }
}