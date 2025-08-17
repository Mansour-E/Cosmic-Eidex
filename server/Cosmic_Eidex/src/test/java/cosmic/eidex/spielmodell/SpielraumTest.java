package cosmic.eidex.spielmodell;

import org.junit.jupiter.api.Test;
import cosmic.eidex.Lobby.Spielraum;
import static org.junit.jupiter.api.Assertions.*;


public class SpielraumTest {
    @Test
    public void testBeitretenErfolgreich(){
        Spielraum raum = new Spielraum("TestRaum");
        Spieler s = new Spieler("Alice", "pw", 18);
        assertTrue(raum.beitreten(s));
        assertEquals(1, raum.getSpieler().size());

    }
    @Test
    public void testBeitretenZuVoll(){
        Spielraum raum = new Spielraum("Voll");
        for (int i = 0; i<3; i++){
            raum.beitreten(new Spieler("p"+i, "pw", 18 ));
            }
            boolean result = raum.beitreten(new Spieler("extra", "pw", 18));
        assertFalse(result);
    }

    //TODO: Muss angepasst werden
    /*
    @Test
    public void testErstelleSpielGibtJedemSpielerNeunKarten() {
        Spielraum raum = new Spielraum("Test");
        Spieler s1 = new Spieler("A", "pw", 18);
        Spieler s2 = new Spieler("B", "pw", 18);
        Spieler s3 = new Spieler("C", "pw", 18);
        raum.beitreten(s1);
        raum.beitreten(s2);
        raum.beitreten(s3);

        //raum.erstelleSpiel();

        for (Spieler s : raum.getSpieler()) {
            assertEquals(9, s.getSpielerHand().getHand().size(), "Spieler sollte 9 Karten haben");
        }
    }

    @Test
    public void testStarteSpielInitialisiertPartie() {
        Spielraum raum = new Spielraum("StartTest");
        raum.beitreten(new Spieler("X", "pw", 18));
        raum.beitreten(new Spieler("Y", "pw", 18));
        raum.beitreten(new Spieler("Z", "pw", 18));

        //raum.erstelleSpiel();
        raum.starteSpiel();

        assertNotNull(raum.getPartie(), "Partie sollte nicht null sein nach Spielstart");
    }

*/
}
