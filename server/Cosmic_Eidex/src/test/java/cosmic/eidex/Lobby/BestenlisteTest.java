package cosmic.eidex.Lobby;

import cosmic.eidex.repository.SpielerRepository;
import cosmic.eidex.spielmodell.Spieler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BestenlisteTest {
    private SpielerRepository repo;
    private Bestenliste bestenliste;

private final Map<String, Spieler> simulierteDatenbank = new HashMap<>();

    @BeforeEach
    void setUp() {

        //Testvariante für SpielerRepository
        repo = mock(SpielerRepository.class);
        bestenliste = new Bestenliste(repo);

        when(repo.save(any())).thenAnswer(invocation -> {
            Spieler s = invocation.getArgument(0);
            simulierteDatenbank.put(s.getNickname(), s);
            return s;
        });
        when(repo.findAll()).thenAnswer(inv -> new ArrayList<>(simulierteDatenbank.values()));
    }
    @Test
    void testAktualisiereNeuenSpieler() {
        Spieler s1 = new Spieler("Max", "max123");
        bestenliste.aktualisiere(s1);
        List<Spieler> top = bestenliste.gibTop10();
        assertEquals(1, top.size());
        assertEquals("Max", top.getFirst().getNickname());
    }

    @Test
    void testAktualisierungErhoehtSieganzahl() {
        Spieler s = new Spieler("Max", "max123");
        bestenliste.aktualisiere(s);
        bestenliste.aktualisiere(s);
        bestenliste.aktualisiere(s);

        // Direkt aus der Map prüfen
        int siege = bestenliste.getEintraege().get(s);
        assertEquals(3, siege);
    }

    @Test
    void testGibTop10MitMehrAls10Spielern() {
        for (int i = 0; i < 15; i++) {
            Spieler s = new Spieler("Spieler" + i, "passwort" + i);
            for (int j = 0; j <= i; j++) {
                bestenliste.aktualisiere(s); // Spieler i gewinnt (i+1)-mal
            }
        }
        List<Spieler> top = bestenliste.gibTop10();
        assertEquals(10, top.size());
        assertEquals("Spieler14", top.get(0).getNickname());
    }

    @Test
    void testSortierungBeiGleichenSiegen() {
        Spieler s1 = new Spieler("Anna", "anna123");
        Spieler s2 = new Spieler("Berta", "berta123");

        for (int i = 0; i < 3; i++) {
            bestenliste.aktualisiere(s1);
            bestenliste.aktualisiere(s2);
        }

        List<Spieler> top = bestenliste.gibTop10();
        assertTrue(top.contains(s1));
        assertTrue(top.contains(s2));
    }

    @Test
    void testNullSpielerAktualisieren() {
        assertDoesNotThrow(() -> bestenliste.aktualisiere(null));
        assertEquals(0, bestenliste.gibTop10().size());
    }

    @Test
    void testGibTop10MitWenigerAls10Spielern() {
        for (int i = 0; i < 3; i++) {
            Spieler s = new Spieler("S" + i, "passwort" + i);
            bestenliste.aktualisiere(s);
        }
        List<Spieler> top = bestenliste.gibTop10();
        assertEquals(3, top.size());
    }

}
