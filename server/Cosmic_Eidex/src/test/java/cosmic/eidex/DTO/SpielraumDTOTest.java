package cosmic.eidex.DTO;

import cosmic.eidex.spielmodell.Spieler;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SpielraumDTOTest {

    @Test
    void testDefaultConstructor() {
        SpielraumDTO dto = new SpielraumDTO();
        assertNull(dto.getId());
        assertNull(dto.getName());
        assertNull(dto.getSpieler());
    }

    @Test
    void testConstructorAndGetters() {
        Spieler s1 = new Spieler("Anna", "pw");
        Spieler s2 = new Spieler("Tom", "pw");
        List<Spieler> spielerList = new ArrayList<>();
        spielerList.add(s1);
        spielerList.add(s2);

        SpielraumDTO dto = new SpielraumDTO(1L, "Raum A", spielerList, new HashMap<>());

        assertEquals(1L, dto.getId());
        assertEquals("Raum A", dto.getName());
        assertEquals(2, dto.getSpieler().size());
        assertEquals("Anna", dto.getSpieler().get(0).getNickname());
    }

    @Test
    void testConstructorWithNull() {
        Spieler s1 = new Spieler("Anna", "pw");
        Spieler s2 = new Spieler("Tom", "pw");
        List<Spieler> spielerList = new ArrayList<>();
        spielerList.add(s1);
        spielerList.add(s2);

        SpielraumDTO dto = new SpielraumDTO(1L, "Raum A", spielerList, null);
        assertEquals(1L, dto.getId());
        assertEquals("Raum A", dto.getName());
        assertNotNull(dto.getPlatzMap());
        assertTrue(dto.getPlatzMap().isEmpty());


    }

    @Test
    void testSetters() {
        SpielraumDTO dto = new SpielraumDTO();
        dto.setId(2L);
        dto.setName("Raum B");

        Spieler s1 = new Spieler("Lisa", "pw");
        List<Spieler> list = List.of(s1);
        dto.setSpieler(list);

        assertEquals(2L, dto.getId());
        assertEquals("Raum B", dto.getName());
        assertEquals(1, dto.getSpieler().size());
    }
}
