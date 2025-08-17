package cosmic.eidex.Lobby;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NachrichtTest {

    Nachricht nachricht;

    @BeforeEach
    void setUp() {
        nachricht = new Nachricht();
    }

    @Test
    void setSender() {
        nachricht.setSender("Alice");
        assertEquals("Alice", nachricht.getSender());
        nachricht.setSender("Bob");
        assertEquals("Bob", nachricht.getSender());
    }

    @Test
    void setInhalt() {
        nachricht.setInhalt("Hallo");
        assertEquals("Hallo", nachricht.getInhalt());
        nachricht.setInhalt("Welt");
        assertEquals("Welt", nachricht.getInhalt());
    }
}