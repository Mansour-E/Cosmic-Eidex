package cosmic.eidex.Service.SpielNachrichten;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SpielraumStartNachrichtTest {
    SpielraumStartNachricht n;
    @BeforeEach
    void setUp() {
        n = new SpielraumStartNachricht(4L,"spieler");
    }

    @Test
    void testSetUndGetRaumId() {
        assertEquals(4L,n.getRaumId());
        n.setRaumId(5L);
        assertEquals(5L,n.getRaumId());
    }

    @Test
    void getSpieler() {
        assertEquals("spieler",n.getSpieler());
    }
}