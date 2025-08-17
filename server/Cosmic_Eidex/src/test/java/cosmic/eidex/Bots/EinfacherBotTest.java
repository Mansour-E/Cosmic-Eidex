package cosmic.eidex.Bots;

import cosmic.eidex.spielmodell.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class EinfacherBotTest {

    private final EinfacherBot bot1 = new EinfacherBot("bot1");

    private List<Karte> handKarten;

    @BeforeEach
    void setUp() {
        SpielerHand hand = new SpielerHand();
        Karte karte1 = new Karte("Herz", "9");
        Karte karte2 = new Karte("Eidex", "5");
        Karte karte3 = new Karte("Herz", "6");
        handKarten = List.of(karte1, karte2, karte3);
        hand.addToHand(handKarten);
        bot1.setSpielerHand(hand);
    }

    @Test
    public void karteEntscheiden1() {
        Regel regel = new Regel("Herz", "Normal");
        StichStapel stich = new StichStapel();
        Karte entschieden = bot1.zugSpielen(regel, stich);
        assertEquals("Herz", entschieden.farbe);
        assert entschieden.wert.equals("9") || entschieden.wert.equals("6");
    }

    @Test
    public void karteEntscheiden2() {
        Regel regel = new Regel("Rabe", "Normal");
        StichStapel stich = new StichStapel();
        Karte entschieden = bot1.zugSpielen(regel, stich);
        assert entschieden.wert.equals("6") || entschieden.wert.equals("9") || entschieden.wert.equals("5");
    }

    @Test
    public void drueckeKarte() {
        Regel regel = new Regel("Herz", "Normal");
        Karte karte = bot1.drueckeKarte(regel);
        bot1.spielerHand.karteDruecken(karte);
        assertEquals(handKarten.size() - 1, bot1.spielerHand.getHand().size());
    }
}
