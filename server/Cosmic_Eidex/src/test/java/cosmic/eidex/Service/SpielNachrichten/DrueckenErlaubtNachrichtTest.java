package cosmic.eidex.Service.SpielNachrichten;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DrueckenErlaubtNachrichtTest {
    DrueckenErlaubtNachricht n;
    @BeforeEach
    void setUp() {
        n = new DrueckenErlaubtNachricht(1L);
    }

    @Test
    void getRaumId() {
        assertEquals(1L, n.getRaumId());
    }

    @Test
    void getTyp() {
        assertEquals(SpielNachrichtenTyp.DRUECKEN_ERLAUBT, n.getTyp());
    }
}