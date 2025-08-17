package cosmic.eidex.DTO;

import cosmic.eidex.Lobby.Nachricht;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

class NachrichtDTOTest {

    @Test
    void testNachrichtDTOConstructorAndGetters() {
        OffsetDateTime zeit = OffsetDateTime.now();
        Nachricht nachricht = new Nachricht("user1", "Hallo Welt");
        nachricht.setZeitstempel(zeit);

        NachrichtDTO dto = new NachrichtDTO(nachricht);

        assertEquals("user1", dto.getSender());
        assertEquals("Hallo Welt", dto.getInhalt());
        assertEquals(zeit, dto.getZeitstempel());
    }

    @Test
    void testNachrichtDTODefaultConstructor() {
        NachrichtDTO dto = new NachrichtDTO();

        assertNull(dto.getSender());
        assertNull(dto.getInhalt());
        assertNull(dto.getZeitstempel());
    }
}