package cosmic.eidex.cont;

import cosmic.eidex.Service.BestenlisteService;
import cosmic.eidex.spielmodell.Spieler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class BestenlisteContTest {

    @Mock
    private BestenlisteService mockBestenlisteService;

    @InjectMocks
    private BestenlisteCont controller;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendeTop10() {
        Spieler s1 = new Spieler("User1", "pw1");
        Spieler s2 = new Spieler("User2", "pw2");
        List<Spieler> expectedList = List.of(s1, s2);

        when(mockBestenlisteService.getTop10Spieler()).thenReturn(expectedList);

        List<Spieler> result = controller.sendeTop10();

        assertEquals(expectedList, result);
        assertEquals(2, result.size());
        assertEquals("User1", result.get(0).getNickname());
    }
}
