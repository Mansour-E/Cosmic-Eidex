package cosmic.eidex.cont;

import cosmic.eidex.DTO.SpielraumDTO;
import cosmic.eidex.Lobby.Spielraum;
import cosmic.eidex.Service.SpielNachrichten.KartenGedruecktNachricht;
import cosmic.eidex.Service.SpielNachrichten.SpielraumStartNachricht;
import cosmic.eidex.Service.SpielNachrichten.SpielzugNachricht;
import cosmic.eidex.Service.SpielraumService;
import cosmic.eidex.spielmodell.Karte;
import cosmic.eidex.spielmodell.Partie;
import cosmic.eidex.spielmodell.Spieler;
import cosmic.eidex.spielmodell.Turnier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SpielraumContTest {

    private SpielraumService spielraumService;
    private SpielraumCont controller;
    private final SimpMessagingTemplate messagingTemplate = null;
    private SpielraumStartNachricht message;

    private Spieler Alice;
    private Spieler Ben;
    private Spieler Anna;
    private Spieler Eva;
    //Testspieler
    private Spieler createMockSpieler(String name) {
        Spieler spieler = mock(Spieler.class, withSettings().useConstructor(name, "psswd"));
        when(spieler.getNickname()).thenReturn(name);
        return spieler;
    }

    @BeforeEach
    void setUp() {
        Ben = createMockSpieler("Ben");
        Alice = createMockSpieler("Alice");
        Eva = createMockSpieler("Eva");
        Anna = createMockSpieler("Anna");
        message = new SpielraumStartNachricht(1L,"Alice");

        spielraumService = mock(SpielraumService.class);
        controller = new SpielraumCont(spielraumService, messagingTemplate);
    }
    @Test
    void testErstelleSpiel() {
        Spielraum raum = new Spielraum("TestRaum");
        raum.setSpieler(List.of(Alice));
        when(spielraumService.createSpielraum("TestRaum", "1234")).thenReturn(raum);

        SpielraumDTO dto = controller.erstelleSpiel("TestRaum", "1234");

        assertEquals("TestRaum", dto.getName());
        assertEquals(1, dto.getSpieler().size());
        assertEquals("Alice", dto.getSpieler().getFirst().getNickname());
        verify(spielraumService).createSpielraum("TestRaum", "1234");
    }



    @Test
    void testBeitreten() {
        Spieler spieler = new Spieler("Bob", "pw", 22);
        when(spielraumService.beitreten(1L, spieler)).thenReturn(true);

        assertTrue(controller.beitreten(1L, spieler));
        verify(spielraumService).beitreten(1L, spieler);
    }

    @Test
    void testStarteSpiel() {
        Spielraum raum = mock(Spielraum.class);
        when(spielraumService.getSpielraumById(1L)).thenReturn(Optional.of(raum));

        assertEquals(message,controller.starteSpiel(1L,message));
        verify(spielraumService).starteSpiel(1L);
        verify(raum, never()).starteSpiel();
    }

    @Test
    void testGetAlleSpielraeume() {
        Spielraum raum1 = new Spielraum("Raum1");
        raum1.setSpieler(List.of(Anna));
        Spielraum raum2 = new Spielraum("Raum2");
        raum2.setSpieler(List.of(Ben));

        when(spielraumService.getAlleSpielraeume()).thenReturn(Arrays.asList(raum1, raum2));

        List<SpielraumDTO> result = controller.getAlleSpielraeume();

        assertEquals(2, result.size());
        assertEquals("Raum1", result.getFirst().getName());
        assertEquals("Anna", result.getFirst().getSpieler().getFirst().getNickname());
    }

    @Test
    void testGetSpielraumNames() {
        Spielraum raum1 = new Spielraum("Raum1");
        Spielraum raum2 = new Spielraum("Raum2");
        when(spielraumService.getAlleSpielraeume()).thenReturn(Arrays.asList(raum1, raum2));
        List<String> result = controller.getSpielraumNames();
        assertEquals(2, result.size());
        assertEquals("Raum1", result.getFirst());
        assertEquals("Raum2", result.get(1));
        verify(spielraumService).getAlleSpielraeume();
    }


    @Test
    void testGetSpielraumById_found() {
        Spielraum raum = new Spielraum("RaumX");
        raum.setSpieler(List.of(Eva));

        when(spielraumService.getSpielraumById(1L)).thenReturn(Optional.of(raum));

        SpielraumDTO dto = controller.getSpielraumById(1L);
        assertEquals("RaumX", dto.getName());
        assertEquals("Eva", dto.getSpieler().getFirst().getNickname());
    }



    @Test
    void testGetSpielraumById_notFound() {
        when(spielraumService.getSpielraumById(999L)).thenReturn(Optional.empty());
        RuntimeException exception = assertThrows(RuntimeException.class, () -> controller.getSpielraumById(999L));
        assertEquals("Spielraum nicht gefunden mit ID: 999", exception.getMessage());
    }

    @Test
    void testVerlasseSpielraum() {
        when(spielraumService.verlasse(1L, "Nick"))
                .thenReturn(true);
        assertTrue(controller.verlasseSpielraum(1L, "Nick"));
    }

    @Test
    void testSetBereitStatus() {
        controller.setBereitStatus(5L,"Alice", true);
        verify(spielraumService).setBereitStatus(5L,"Alice", true);
    }

    @Test
    void testGetAlleSpielRaeume() {
        when(spielraumService.getAlleSpielraeume()).thenReturn(List.of());
        assertEquals(controller.sendeAlleSpielraeume(), List.of());
    }

    @Test
    void testSendeSpielzug_SpielerNotFound() {
        SpielraumService service = spy(new SpielraumService(null, null, null, null));
        Spielraum raum = mock(Spielraum.class);
        SpielraumCont cont = new SpielraumCont(service, messagingTemplate);
        when(raum.getTurnier()).thenReturn(new Turnier(List.of(Alice,Anna,Eva)));
        when(raum.getSpieler()).thenReturn(List.of(Alice, Anna, Eva));
        when(service.findeInSpielraumListe(5L)).thenReturn(raum);
        SpielzugNachricht msg = new SpielzugNachricht(5L, "Hans", new Karte("Eidex", "8"));
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> cont.sendeSpielzug(5L, msg)
        );
        assertEquals("Spieler nicht gefunden: Hans", exception.getMessage());
    }
}
