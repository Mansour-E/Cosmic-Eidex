package cosmic.eidex.Service;

import cosmic.eidex.Bots.EinfacherBot;
import cosmic.eidex.Bots.SchwererBot;
import cosmic.eidex.DTO.SpielraumDTO;
import cosmic.eidex.Lobby.Spielraum;
import cosmic.eidex.repository.SpielerRepository;
import cosmic.eidex.repository.SpielraumRepository;
import cosmic.eidex.spielmodell.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpielraumServiceTest {

    @Mock
    private SpielraumRepository spielraumRepository;

    @Mock
    private SpielerRepository spielerRepository;

    @Mock
    SpielraumPublisher publisher;

    @Mock
    SimpMessagingTemplate messaging;

    @InjectMocks
    private SpielraumService spielraumService;

    private List<Spielraum> spielraumListe = new ArrayList<>();

    private Map<Long, Spielraum> aktiveRaeume = new ConcurrentHashMap<>();

    @Captor
    ArgumentCaptor<String> topicCaptor;

    @Captor
    ArgumentCaptor<Object> payloadCaptor;

    String nick;
    Spieler s1;
    Spieler s2;
    Spieler s3;
    Spielraum raum1;
    Spielraum raum2;
    Turnier turnier;
    Turnier turnier2;
    Partie partie;
    EinfacherBot bot1;
    SchwererBot bot2;
    HashMap<Spieler,Integer> m;



    @BeforeEach
    void setup() {
        nick = "Nick";
        s1 = new Spieler("Alice","pw1");
        s2 = new Spieler("Bob","pw2");
        s3 = new Spieler("Charlie","pw3");
        bot1 = new EinfacherBot();
        bot2 = new SchwererBot();
        raum1 = Mockito.spy(new Spielraum("Raum1"));
        raum2 = new Spielraum("Raum2");
        turnier = new Turnier(List.of(s1,bot1,s2));
        turnier2 = new Turnier(List.of(s1,s2,s3));
        partie = mock(Partie.class);
    }

    @Test
    void testCreateSpielraum_Erfolgreich() {
        String name = "Raum1";
        String passwort = "geheim";

        when(spielraumRepository.existsByName(name)).thenReturn(false);
        when(spielraumRepository.save(any(Spielraum.class))).thenAnswer(invocation -> (Spielraum) invocation.getArgument(0));

        Spielraum raum = spielraumService.createSpielraum(name, passwort);
        assertNotNull(raum);
        assertEquals(name, raum.getName());
        assertEquals(passwort, raum.getPasswort());
        verify(spielraumRepository).save(raum);
        verify(publisher).sendSpielraumUpdate(any());
    }

    @Test
    void testCreateSpielraum_NameSchonVorhanden() {
        String name = "Raum1";
        when(spielraumRepository.existsByName(name)).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                spielraumService.createSpielraum(name, "pw"));

        assertEquals("Ein Spielraum mit diesem Namen existiert bereits.", ex.getMessage());
        verify(spielraumRepository, never()).save(any());
    }

    @Test
    void testBeitreten_Erfolgreich() {
        Long raumId = 1L;

        when(spielraumRepository.findById(raumId)).thenReturn(Optional.of(raum1));

        boolean result = spielraumService.beitreten(raumId, s1);

        assertTrue(result);
        verify(spielraumRepository).save(raum1);
    }

    @Test
    void testBeitreten_RaumNichtGefunden() {
        when(spielraumRepository.findById(1L)).thenReturn(Optional.empty());

        boolean result = spielraumService.beitreten(1L, new Spieler("Nick", "pw", 20));

        assertFalse(result);
        verify(spielraumRepository, never()).save(any());
    }

    @Test
    void testGetAlleSpielraeume() {
        List<Spielraum> raeume = List.of(raum1,raum2);
        when(spielraumRepository.findAll()).thenReturn(raeume);

        List<Spielraum> result = spielraumService.getAlleSpielraeume();

        assertEquals(2, result.size());
        assertEquals("Raum1", result.getFirst().getName());
    }

    @Test
    void testGetSpielraumByName() {
        when(spielraumRepository.findByName("Raum1")).thenReturn(Optional.of(raum1));

        Optional<Spielraum> result = spielraumService.getSpielraumByName("Raum1");

        assertTrue(result.isPresent());
        assertEquals("Raum1", result.get().getName());
    }

    @Test
    void testGetSpielraumById_SpielerWiederherstellung() {
        Long id = 10L;
        raum1.beitreten(s1);
        raum1.beitreten(s2);

        when(spielraumRepository.findById(id)).thenReturn(Optional.of(raum1));

        Optional<Spielraum> result = spielraumService.getSpielraumById(id);

        assertTrue(result.isPresent());
        assertEquals(2, result.get().getSpieler().size());
        assertEquals("Alice", result.get().getSpieler().getFirst().getNickname());
    }

    @Test
    void testVerlasse_EntfernenErfolgreich_MitSpielernImRaum() {
        Long raumId = 80L;
        raum1.beitreten(s1);
        raum1.beitreten(s2);
        when(spielraumRepository.findById(raumId)).thenReturn(Optional.of(raum1));
        boolean result = spielraumService.verlasse(raumId, "Alice");

        assertTrue(result);
        verify(spielraumRepository).save(raum1);
        verify(spielraumRepository, never()).delete(any());
    }

    @Test
    void testVerlasseLetzterSpielerLoescht() {
        Long id = 7L;
        raum1.getSpieler().add(s1);
        raum1.getPlatzMap().put(0,"Solo");
        when(spielraumRepository.findById(id)).thenReturn(Optional.of(raum1));
        boolean ok = spielraumService.verlasse(id,"Alice");
        assertTrue(ok);
        verify(spielraumRepository).delete(raum1);
        verify(publisher).sendSpielraumUpdate(any());
    }

    @Test
    void testVerlasse_EntfernenNichtErfolgreich() {
        Long raumId = 1L;

        Spielraum raum = mock(Spielraum.class);
        when(spielraumRepository.findById(raumId)).thenReturn(Optional.of(raum));

        boolean result = spielraumService.verlasse(raumId, nick);

        assertFalse(result);
        verify(spielraumRepository, never()).save(any());
        verify(spielraumRepository, never()).delete(any());
    }

    @Test
    void testSetBereit() {
        Long id = 5L;
        raum1.getSpieler().add(s1);
        when(spielraumRepository.findById(id)).thenReturn(Optional.of(raum1));
        spielraumService.setBereitStatus(id,"Alice",true);
        assertTrue(s1.isBereit());
        verify(spielraumRepository).save(raum1);
        verify(messaging).convertAndSend(topicCaptor.capture(), payloadCaptor.capture());
        assertEquals("/topic/imspielraum/"+id, topicCaptor.getValue());
    }

    @Test
    void testEntferneSpielraum() {
        Long id = 9L;
        when(spielraumRepository.findById(id)).thenReturn(Optional.of(raum1));
        spielraumService.entferneSpielraum(id);
        verify(spielraumRepository).delete(raum1);
        verify(publisher).sendSpielraumUpdate(any());
    }

    @Test
    void testEntferneSpielraumDerNichtExistiert() {
        when(spielraumRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> spielraumService.entferneSpielraum(99L));
    }

    @Test
    void testBeitretenNameSchonVorhanden() {
        Long id = 119L;
        raum1.getPlatzMap().put(0,"Alice");
        when(spielraumRepository.findById(id)).thenReturn(Optional.of(raum1));
        assertTrue(spielraumService.beitreten(id, s1));
        assertEquals(1,raum1.getPlatzMap().size());
        verify(messaging).convertAndSend(eq("/topic/imspielraum/"+id), any(Object.class));
    }

    @Test
    void testFindeSpielerMitName() {
        List<Spieler> spielers = Arrays.asList(s1, s2);
        Spieler gefunden = ReflectionTestUtils
                .invokeMethod(spielraumService,"findeSpielerMitName", spielers, "Bob");
        assertEquals(s2,gefunden);
        assertThrows(RuntimeException.class, () ->
                ReflectionTestUtils.invokeMethod(spielraumService,"findeSpielerMitName", spielers, "Charlie"));

    }
    @Test
    void testFindeInSpielraumListe() {
        Long id = 42L;
        ConcurrentHashMap<Long, Spielraum> map = new ConcurrentHashMap<>();
        map.put(id, raum1);
        ReflectionTestUtils.setField(spielraumService, "aktiveRaeume", map);

        assertSame(raum1, spielraumService.findeInSpielraumListe(id));
    }

    @Test
    void testStarteSpiel() {
        Long id = 100L;
        raum1.beitreten(s1);
        ReflectionTestUtils.setField(raum1, "id", id);
        Turnier turnier = mock(Turnier.class);
        Partie partie   = mock(Partie.class);
        when(turnier.getAktuellePartie()).thenReturn(partie);
        when(raum1.getTurnier()).thenReturn(turnier);
        Karte trumpf = mock(Karte.class);
        when(raum1.getTurnier().getAktuellePartie().entscheideRegelTrumpf())
                .thenReturn(trumpf);
        when(raum1.getTurnier().getAktuellePartie().getHandVonSpieler(anyString()))
                .thenReturn(List.of());                                     // leere Hand
        when(spielraumRepository.findWithSpielerById(id))
                .thenReturn(Optional.of(raum1));
        spielraumService.starteSpiel(id);
        verify(raum1).starteSpiel();
        verify(messaging, atLeastOnce()).convertAndSend(topicCaptor.capture(), payloadCaptor.capture());
        assertTrue(topicCaptor.getAllValues().contains("/topic/spielraumstatus/" + id));
        assertTrue(topicCaptor.getAllValues().contains("/topic/spielraumtrumpf/" + id));
    }
    @Test
    void testZieheTrumpfkarte() {
        Long id = 200L;
        raum1.setTurnier(turnier);
        raum1.beitreten(s1);
        raum1.getTurnier().setAktuellePartie(partie);
        when(raum1.getId()).thenReturn(id);
        Karte trumpf = mock(Karte.class);
        when(raum1.getTurnier().getAktuellePartie().entscheideRegelTrumpf()).thenReturn(trumpf);
        when(raum1.getTurnier().getAktuellePartie().getHandVonSpieler(anyString()))
                .thenReturn(List.of());
        ReflectionTestUtils.invokeMethod(spielraumService, "zieheTrumpfkarte", id, raum1);
        verify(messaging).convertAndSend(eq("/topic/spielraumtrumpf/" + id), any(Object.class));
    }

    @Test
    void testVerteileKarten() {
        Long id = 133L;
        ReflectionTestUtils.setField(raum1, "id", id);
        raum1.setTurnier(turnier);
        raum1.beitreten(s1);
        raum1.beitreten(s2);
        raum1.getTurnier().setAktuellePartie(partie);
        when(partie.getHandVonSpieler("Alice"))
                .thenReturn(List.of());
        ReflectionTestUtils.invokeMethod(spielraumService, "verteileKarten", id, raum1);
        verify(messaging,times(2))
                .convertAndSend(eq("/topic/spielraumausteilen/" + id), any(Object.class));
    }

    @Test
    void testVerarbeiteGedrueckteKarte() {
        Long id = 134L;
        ReflectionTestUtils.setField(raum1, "id", id);
        raum1.setTurnier(turnier);
        raum1.getTurnier().setAktuellePartie(partie);
        raum1.beitreten(s1);
        raum1.beitreten(s2);
        Regel regel = mock(Regel.class);
        //when(partie.getRegel()).thenReturn(regel);
        ReflectionTestUtils.setField(partie, "regel", regel);
        when(partie.alleGedrueckt()).thenReturn(true);
        when(partie.getAktuelleRunde()).thenReturn(mock(Runde.class));
        when(partie.getAktuelleRunde().getAktuellerSpieler()).thenReturn(s1);
        ConcurrentHashMap<Long, Spielraum> map = new ConcurrentHashMap<>();
        map.put(id, raum1);
        ReflectionTestUtils.setField(spielraumService, "aktiveRaeume", map);
        spielraumService.verarbeiteGedrueckteKarte(id,"Alice",mock(Karte.class));
        verify(messaging).convertAndSend(eq("/topic/spielraumstatus/" + id), any(Object.class));
    }

    @Test
    void testVerarbeiteSpielzug() {
        Long id = 135L;
        ReflectionTestUtils.setField(raum1, "id", id);
        raum1.setTurnier(turnier);
        raum1.beitreten(s1);
        raum1.beitreten(s2);
        raum1.getTurnier().setAktuellePartie(partie);
        Runde runde = mock(Runde.class);
        when(partie.getAktuelleRunde()).thenReturn(runde);
        when(runde.getNaechsterSpieler()).thenReturn(s2);
        StichStapel stapel = mock(StichStapel.class);
        Regel regel = mock(Regel.class);
        //when(partie.getRegel()).thenReturn(new Regel("Herz","Normal"));
        ReflectionTestUtils.setField(partie, "regel", regel);
        ReflectionTestUtils.setField(runde, "stichstapel", stapel);
        when(stapel.istVoll()).thenReturn(false);
        ConcurrentHashMap<Long, Spielraum> map = new ConcurrentHashMap<>();
        map.put(id, raum1);
        ReflectionTestUtils.setField(spielraumService, "aktiveRaeume", map);
        assertDoesNotThrow(() -> spielraumService.verarbeiteSpielzug(id,"Alice",mock(Karte.class)));
    }

    @Test
    void testVerarbeiteSpielzugStapelVoll() {
        Long id = 135L;
        ReflectionTestUtils.setField(raum1, "id", id);
        raum1.beitreten(s1);
        raum1.beitreten(s2);
        raum1.beitreten(s3);
        raum1.setTurnier(turnier2);
        raum1.getTurnier().setAktuellePartie(partie);
        Runde runde = mock(Runde.class);
        when(runde.getStichGewinner()).thenReturn(s1);
        when(partie.getAktuelleRunde()).thenReturn(runde);
        StichStapel stapel = mock(StichStapel.class);
        List<Spieler> spieler = new ArrayList<>();
        spieler.add(s1);
        spieler.add(s2);
        spieler.add(s3);

        SpielStand stand = new SpielStand(spieler);
        Map<Spieler, Integer> punkte = new HashMap<>();
        punkte.put(s1, 0);
        punkte.put(s2, 0);
        punkte.put(s3, 0);
        ReflectionTestUtils.setField(stand, "punkteliste", punkte);
        ReflectionTestUtils.setField(partie, "spielstand",stand);
        ReflectionTestUtils.setField(runde, "stichstapel", stapel);
        ReflectionTestUtils.setField(stapel, "gespielteKarten", new LinkedHashMap<Spieler,Karte>());
        when(stapel.istVoll()).thenReturn(true);
        Regel regel = mock(Regel.class);
        when(partie.getRegel()).thenReturn(regel);
        ConcurrentHashMap<Long, Spielraum> map = new ConcurrentHashMap<>();
        map.put(id, raum1);
        ReflectionTestUtils.setField(spielraumService, "aktiveRaeume", map);
        assertDoesNotThrow(() -> spielraumService.verarbeiteSpielzug(id,"Alice",mock(Karte.class)));
    }


    @Test
    void testVerarbeiteNeuePartie() {
        Long id = 136L;
        ReflectionTestUtils.setField(raum1, "id", id);
        raum1.setTurnier(turnier);
        raum1.getTurnier().setAktuellePartie(partie);
        Karte trumpf = mock(Karte.class);
        when(raum1.getTurnier().getAktuellePartie().entscheideRegelTrumpf()).thenReturn(trumpf);
        ConcurrentHashMap<Long, Spielraum> map = new ConcurrentHashMap<>();
        map.put(id, raum1);
        ReflectionTestUtils.setField(spielraumService, "aktiveRaeume", map);
        ReflectionTestUtils.invokeMethod(spielraumService, "verarbeiteNeuePartie", id);
        verify(messaging).convertAndSend(eq("/topic/spielraumtrumpf/" + id), any(Object.class));
    }

    @Test
    void testVerarbeiteNeuePartieMitNeuerPartie() {
        Long id = 136L;
        ReflectionTestUtils.setField(raum1, "id", id);
        raum1.setTurnier(turnier);
        raum1.beitreten(s1);
        raum1.beitreten(s2);
        ReflectionTestUtils.setField(turnier,"istAbgeschlossen", true);
        ConcurrentHashMap<Long, Spielraum> map = new ConcurrentHashMap<>();
        map.put(id, raum1);
        ReflectionTestUtils.setField(spielraumService, "aktiveRaeume", map);
        ReflectionTestUtils.setField(turnier, "gewinner", s1);
        ReflectionTestUtils.invokeMethod(spielraumService, "verarbeiteNeuePartie", id);
        verify(messaging).convertAndSend(eq("/topic/spielraumstatus/" + id), any(Object.class));
    }

    @Test
    void testAddAlleRaeume() {
        List<Spielraum> l = List.of(raum1,raum2);
        spielraumService.addAlleRaeume(l);
        List<Spielraum> liste = (List<Spielraum>) ReflectionTestUtils.getField(spielraumService, "spielraumListe");
        assertNotNull(liste);
        assertEquals(2, liste.size());
        assertTrue(liste.contains(raum1));
        assertTrue(liste.contains(raum2));
    }

    @Test
    void testBeitretenBotEinfach() {
        Long raumId = 136L;
        when(spielraumRepository.findById(raumId)).thenReturn(Optional.of(raum1));
        boolean result = spielraumService.beitretenBotEinfach(136L,bot1);
        assertTrue(result);
        assertTrue(raum1.getSpieler().contains(bot1));
        verify(spielraumRepository).save(raum1);
        verify(messaging).convertAndSend(eq("/topic/imspielraum/" + raumId), any(SpielraumDTO.class));
    }

    @Test
    void testBeitretenBotSchwer() {
        Long raumId = 136L;
        when(spielraumRepository.findById(raumId)).thenReturn(Optional.of(raum1));
        boolean result = spielraumService.beitretenBotSchwer(136L,bot2);
        assertTrue(result);
        assertTrue(raum1.getSpieler().contains(bot2));
        verify(spielraumRepository).save(raum1);
        verify(messaging).convertAndSend(eq("/topic/imspielraum/" + raumId), any(SpielraumDTO.class));
    }

    @Test
    void botDruecker() {
        Long raumId = 3L;
        bot1.setNickname("bot1");
        ArrayList<Karte> l = new ArrayList<>();
        l.add(new Karte("Herz","7"));
        l.add(new Karte("Rabe","9"));
        bot1.getSpielerHand().addToHand(l);
        raum1.beitreten(bot1);
        raum1.setTurnier(turnier);
        turnier.setAktuellePartie(partie);
        when(partie.getRegel()).thenReturn(new Regel("Herz","Normal"));

        ConcurrentHashMap<Long, Spielraum> map = new ConcurrentHashMap<>();
        map.put(raumId, raum1);
        ReflectionTestUtils.setField(spielraumService, "aktiveRaeume", map);
        SpielraumService spy = Mockito.spy(spielraumService);
        doNothing().when(spy).verarbeiteGedrueckteKarte(eq(raumId),eq(bot1.getNickname()), any(Karte.class));
        spy.botDruecker(raumId,raum1);
        verify(spy,atLeastOnce()).verarbeiteGedrueckteKarte(eq(raumId),eq("bot1"),any(Karte.class));
    }

    @Test
    void testVerarbeiteGedrueckteKarteBot() {
        Long id = 134L;
        ReflectionTestUtils.setField(raum1, "id", id);
        raum1.setTurnier(turnier);
        raum1.getTurnier().setAktuellePartie(partie);
        raum1.beitreten(s1);
        raum1.beitreten(bot1);

        Runde runde = mock(Runde.class);
        StichStapel stapel = mock(StichStapel.class);
        ReflectionTestUtils.setField(runde, "stichstapel", stapel);

        Regel regel = mock(Regel.class);
        when(partie.getRegel()).thenReturn(regel);
        ReflectionTestUtils.setField(partie, "regel", regel);

        when(partie.alleGedrueckt()).thenReturn(true);
        when(partie.getAktuelleRunde()).thenReturn(runde);
        when(runde.getAktuellerSpieler()).thenReturn(bot1);
        when(runde.getNaechsterSpieler()).thenReturn(s1);

        ConcurrentHashMap<Long, Spielraum> map = new ConcurrentHashMap<>();
        map.put(id, raum1);
        ReflectionTestUtils.setField(spielraumService, "aktiveRaeume", map);

        List<Karte> karten = List.of(new Karte("Herz","10"),new Karte("Herz","8"));
        bot1.getSpielerHand().addToHand(karten);
        when(regel.istGueltigeKarte(any(), any(), any())).thenReturn(true);

        spielraumService.verarbeiteGedrueckteKarte(id, bot1.getNickname(), mock(Karte.class));

        verify(messaging).convertAndSend(eq("/topic/spielraumstatus/" + id), any(Object.class));
    }

    @Test
    void testVerarbeiteSpielzugBot() {
        Long id = 135L;
        ReflectionTestUtils.setField(raum1, "id", id);
        raum1.setTurnier(turnier);
        raum1.beitreten(s1);
        raum1.beitreten(bot1);
        raum1.getTurnier().setAktuellePartie(partie);
        Runde runde = mock(Runde.class);
        when(partie.getAktuelleRunde()).thenReturn(runde);
        Regel regel = mock(Regel.class);
        //when(partie.getRegel()).thenReturn(new Regel("Herz","Normal"));
        ReflectionTestUtils.setField(partie, "regel", regel);
        when(runde.getNaechsterSpieler()).thenReturn(s1);
        StichStapel stapel = mock(StichStapel.class);
        ReflectionTestUtils.setField(runde, "stichstapel", stapel);
        when(stapel.istVoll()).thenReturn(false);
        ConcurrentHashMap<Long, Spielraum> map = new ConcurrentHashMap<>();
        map.put(id, raum1);

        ReflectionTestUtils.setField(spielraumService, "aktiveRaeume", map);
        assertDoesNotThrow(() -> spielraumService.verarbeiteSpielzug(id, bot1.getNickname(), mock(Karte.class)));
    }

    @Test
    void testVerarbeiteSpielzugBotStichstapelVoll() {
        Long id = 135L;
        ReflectionTestUtils.setField(raum1, "id", id);
        raum1.setTurnier(turnier);
        raum1.beitreten(s1);
        raum1.beitreten(bot1);
        raum1.beitreten(s2);
        List<Karte> karten = List.of(new Karte("Herz","10"),new Karte("Herz","8"));
        bot1.getSpielerHand().addToHand(karten);
        raum1.getTurnier().setAktuellePartie(partie);
        Runde runde = mock(Runde.class);

        when(runde.getStichGewinner()).thenReturn(s1);
        when(partie.getAktuelleRunde()).thenReturn(runde);
        StichStapel stapel = mock(StichStapel.class);
        List<Spieler> spieler = new ArrayList<>();
        spieler.add(s1);
        spieler.add(s2);
        spieler.add(bot1);

        SpielStand stand = new SpielStand(spieler);
        Map<Spieler, Integer> punkte = new HashMap<>();
        punkte.put(s1, 0);
        punkte.put(s2, 0);
        punkte.put(bot1, 0);
        ReflectionTestUtils.setField(stand, "punkteliste", punkte);
        ReflectionTestUtils.setField(partie, "spielstand",stand);
        ReflectionTestUtils.setField(runde, "stichstapel", stapel);
        ReflectionTestUtils.setField(stapel, "gespielteKarten", new LinkedHashMap<Spieler,Karte>());
        Regel regel = mock(Regel.class);
        when(partie.getRegel()).thenReturn(regel);

        when(stapel.istVoll()).thenReturn(true);
        ConcurrentHashMap<Long, Spielraum> map = new ConcurrentHashMap<>();
        map.put(id, raum1);
        ReflectionTestUtils.setField(spielraumService, "aktiveRaeume", map);
        assertDoesNotThrow(() -> spielraumService.verarbeiteSpielzug(id, bot1.getNickname(), mock(Karte.class)));
    }
}

