package cosmic.eidex.spielmodell;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class KartenDeckTest {

    private KartenDeck deck;
    private KartenDeck deck2;

    @BeforeEach
    public void setup() {
        deck = new KartenDeck();
        ArrayList<Karte> list  = new ArrayList<>();
        list.add(new Karte("Stern", "10"));
        list.add(new Karte("Stern", "8"));
        deck2 = new KartenDeck(list);
    }

    @Test
    public void testDeckHas36Karten() {
        assertEquals(36, deck.deck.size());
    }

    @Test
    public void testShuffleChangesOrder() {
        String before = deck.deck.get(0).getFarbe() + deck.deck.get(0).getWert();
        deck.shuffle();
        String after = deck.deck.get(0).getFarbe() + deck.deck.get(0).getWert();
        assertNotEquals(before, after);
    }

    @Test
    public void testZiehenReduziertDeck() {
        SpielerHand hand = new SpielerHand();
        deck.ziehen(hand);

        assertEquals(32, deck.deck.size());
        assertEquals(4, hand.getHand().size());
    }

    @Test
    public void testDeckHatKeineDoppeltenKarten() {
        Set<String> kartenSet = new HashSet<>();
        for (Karte karte : deck.deck) {
            String id = karte.getFarbe() + karte.getWert();
            assertFalse(kartenSet.contains(id), "Duplikat: " + id);
            kartenSet.add(id);
        }
        assertEquals(36, kartenSet.size());
    }

    @Test
    void testGetDeck() {
        ArrayList<Karte> list  = new ArrayList<>();
        list.add(new Karte("Stern", "10"));
        list.add(new Karte("Stern", "8"));
        assertEquals(2, deck2.getDeck(list).size());
    }
}
