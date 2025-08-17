package cosmic.eidex.DTO;

import cosmic.eidex.Lobby.Bestenliste;
import cosmic.eidex.repository.SpielerRepository;
import cosmic.eidex.spielmodell.Spieler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BestenlisteDTOTest {

    private SpielerRepository spielerRepository;
    private Bestenliste bestenliste;

    @BeforeEach
    void setUp() {
        spielerRepository = mock(SpielerRepository.class);
        bestenliste = new Bestenliste(spielerRepository);
    }

    @Test
    void testLeererKonstruktor() {
        BestenlisteDTO bestenlisteDTO = new BestenlisteDTO();
        assertNotNull(bestenlisteDTO);
        assertNull(bestenlisteDTO.getTop10Spieler());
    }

    @Test
    void testBestenlisteDTOErstellung() {
        Spieler s1 = new Spieler("Anna", "pw", 0);
        s1.setSiege(10);
        Spieler s2 = new Spieler("Bob", "pw", 0);
        s2.setSiege(7);

        when(spielerRepository.findAll()).thenReturn(Arrays.asList(s1, s2));

        BestenlisteDTO dto = new BestenlisteDTO(bestenliste);
        List<SpielerDTO> topSpieler = dto.getTop10Spieler();

        assertNotNull(topSpieler);
        assertEquals(2, topSpieler.size());
        assertEquals("Anna", topSpieler.get(0).getNickname());
        assertEquals(10, topSpieler.get(0).getSiege());
        assertEquals("Bob", topSpieler.get(1).getNickname());
        assertEquals(7, topSpieler.get(1).getSiege());
    }
}
