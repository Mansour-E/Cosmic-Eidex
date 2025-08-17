package cosmic.eidex.Service;

import cosmic.eidex.Service.SessionManager;
import org.junit.jupiter.api.*;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SessionManagerTest {

    @BeforeEach
    void resetActiveSessions() throws Exception {
        // Reflection um das private static final Feld activeSessions zu leeren
        Field field = SessionManager.class.getDeclaredField("activeSessions");
        field.setAccessible(true);

        // da es final ist, müssen wir auch das entfernen (oder ein neues HashMap setzen)
        Map<String, String> emptyMap = new HashMap<>();
        field.set(null, emptyMap);
    }

    @Test
    void testSaveAndGetToken() {
        SessionManager.saveToken("user1", "token123");
        String token = SessionManager.getToken("user1");
        assertEquals("token123", token);
    }

    @Test
    void testRemoveToken() {
        SessionManager.saveToken("user1", "token123");
        SessionManager.removeToken("user1");
        assertNull(SessionManager.getToken("user1"));
    }

    @Test
    void testIsLoggedIn() {
        SessionManager.saveToken("user1", "token123");
        assertTrue(SessionManager.isLoggedIn("user1"));
        assertFalse(SessionManager.isLoggedIn("user2"));
    }

    @Test
    void testGetNickname() {
        SessionManager.saveToken("user1", "token123");
        SessionManager.saveToken("user2", "token456");

        String nickname = SessionManager.getNickname();
        assertTrue(nickname.equals("user1") || nickname.equals("user2"));
    }

    @Test
    void testPrintAllActiveSessions() {
        // Da printAllActiveSessions nur ausgibt, rufen wir sie auf um sicherzugehen, dass kein Fehler auftritt
        SessionManager.printAllActiveSessions();

        SessionManager.saveToken("user1", "token123");
        SessionManager.printAllActiveSessions();

        // Hier keine Assertion, nur Sicherstellen, dass keine Exceptions geworfen werden
    }
}

