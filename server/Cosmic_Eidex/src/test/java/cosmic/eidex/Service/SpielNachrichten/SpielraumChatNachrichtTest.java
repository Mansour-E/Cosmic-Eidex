package cosmic.eidex.Service.SpielNachrichten;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SpielraumChatNachrichtTest {
    SpielraumChatNachricht n;
    @BeforeEach
    void setUp() {
        n = new SpielraumChatNachricht(3L,"sender","Hallo");
    }

    @Test
    void testSetUndGetRaumId() {
        assertEquals(3L,n.getRaumId());
        n.setRaumId(4L);
        assertEquals(4L,n.getRaumId());
    }
}