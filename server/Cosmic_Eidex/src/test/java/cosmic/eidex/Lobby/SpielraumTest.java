package cosmic.eidex.Lobby;

import cosmic.eidex.Service.OffsetDateTimeAdapter;
import cosmic.eidex.Service.Spielstatus;
import cosmic.eidex.spielmodell.Partie;
import cosmic.eidex.spielmodell.Spieler;
import cosmic.eidex.spielmodell.Turnier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;


class SpielraumTest {

    Spieler Bob;
    Spieler Alice;
    Spieler Charlie;
    Spielraum room;


    //Testspieler
    private Spieler createMockSpieler(String name) {
        Spieler spieler = mock(Spieler.class, withSettings().useConstructor(name, "psswd"));
        when(spieler.getNickname()).thenReturn(name);
        return spieler;
    }

    @BeforeEach
    public void setUp() {
        Bob = createMockSpieler("Bob");
        Alice = createMockSpieler("Alice");
        Charlie = createMockSpieler("Charlie");
        room = new Spielraum();
    }

    @Test
    void testBeitreten() {
        room.beitreten(Alice);
        room.beitreten(Bob);
        assertEquals(room.getSpieler(), List.of(Alice, Bob));
    }

    @Test
    void testEntferneSpieler() {
        room.beitreten(Alice);
        room.entferneSpieler("Alice");
        assertEquals(room.getSpieler(), List.of());
    }

    @Test
    void testStarteSpiel() {
        room.beitreten(Alice);
        room.beitreten(Bob);
        room.beitreten(Charlie);
        assertEquals(room.getSpieler(), List.of(Alice, Bob, Charlie));
        room.starteSpiel();
        assertTrue(room.isStarted());
        assertEquals(room.getTurnier().getSpieler(), List.of(Alice, Bob, Charlie));
        assertEquals(room.getStatus(), Spielstatus.TRUMPF_WIRD_GEZOGEN);
    }

    @Test
    void tetsSetundGetStatus() {
        room.setStatus(Spielstatus.WARTET_AUF_SPIELZUEGE);
        assertEquals(room.getStatus(), Spielstatus.WARTET_AUF_SPIELZUEGE);
    }

    @Test
    void testSetundGetPasswort() {
        room.setPasswort("Alice");
        assertEquals(room.getPasswort(), "Alice");
    }

    @Test
    void testSetundGetName() {
        room.setName("Alice");
        assertEquals(room.getName(), "Alice");
    }

    @Test
    void testSetundGetBereitStatus() {
        room.beitreten(Alice);
        room.beitreten(Bob);
        room.beitreten(Charlie);
        assertEquals(room.getSpieler(), List.of(Alice, Bob, Charlie));
        room.setBereitStatus(Alice.getNickname(),true);
        assertTrue(room.isBereit("Alice"));
        assertFalse(room.isBereit("Bob"));
        room.setBereitStatus(Bob.getNickname(),true);
        room.setBereitStatus(Charlie.getNickname(), true);

    }

    @Test
    void testSetUndIsStarted() {
        room.setStarted(false);
        assertFalse(room.isStarted());
        room.setStarted(true);
        assertTrue(room.isStarted());
        room.setStarted(false);
        assertFalse(room.isStarted());
    }

    @Test
    void testSetUndGetPartie() {
        Partie partie = mock(Partie.class);
        room.setPartie(partie);
        assertEquals(room.getPartie(), partie);
    }

    @Test
    void testSetUndGetCreatedAt() {
        OffsetDateTime time = OffsetDateTime.now();
        room.setCreatedAt(time);
        assertEquals(room.getCreatedAt(), time);
    }

    @Test
    void testSetUndGetTurnier() {
        Turnier turnier = mock(Turnier.class);
        room.setTurnier(turnier);
        assertEquals(room.getTurnier(), turnier);
    }
}
