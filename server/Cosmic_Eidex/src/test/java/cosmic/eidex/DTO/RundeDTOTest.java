package cosmic.eidex.DTO;

import cosmic.eidex.spielmodell.Karte;
import cosmic.eidex.spielmodell.Regel;
import cosmic.eidex.spielmodell.Spieler;
import cosmic.eidex.spielmodell.StichStapel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.*;

class RundeDTOTest {
    private RundeDTO dto;
    private StichStapel stapel;
    private StichStapel stapel2;
    private Regel regel;
    private Regel regel2;
    private LinkedHashMap<Spieler, Karte> map;


    @BeforeEach
    void setUp() {
        regel = new Regel("","Obenabe");
        regel2 = new Regel("Stern","Normal");
        Spieler alice = new Spieler("Alice", "123");
        stapel = new StichStapel();
        stapel2 = new StichStapel(map);
        map = new LinkedHashMap<Spieler, Karte>();
        Karte karte = new Karte("Stern", "Bube");
        map.put(alice, karte);
        dto = new RundeDTO(1L,regel,5,"Alice");
    }

    @Test
    void testGetAktuellerSpieler() {
        assertEquals("Alice",dto.getAktuellerSpieler());
    }

    @Test
    void testSetUndGetRaumId() {
        assertEquals(1L, dto.getRaumid());
        dto.setRaumid(2L);
        assertEquals(2L, dto.getRaumid());
    }

    @Test
    void testSetUndGetRegel() {
        assertEquals(regel,dto.getRegel());
        dto.setRegel(regel2);
        assertEquals(regel2,dto.getRegel());
    }

    @Test
    void testSetUndGetRunde() {
        assertEquals(5,dto.getRunde());
        dto.setRunde(11);
        assertEquals(11,dto.getRunde());
    }
}