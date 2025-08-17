package cosmic.eidex.Service.SpielNachrichten;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PartiePunkteNachrichtTest {

    private PartiePunkteNachricht n;
    private Map<String, Integer> m;
    private Map<String, Integer> m2;

    @BeforeEach
    void setUp() {
        m = new HashMap<>();
        m2 = new HashMap<>();
        m.put("Alice",4711);
        m.put("Bob",123);
        m.put("Charlie",456);
        m2.put("Charlie",456);
        n = new PartiePunkteNachricht(123L,m);
    }

    @Test
    void getUndSetRaumId() {
        assertEquals(123L,n.getRaumId());
        n.setRaumId(456L);
        assertEquals(456L,n.getRaumId());
    }

    @Test
    void getUndSetPartiePunkte() {
        assertEquals(m,n.getPartiePunkte());
        n.setPartiePunkte(m2);
        assertEquals(m2,n.getPartiePunkte());
    }
}