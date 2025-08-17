package cosmic.eidex.DTO;

import cosmic.eidex.spielmodell.Spieler;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SpielerDTOTest {

    @Test
    void testDefaultConstructor() {
        SpielerDTO dto = new SpielerDTO();
        assertNull(dto.getNickname());
        assertNull(dto.getSiege());
    }

    @Test
    void testConstructorWithSpieler() {
        Spieler spieler = new Spieler("Alice", "geheim");
        spieler.setSiege(5);

        SpielerDTO dto = new SpielerDTO(spieler);

        assertEquals("Alice", dto.getNickname());
        assertEquals(5, dto.getSiege());
    }
}
