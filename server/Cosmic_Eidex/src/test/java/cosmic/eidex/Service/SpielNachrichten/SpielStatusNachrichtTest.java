package cosmic.eidex.Service.SpielNachrichten;

import cosmic.eidex.Service.Spielstatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SpielStatusNachrichtTest {
    SpielStatusNachricht n;
    @BeforeEach
    void setUp() {
        n = new SpielStatusNachricht(4L, Spielstatus.WARTET_AUF_SPIELZUEGE);
    }

    @Test
    void getRaumId() {
        assertEquals(4L, n.getRaumId());
    }

    @Test
    void getStatus() {
        assertEquals(Spielstatus.WARTET_AUF_SPIELZUEGE, n.getStatus());
    }

    @Test
    void getTyp() {
        assertEquals(SpielNachrichtenTyp.DRUECKEN_ERLAUBT, n.getTyp());
    }
}