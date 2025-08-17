package cosmic.eidex.Service.SpielNachrichten;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RundenPunkteNachrichtTest {

    public RundenPunkteNachricht n;

    @BeforeEach
    void setUp() {
        n = new RundenPunkteNachricht(123L,"Alice",1);
    }

    @Test
    void getUndSetRaumId() {
        assertEquals(123L,n.getRaumId());
        n.setRaumId(456L);
        assertEquals(456L,n.getRaumId());
    }

    @Test
    void getUndSetSpieler() {
        assertEquals("Alice",n.getSpieler());
        n.setSpieler("Bob");
        assertEquals("Bob",n.getSpieler());
    }

    @Test
    void getUndSetPunkte() {
        assertEquals(1,n.getPunkte());
        n.setPunkte(11);
        assertEquals(11,n.getPunkte());
    }
}