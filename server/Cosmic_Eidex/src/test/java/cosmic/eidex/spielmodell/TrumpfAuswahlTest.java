package cosmic.eidex.spielmodell;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class TrumpfAuswahlTest {
    TrumpfAuswahl trumpfAuswahl;
    Regel regel;
    Karte karte;
    @BeforeEach
    void setUp() {
        karte = mock(Karte.class);
        regel = mock(Regel.class);
        trumpfAuswahl = new TrumpfAuswahl(regel,karte);
    }

    @Test
    void testGetRegel() {
        assertEquals(regel,trumpfAuswahl.getRegel());
    }

    @Test
    void testGetTrumpfkarte() {
        assertEquals(karte,trumpfAuswahl.getTrumpfkarte());
    }
}