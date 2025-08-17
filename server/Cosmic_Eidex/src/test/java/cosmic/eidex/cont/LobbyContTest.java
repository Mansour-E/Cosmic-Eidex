package cosmic.eidex.cont;

import cosmic.eidex.DTO.LobbyDTO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LobbyContTest {

    private LobbyCont controller;

    @BeforeEach
    void setUp() {
        controller = new LobbyCont();
    }

    @Test
    void testGetLobbyDaten_Leer() {
        LobbyDTO dto = controller.getLobbyDaten();
        assertNotNull(dto);
        assertTrue(dto.getSpielraeume().isEmpty());
        assertTrue(dto.getSpieler().isEmpty());
    }

    @Test
    void testGetLobbyDaten_MitSpielraeumen() {
        // Neuen Raum über den Controller erzeugen
        controller.erzeugeNeuenRaum("TestRaum");

        LobbyDTO dto = controller.getLobbyDaten();

        assertNotNull(dto);
        assertEquals(1, dto.getSpielraeume().size());
        assertEquals("TestRaum", dto.getSpielraeume().get(0).getName());
    }

    @Test
    void testErzeugeNeuenRaum_Doppelt() {
        controller.erzeugeNeuenRaum("Raum1");
        controller.erzeugeNeuenRaum("Raum1");  // sollte ignoriert werden

        LobbyDTO dto = controller.getLobbyDaten();
        assertEquals(1, dto.getSpielraeume().size());
    }
}
