package cosmic.eidex.Lobby;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class LobbyTest {

    private Lobby lobby;
    private Lobby lobby2;

    @BeforeEach
    void setUp() {
        lobby = new Lobby();
        lobby2 = new Lobby(new ArrayList<>(),null);
    }

    @Test
    void testErzeugeNeuenRaumErfolgreich() {
        lobby.erzeugeNeuenRaum("Raum1");
        assertEquals(1, lobby.spielraeume.size());
        assertEquals("Raum1", lobby.spielraeume.getFirst().getName());
    }

    @Test
    void testErzeugeNeuenSpielraumMitAnderemKonstruktor() {
        lobby2.erzeugeNeuenRaum("Raum1");
        assertEquals(1, lobby2.spielraeume.size());
        assertEquals("Raum1", lobby2.spielraeume.getFirst().getName());
    }

    @Test
    void testErzeugeNeuenRaum_DoppelterName() {
        lobby.erzeugeNeuenRaum("Raum1");
        int sizeBefore = lobby.spielraeume.size();

        // Versuch, nochmal gleichen Raum anzulegen
        lobby.erzeugeNeuenRaum("Raum1");

        // Größe darf sich nicht ändern
        assertEquals(sizeBefore, lobby.spielraeume.size());
    }

    @Test
    void testSpielraeumeAnzeigen() {
        lobby.erzeugeNeuenRaum("RaumA");
        lobby.erzeugeNeuenRaum("RaumB");

        var raeume = lobby.spielrauemeAnzeigen();

        assertEquals(2, raeume.size());
        assertTrue(raeume.stream().anyMatch(r -> r.getName().equals("RaumA")));
        assertTrue(raeume.stream().anyMatch(r -> r.getName().equals("RaumB")));
    }
}
