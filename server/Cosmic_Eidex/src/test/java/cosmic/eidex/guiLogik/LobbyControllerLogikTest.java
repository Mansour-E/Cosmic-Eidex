package cosmic.eidex.guiLogik;

import cosmic.eidex.DTO.SpielraumDTO;
import cosmic.eidex.gui.StageManager;
import cosmic.eidex.repository.SpielerRepository;
import cosmic.eidex.spielmodell.Spieler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LobbyControllerLogikTest {
    LobbyControllerLogik lobbyController;
    RestTemplate restTemplate;
    StageManager stageManager;
    SpielerRepository spielerRepository;

    @BeforeEach
    void setUp() {
        stageManager = mock(StageManager.class);
        restTemplate = mock(RestTemplate.class);
        lobbyController = new LobbyControllerLogik(stageManager);
        lobbyController.setRestTemplate(restTemplate);
    }

    @Test
    void getSpielerImRaumNamen() {
        Spieler s1 = new Spieler("Alice","pw");
        Spieler s2 = new Spieler("Bob","pw");
        SpielraumDTO dto = new SpielraumDTO();
        dto.setName("TestRaum");
        dto.setSpieler(Arrays.asList(s1,s2));
        SpielraumDTO[] response = new SpielraumDTO[]{dto};
        when(restTemplate.getForObject("http://localhost:8080/spielraum/alle", SpielraumDTO[].class))
                .thenReturn(response);

        List<String> spieler = lobbyController.getSpielerImRaum("TestRaum");
        assertNotNull(spieler);
        assertTrue(spieler.contains("Alice"));
        assertTrue(spieler.contains("Bob"));
        assertEquals(Arrays.asList("Alice","Bob"),spieler);
    }

    @Test
    void getSpielerImRaumLeereListe() {
        Spieler s1 = new Spieler("Alice","pw");
        Spieler s2 = new Spieler("Bob","pw");
        SpielraumDTO dto = new SpielraumDTO();
        dto.setName("TestRaum");
        dto.setSpieler(Arrays.asList(s1,s2));
        SpielraumDTO[] response = new SpielraumDTO[]{dto};
        when(restTemplate.getForObject("http://localhost:8080/spielraum/alle", SpielraumDTO[].class))
                .thenReturn(response);
        List<String> spieler = lobbyController.getSpielerImRaum("NichtVorhanden");
        assertTrue(spieler.isEmpty());
    }

    @Test
    void getSpielerImRaumLeereListeOhneAndereRaueme() {
        List<String> spieler = lobbyController.getSpielerImRaum("NichtVorhanden");
        assertTrue(spieler.isEmpty());
    }

    @Test
    void getRaumIdByName() {
        SpielraumDTO dto1 = new SpielraumDTO();
        dto1.setName("TestRaum1");
        dto1.setId(1L);
        SpielraumDTO dto2 = new SpielraumDTO();
        dto2.setName("TestRaum2");
        dto2.setId(2L);

        SpielraumDTO[] response = new SpielraumDTO[]{dto1,dto2};
        when(restTemplate.getForObject("http://localhost:8080/spielraum/alle", SpielraumDTO[].class))
                .thenReturn(response);
        Long id = lobbyController.getRaumIdByName("TestRaum1");
        assertNotNull(id);
        assertEquals(id,dto1.getId());
        assertEquals(1L,id);
    }
    @Test
    void getRaumIdByNameLeereListe() {
        SpielraumDTO dto1 = new SpielraumDTO();
        dto1.setName("TestRaum1");
        dto1.setId(1L);
        SpielraumDTO dto2 = new SpielraumDTO();
        dto2.setName("TestRaum2");
        dto2.setId(2L);

        SpielraumDTO[] response = new SpielraumDTO[]{dto1,dto2};
        when(restTemplate.getForObject("http://localhost:8080/spielraum/alle", SpielraumDTO[].class))
                .thenReturn(response);
        Long id = lobbyController.getRaumIdByName("TestRaum3");
        assertNull(id);
    }
}