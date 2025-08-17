package cosmic.eidex.Service;

import cosmic.eidex.repository.SpielerRepository;
import cosmic.eidex.spielmodell.Spieler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class BestenlisteServiceTest {

    private SpielerRepository spielerRepository;
    private BestenlisteService bestenlisteService;

    @BeforeEach
    void setUp() {
        spielerRepository = mock(SpielerRepository.class);
        bestenlisteService = new BestenlisteService(spielerRepository);
    }

    @Test
    void testGetTop10Spieler() {
        Spieler s1 = new Spieler("Anna", "pw1", 25);
        s1.setSiege(100);
        Spieler s2 = new Spieler("Ben", "pw2", 30);
        s2.setSiege(90);

        List<Spieler> expected = Arrays.asList(s1, s2);
        when(spielerRepository.findTop10ByOrderBySiegeDesc()).thenReturn(expected);

        List<Spieler> result = bestenlisteService.getTop10Spieler();

        assertEquals(expected, result);
        verify(spielerRepository).findTop10ByOrderBySiegeDesc();
    }
}

