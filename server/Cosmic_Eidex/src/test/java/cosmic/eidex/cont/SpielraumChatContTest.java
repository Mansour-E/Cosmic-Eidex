package cosmic.eidex.cont;

import cosmic.eidex.Service.SpielNachrichten.SpielraumChatNachricht;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SpielraumChatContTest {

    @Test
    void testHandleChatMessage() {
        SpielraumChatCont cont = new SpielraumChatCont();
        Long raumId = 123L;
        SpielraumChatNachricht nachricht = new SpielraumChatNachricht(raumId,"Alice","Hallo");

        SpielraumChatNachricht result = cont.handleChatMessage(raumId,nachricht);
        assertNotNull(result);
        assertEquals(raumId,result.getRaumId());
        assertEquals("Hallo",result.getInhalt());

    }
}