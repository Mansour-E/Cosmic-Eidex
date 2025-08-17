package cosmic.eidex.cont;

import cosmic.eidex.DTO.SpielerDTO;
import cosmic.eidex.Service.LoginManager;
import cosmic.eidex.spielmodell.Spieler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginManagerContTest {

    @InjectMocks
    private LoginManagerCont controller;

    @Mock
    private LoginManager loginManager;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegister() {
        Spieler s = new Spieler("Max", "pw", 18);
        when(loginManager.registrieren("Max", "pw", 18)).thenReturn(s);

        Spieler result = controller.register("Max", "pw", 18);

        assertEquals("Max", result.getNickname());
        verify(loginManager).registrieren("Max", "pw", 18);
    }

    @Test
    void testLoginErfolgreich() {
        Spieler s = new Spieler("Max", "pw", 18);
        when(loginManager.anmelden("Max", "pw")).thenReturn("token123");
        when(loginManager.getSpielerZuToken("token123")).thenReturn(s);

        ResponseEntity<?> response = controller.login("Max", "pw");

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getHeaders().getFirst("Authorization").contains("Bearer token123"));
        SpielerDTO sDTO = new SpielerDTO(s);
        assertEquals(sDTO, response.getBody());
    }

    @Test
    void testLoginFehlgeschlagen() {
        when(loginManager.anmelden("Max", "pw")).thenReturn(null);

        ResponseEntity<?> response = controller.login("Max", "pw");

        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Login fehlgeschlagen", response.getBody());
    }

    @Test
    void testLogoutMitGueltigemToken() {
        ResponseEntity<?> response = controller.logout("Bearer token123");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Logout erfolgreich", response.getBody());
        verify(loginManager).abmelden("token123");
    }

    @Test
    void testLogoutOhneToken() {
        ResponseEntity<?> response = controller.logout(null);

        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Kein Token", response.getBody());
    }

    @Test
    void testDeleteErfolgreich() {
        Spieler s = new Spieler("Max", "pw", 18);
        s.setToken("token123");

        when(loginManager.getSpielerZuToken("token123")).thenReturn(s);
        when(loginManager.kontoLoeschen("Max", "pw")).thenReturn(true);

        ResponseEntity<?> response = controller.delete("Bearer token123", "Max", "pw");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Konto gelöscht", response.getBody());
    }

    @Test
    void testDeleteFehlgeschlagen1() {
        Spieler s = new Spieler("Max", "pw", 18);
        s.setToken("token123");
        when(loginManager.getSpielerZuToken("token123")).thenReturn(s);

        ResponseEntity<?> response = controller.delete("Bearer token123", "falsch", "pw");
        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Token ungültig", response.getBody());

    }

    @Test
    void testDeleteFehlgeschlagen2() {
        Spieler s = new Spieler("Max", "pw", 18);
        s.setToken("token123");
        when(loginManager.getSpielerZuToken("token123")).thenReturn(s);
        when(loginManager.kontoLoeschen("Max", "pw")).thenReturn(false);
        ResponseEntity<?> response = controller.delete("Bearer token123", "Max", "pw");
        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Löschen fehlgeschlagen", response.getBody());

    }

    @Test
    void testChangeNicknameErfolgreich() {
        Spieler s = new Spieler("Max", "pw", 18);
        s.setToken("token123");
        when(loginManager.getSpielerZuToken("token123")).thenReturn(s);
        when(loginManager.aenderNickName("token123", "Max", "NeuerName")).thenReturn(true);

        ResponseEntity<?> response = controller.changeNickname("Bearer token123", "Max", "NeuerName");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Erfolgreich", response.getBody());
    }

    @Test
    void testChangeNicknameFehlgeschlagen1() {
        ResponseEntity<?> response = controller.changeNickname("token123ohneBearer", "Max", "NeuerName");
        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Token ungültig", response.getBody());

    }

    @Test
    void testChangeNicknameFehlgeschlagen2() {
        Spieler s = new Spieler("Max", "pw", 18);
        s.setToken("token123");
        when(loginManager.getSpielerZuToken("token123")).thenReturn(s);
        when(loginManager.aenderNickName("token123", "Max", "NeuerName")).thenReturn(false);
        ResponseEntity<?> response = controller.changeNickname("Bearer token123", "Max", "NeuerName");
        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Aendern fehlgeschlagen", response.getBody());
    }

    @Test
    void testChangePasswordErfolgreich() {
        Spieler s = new Spieler("Max", "pw", 18);
        s.setToken("token123");
        when(loginManager.getSpielerZuToken("token123")).thenReturn(s);
        when(loginManager.aenderNickName("token123", "Max", "NeuerName")).thenReturn(true);
        ResponseEntity<?> response = controller.changeNickname("Bearer token123", "Max", "NeuerName");
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Erfolgreich", response.getBody());
    }

    @Test
    void testChangePasswordFehlgeschlagen1() {
        ResponseEntity<?> response = controller.changePassword("token123ohneBearer", "Max", "NeuerName");
        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Token ungültig", response.getBody());
    }

    @Test
    void testChangePasswordFehlgeschlagen2() {
        Spieler s = new Spieler("Max", "pw", 18);
        s.setToken("token123");
        when(loginManager.getSpielerZuToken("token123")).thenReturn(s);
        when(loginManager.aenderPasswort("token123", "Max", "neu", "falsch")).thenReturn(false);

        ResponseEntity<?> response = controller.changePassword("Bearer token123", "neu", "falsch");

        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Aendern fehlgeschlagen", response.getBody());
    }

    @Test
    void testFindExisting() {
        when(loginManager.findPlayer("Max")).thenReturn(true);

        Boolean result = controller.findExisting("Max");

        assertTrue(result);
        verify(loginManager).findPlayer("Max");
    }
}
