package cosmic.eidex.Service.SpielNachrichten;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class TunierErgebnisNachrichtTest {

    TunierErgebnisNachricht ergebnis;
    HashMap<String, Integer> m;
    HashMap<String, Integer> m2;


    @BeforeEach
    void setUp() {
        m = new HashMap<>();
        m.put("Alice",7);
        m.put("Bob",1);
        m.put("Charlie",2);
        ergebnis = new TunierErgebnisNachricht(42L,m,"Alice");
        m2 = new HashMap<>();
        m2.put("Alice",3);
        m2.put("Bob",7);
        m2.put("Charlie",2);

    }

    @Test
    void getUndSetRaumId() {
        assertEquals(42L,ergebnis.getRaumId());
        ergebnis.setRaumId(43L);
        assertEquals(43L,ergebnis.getRaumId());
    }

    @Test
    void getundSetTunierPunkte() {
        assertEquals(m,ergebnis.getTunierPunkte());
        ergebnis.setTunierPunkte(m2);
        assertEquals(m2,ergebnis.getTunierPunkte());
    }

    @Test
    void getUndSetGewinner() {
        assertEquals("Alice",ergebnis.getGewinner());
        ergebnis.setGewinner("Bob");
        assertEquals("Bob",ergebnis.getGewinner());

    }
}