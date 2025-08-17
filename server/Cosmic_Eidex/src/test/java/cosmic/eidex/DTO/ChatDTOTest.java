package cosmic.eidex.DTO;

import cosmic.eidex.Lobby.Nachricht;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ChatDTOTest {

    @Test
    void testChatDTOConstructorAndGetter() {
        OffsetDateTime now = OffsetDateTime.now();
        Nachricht n1 = new Nachricht("user1", "Hallo");
        n1.setZeitstempel(now);
        Nachricht n2 = new Nachricht("user2", "Antwort");
        n2.setZeitstempel(now.plusMinutes(1));

        NachrichtDTO dto1 = new NachrichtDTO(n1);
        NachrichtDTO dto2 = new NachrichtDTO(n2);

        ChatDTO chatDTO = new ChatDTO(List.of(dto1, dto2));

        assertNotNull(chatDTO.getNachrichten());
        assertEquals(2, chatDTO.getNachrichten().size());
        assertEquals("user1", chatDTO.getNachrichten().get(0).getSender());
        assertEquals("Hallo", chatDTO.getNachrichten().get(0).getInhalt());
        assertEquals(now, chatDTO.getNachrichten().get(0).getZeitstempel());

        assertEquals("user2", chatDTO.getNachrichten().get(1).getSender());
        assertEquals("Antwort", chatDTO.getNachrichten().get(1).getInhalt());
        assertEquals(now.plusMinutes(1), chatDTO.getNachrichten().get(1).getZeitstempel());
    }

    @Test
    void testChatDTODefaultConstructor() {
        ChatDTO chatDTO = new ChatDTO();
        assertNull(chatDTO.getNachrichten());
    }
}
