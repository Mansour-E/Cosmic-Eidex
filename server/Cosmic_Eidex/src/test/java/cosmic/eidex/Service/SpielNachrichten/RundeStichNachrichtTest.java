package cosmic.eidex.Service.SpielNachrichten;

import cosmic.eidex.spielmodell.Karte;
import cosmic.eidex.spielmodell.Runde;
import cosmic.eidex.spielmodell.StichStapel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RundeStichNachrichtTest {
    RundeStichNachricht n;
    Runde runde1;
    Runde runde2;
    StichStapel stichStapel1;
    StichStapel stichStapel2;
    Karte k;


    @BeforeEach
    void setUp() {
        k = new Karte("Eidex","10");
        n = new RundeStichNachricht(2L,runde1,stichStapel1);
    }

    @Test
    void testSetUndGetRaumId() {
        assertEquals(2L,n.getRaumId());
        n.setRaumId(3L);
        assertEquals(3L,n.getRaumId());
    }

    @Test
    void testSetUndGetRunde() {
        assertEquals(runde1,n.getRunde());
        n.setRunde(runde2);
        assertEquals(runde2,n.getRunde());
    }

    @Test
    void testSetUndGetStichStapel() {
        assertEquals(stichStapel1,n.getStichStapel());
        n.setStichStapel(stichStapel2);
        assertEquals(stichStapel2,n.getStichStapel());
    }

}