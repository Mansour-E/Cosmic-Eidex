package cosmic.eidex.Service;

import cosmic.eidex.repository.SpielerRepository;
import cosmic.eidex.spielmodell.Spieler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginManagerTest {

    private SpielerRepository spielerRepository;
    private LoginManager loginManager;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        spielerRepository = mock(SpielerRepository.class);
        loginManager = new LoginManager(spielerRepository);
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void testRegistrierenNeuerSpieler() {
        String nickname = "neuerSpieler";
        when(spielerRepository.findByNickname(nickname)).thenReturn(Optional.empty());

        Spieler gespeicherterSpieler = new Spieler(nickname, "pw", 20);
        when(spielerRepository.save(any(Spieler.class))).thenReturn(gespeicherterSpieler);

        Spieler result = loginManager.registrieren(nickname, "pw", 20);

        assertNotNull(result);
        assertEquals(nickname, result.getNickname());
        verify(spielerRepository).save(any(Spieler.class));
    }

    @Test
    void testRegistrierenBereitsVorhanden() {
        when(spielerRepository.findByNickname("name")).thenReturn(Optional.of(new Spieler()));

        Spieler result = loginManager.registrieren("name", "pw", 25);

        assertNull(result);
        verify(spielerRepository, never()).save(any());
    }

    @Test
    void testAnmeldenErfolgreich() {
        Spieler s = new Spieler("nick", "pw", 30);
        when(spielerRepository.findByNickname("nick")).thenReturn(Optional.of(s));

        String token = loginManager.anmelden("nick", "pw");

        assertNotNull(token);
        assertEquals(s, loginManager.getSpielerZuToken(token));
        assertEquals(token, s.getToken());
    }

    @Test
    void testAnmeldenFehlgeschlagen() {
        when(spielerRepository.findByNickname("nick")).thenReturn(Optional.empty());

        String token = loginManager.anmelden("nick", "pw");

        assertNull(token);
    }

    @Test
    void testDoppeltesAnmelden() {
        Spieler s = new Spieler("nick", "pw", 30);
        when(spielerRepository.findByNickname("nick")).thenReturn(Optional.of(s));
        //1. Anmeldung
        String token1 = loginManager.anmelden("nick", "pw");
        assertNotNull(token1);
        //2. Anmeldung obwohl schon angemeldet
        String token2 = loginManager.anmelden("nick", "pw");
        assertNull(token2);
    }

    @Test
    void testAnmeldenFalschesPasswort() {
        Spieler s = new Spieler("nick", "pw", 30);
        when(spielerRepository.findByNickname("nick")).thenReturn(Optional.of(s));
        String token1 = loginManager.anmelden("nick", "falsch");
        assertNull(token1);
    }

    @Test
    void testAbmeldenEntferntToken() {
        Spieler s = new Spieler("nick", "pw", 30);
        s.setToken("token123");

        when(spielerRepository.findByNickname("nick")).thenReturn(Optional.of(s));

        String token = loginManager.anmelden("nick", "pw");

        loginManager.abmelden(token);

        assertNull(s.getToken());
        assertNull(loginManager.getSpielerZuToken(token));
    }

    @Test
    void testKontoLoeschenErfolgreich() {
        Spieler s = new Spieler("nick", "pw", 30);
        when(spielerRepository.findByNickname("nick")).thenReturn(Optional.of(s));

        boolean geloescht = loginManager.kontoLoeschen("nick", "pw");

        assertTrue(geloescht);
        verify(spielerRepository).delete(s);
    }

    @Test
    void testKontoLoeschenFalschesPasswort() {
        Spieler s = new Spieler("nick", "pw", 30);
        when(spielerRepository.findByNickname("nick")).thenReturn(Optional.of(s));

        boolean geloescht = loginManager.kontoLoeschen("nick", "falsch");

        assertFalse(geloescht);
        verify(spielerRepository, never()).delete(any());
    }

    @Test
    void testKontoLoeschenSpielerNichtGefunden() {
        when(spielerRepository.findByNickname("x")).thenReturn(Optional.empty());
        boolean x = loginManager.kontoLoeschen("x","pw");
        assertFalse(x);
        verify(spielerRepository, never()).delete(any());
    }


    @Test
    void testAenderNicknameErfolgreich() {
        Spieler s = new Spieler("alt", "pw", 22);
        when(spielerRepository.findByNickname("alt")).thenReturn(Optional.of(s));

        String token = loginManager.anmelden("alt", "pw");

        assertNotNull(token); // Sicherheitscheck

        boolean result = loginManager.aenderNickName(token, "alt", "neu");

        assertTrue(result);
        verify(spielerRepository).save(s);
        assertEquals("neu", s.getNickname());
    }

    @Test
    void testAendernicknameTokenUnbekannt() {
        boolean x = loginManager.aenderNickName("tokenABC","alt","neu");
        assertFalse(x);
    }

    @Test
    void testAenderPasswortErfolgreich() {
        Spieler s = new Spieler("nick", "alt", 22);
        when(spielerRepository.findByNickname("nick")).thenReturn(Optional.of(s));
        String token = loginManager.anmelden("nick", "alt");

        boolean result = loginManager.aenderPasswort(token, "nick", "neu", "alt");

        assertTrue(result);
        verify(spielerRepository).save(s);
        assertEquals("neu", s.getPasswort());
    }

    @Test
    void testAenderPasswortFalsch() {
        Spieler s = new Spieler("nick", "alt", 22);
        when(spielerRepository.findByNickname("nick")).thenReturn(Optional.of(s));
        //1. Gueltiges Token aber falsches altes Passwort
        String token = loginManager.anmelden("nick", "alt");
        boolean result1 = loginManager.aenderPasswort(token, "nick", "neu", "falsch");
        assertFalse(result1);
        //2. Unbekanntes Token
        boolean result2 = loginManager.aenderPasswort("falsch", "nick", "neu", "alt");
        assertFalse(result2);
    }

    @Test
    void testIstTokenGueltig() {
        Spieler s = new Spieler("x", "pw", 20);
        when(spielerRepository.findByNickname("x")).thenReturn(Optional.of(s));

        String token = loginManager.anmelden("x", "pw");

        assertTrue(loginManager.istTokenGueltig(token));
        loginManager.abmelden(token);
        assertFalse(loginManager.istTokenGueltig(token));
    }

    @Test
    void testFindPlayer() {
        when(spielerRepository.findByNickname("gibtEs")).thenReturn(Optional.of(new Spieler()));
        when(spielerRepository.findByNickname("nicht")).thenReturn(Optional.empty());

        assertTrue(loginManager.findPlayer("gibtEs"));
        assertFalse(loginManager.findPlayer("nicht"));
    }

    @Test
    void testPrintAlleAktivenLeer() {
        loginManager.printAlleAktiven();
        String console = outContent.toString().trim();
        assertEquals("Keine aktiven Sessions.",console);
    }

    @Test
    void testPrintAlleAktiven() {
        Spieler s = new Spieler("nick", "pw", 20);
        when(spielerRepository.findByNickname("nick")).thenReturn(Optional.of(s));
        String token = loginManager.anmelden("nick", "pw");
        outContent.reset();
        loginManager.printAlleAktiven();
        String console = outContent.toString().trim();
        assertTrue(console.contains("Aktive Sessions:"));
        assertTrue(console.contains("Token: "));
        assertTrue(console.contains("Spieler: nick"));
    }
}

