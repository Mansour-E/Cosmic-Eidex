package cosmic.eidex.spielmodell;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class KarteTest {

    Karte karte;

    @BeforeEach
    void setUp() {
        karte = new Karte("Rabe", "Koenig");
    }

    @Test
    void SetUndGetFarbe() {
        karte.setFarbe("Stern");
        assertEquals("Stern", karte.getFarbe());
    }

    @Test
    void SetUndGetWert() {
        karte.setWert("10");
        assertEquals("10", karte.getWert());
    }

    @Test
    void getFarbeList() {
        assertEquals(List.of("Herz", "Eidex", "Rabe", "Stern"), Karte.getFarbeList());
    }

    @Test
    void getWertList() {
        assertEquals(List.of("6", "7", "8", "9", "10", "Bube", "Dame", "Koenig", "Ass"), Karte.getWertList());
    }
}