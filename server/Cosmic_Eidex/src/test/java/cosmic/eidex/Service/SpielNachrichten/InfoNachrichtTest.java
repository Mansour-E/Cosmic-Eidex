package cosmic.eidex.Service.SpielNachrichten;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InfoNachrichtTest {

    private InfoNachricht n;

    @BeforeEach
    void setUp() {
        n = new InfoNachricht(123L, "Info","Wichtige Information!");
    }

    @Test
    void getUndSetRaumId() {
        assertEquals(123L, n.getRaumId());
        n.setRaumId(456L);
        assertEquals(456L, n.getRaumId());
    }

    @Test
    void getUndSetInfo() {
        assertEquals("Info", n.getInfoTyp());
        n.setInfoTyp("Info2");
        assertEquals("Info2", n.getInfoTyp());
    }

    @Test
    void getUndSetInfoTyp() {
        assertEquals("Wichtige Information!", n.getInfo());
        n.setInfo("Noch viel wichtigere Information");
        assertEquals("Noch viel wichtigere Information", n.getInfo());
    }
}