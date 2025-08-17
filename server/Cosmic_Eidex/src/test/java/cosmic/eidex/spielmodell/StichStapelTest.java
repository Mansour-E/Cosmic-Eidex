package cosmic.eidex.spielmodell;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class StichStapelTest {

    Spieler spieler1 = new Spieler("s1", "1");
    Spieler spieler2 = new Spieler("s2", "2");
    Spieler spieler3 = new Spieler("s3", "3");



    @Test
    public void testAddAndRetrieveKarte() {
        StichStapel stapel = new StichStapel();
        Karte karte = new Karte("Rabe", "10");
        stapel.addGespielteKarte(spieler1, karte);

        assertEquals(karte, stapel.getStichKarte(spieler1));
        assertEquals(1, stapel.getGespielteKarten().size());
    }

    @Test
    public void testGespielteKartenOrder() {
        StichStapel stapel = new StichStapel();
        stapel.addGespielteKarte(spieler1, new Karte("Stern", "Ass"));
        stapel.addGespielteKarte(spieler2, new Karte("Herz", "König"));

        assertEquals(2, stapel.getGespielteKarten().size());
        assertTrue(stapel.getGespielteKarten().containsKey(spieler1));
        assertTrue(stapel.getGespielteKarten().containsKey(spieler2));
    }

    @Test
    public void testIstVoll() {
        StichStapel stapel = new StichStapel();
        stapel.addGespielteKarte(spieler1, new Karte("Stern", "Ass"));
        stapel.addGespielteKarte(spieler2, new Karte("Herz", "König"));
        assertEquals(2, stapel.getGespielteKarten().size());
        assertFalse(stapel.istVoll());
        stapel.addGespielteKarte(spieler3, new Karte("Stern","7"));
        assertTrue(stapel.istVoll());
    }
}
