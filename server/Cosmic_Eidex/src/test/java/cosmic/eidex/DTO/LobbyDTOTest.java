package cosmic.eidex.DTO;

import cosmic.eidex.Lobby.Spielraum;
import cosmic.eidex.spielmodell.Spieler;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LobbyDTOTest {

    @Test
    void testLobbyDTOConstructorAndGetter() {
        Spieler spieler1 = new Spieler("user1", "pw1");
        Spieler spieler2 = new Spieler("user2", "pw2");

        Spielraum spielraum1 = new Spielraum("Raum 1");
        Spielraum spielraum2 = new Spielraum("Raum 2");

        LobbyDTO lobbyDTO = new LobbyDTO();
        lobbyDTO.getSpieler().add(spieler1);
        lobbyDTO.getSpieler().add(spieler2);
        lobbyDTO.getSpielraeume().add(spielraum1);
        lobbyDTO.getSpielraeume().add(spielraum2);

        assertEquals(2, lobbyDTO.getSpieler().size());
        assertEquals(2, lobbyDTO.getSpielraeume().size());

        assertEquals("user1", lobbyDTO.getSpieler().get(0).getNickname());
        assertEquals("Raum 1", lobbyDTO.getSpielraeume().get(0).getName());
    }
}
