package cosmic.eidex.spielmodell;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.rsocket.MetadataExtractorRegistryExtensionsKt;

import java.util.*;

import static cosmic.eidex.spielmodell.Regel.zieheTrumpf;
import static org.junit.jupiter.api.Assertions.*;

public class RegelTest {

    private Spieler s1;
    private Spieler s2;
    private SpielerHand hand;

    @BeforeEach
    public void setup() {
        s1 = new Spieler("Erik", "AA");
        s2 = new Spieler("Seli", "BB");
        hand = new SpielerHand();
        hand.getHand().addAll(Arrays.asList(
                new Karte("Herz", "Ass"),
                new Karte("Eidex", "9"),
                new Karte("Herz", "6"),
                new Karte("Rabe", "Bube")
        ));
    }

    @Test
    public void testEntscheideRegelObenabe() {
        Regel regel = Regel.entscheideRegel(new Karte("Herz", "Ass"));
        assertEquals("Obenabe", regel.getRegel());
    }

    @Test
    public void testEntscheideRegelUndenufe() {
        Regel regel = Regel.entscheideRegel(new Karte("Stern", "6"));
        assertEquals("Undenufe", regel.getRegel());
    }

    @Test
    public void testEntscheideRegelNormal() {
        Regel regel = Regel.entscheideRegel(new Karte("Herz", "9"));
        assertEquals("Normal", regel.getRegel());
        assertEquals("Herz", regel.trumpf);
    }

    @Test
    public void testStichHatTrumpf() {
        Regel regel = new Regel("Herz", "Normal");
        StichStapel stich = new StichStapel();
        stich.gespielteKarten.put(s1, new Karte("Herz", "10"));
        assertTrue(regel.stichHatTrumpf(stich));
    }

    @Test
    public void testStichHatKeinenTrumpf() {
        Regel regel = new Regel("Eidex", "Normal");
        StichStapel stich = new StichStapel();
        stich.gespielteKarten.put(s1, new Karte("Herz", "10"));
        assertFalse(regel.stichHatTrumpf(stich));
    }

    @Test
    public void testHandHatFarbeTrue() {
        assertTrue(Regel.handHatFarbe(hand, "Herz"));
    }

    @Test
    public void testHandHatFarbeFalse() {
        SpielerHand testHand = new SpielerHand();
        testHand.getHand().addAll(Arrays.asList(
                new Karte("Herz", "6"),
                new Karte("Herz", "7")
        ));
        assertFalse(Regel.handHatFarbe(testHand, "Eidex"));
    }

    @Test
    public void testIstGueltigeKarte_AnfangDesStichs() {
        Regel regel = new Regel("Herz", "Normal");
        StichStapel stich = new StichStapel();
        assertTrue(regel.istGueltigeKarte(hand, hand.getHand().getFirst(), stich));
    }

    @Test
    public void testIstGueltigeKarte_FarbeMussBedientWerden() {
        Regel regel = new Regel("Herz", "Normal");
        StichStapel stich = new StichStapel();
        stich.gespielteKarten.put(s1, new Karte("Rabe", "8"));
        assertFalse(regel.istGueltigeKarte(hand, new Karte("Stern", "6"), stich));
        assertTrue(regel.istGueltigeKarte(hand, new Karte("Herz", "6"), stich));
        assertTrue(regel.istGueltigeKarte(hand, new Karte("Rabe", "Bube"), stich));
    }

    @Test
    public void testGetGueltigeKarten() {
        Regel regel = new Regel("Herz", "Normal");
        StichStapel stich = new StichStapel();
        stich.gespielteKarten.put(s1, new Karte("Rabe", "7"));
        List<Karte> result = regel.getGueltigeKarten(hand, stich);
        assertEquals(3, result.size());
    }

    @Test
    public void testTrumpfBubeMussNicht(){
        Regel regel = new Regel("Rabe", "Normal");
        StichStapel stichStapel = new StichStapel();
        stichStapel.gespielteKarten.put(s1, new Karte("Rabe", "8"));
        stichStapel.gespielteKarten.put(s2, new Karte("Rabe", "Dame"));
        List<Karte> gueltig = regel.getGueltigeKarten(hand, stichStapel);
        for(Karte karte : gueltig){
            System.out.println(karte.toString());
        }
        assertTrue(regel.istGueltigeKarte(hand, new Karte("Rabe", "Bube"), stichStapel));
        assertTrue(regel.istGueltigeKarte(hand, new Karte("Herz", "6"), stichStapel));
    }

    @Test
    public void testStichGewinnerObenabe() {
        Regel regel = new Regel("", "Obenabe");
        StichStapel stich = new StichStapel();
        stich.gespielteKarten.put(s1, new Karte("Herz", "8"));
        stich.gespielteKarten.put(s2, new Karte("Herz", "Ass"));

        Spieler gewinner = regel.bestimmeStichGewinner(stich);
        assertEquals(s2, gewinner);
    }

    @Test
    public void testStichGewinnerUndenufe() {
        Regel regel = new Regel("", "Undenufe");
        StichStapel stich = new StichStapel();
        stich.gespielteKarten.put(s1, new Karte("Herz", "6"));
        stich.gespielteKarten.put(s2, new Karte("Herz", "Ass"));

        Spieler gewinner = regel.bestimmeStichGewinner(stich);
        assertEquals(s1, gewinner);
    }

    @Test
    public void testStichGewinnerNormalMitTrumpf() {
        Regel regel = new Regel("Herz", "Normal");
        StichStapel stich = new StichStapel();
        stich.gespielteKarten.put(s1, new Karte("Eidex", "Ass"));
        stich.gespielteKarten.put(s2, new Karte("Herz", "7"));

        Spieler gewinner = regel.bestimmeStichGewinner(stich);
        assertEquals(s2, gewinner);
    }

    @Test
    public void testStichGewinnerNormalOhneTrumpf() {
        Regel regel = new Regel("Herz", "Normal");
        StichStapel stich = new StichStapel();
        stich.gespielteKarten.put(s1, new Karte("Eidex", "8"));
        stich.gespielteKarten.put(s2, new Karte("Eidex", "9"));

        Spieler gewinner = regel.bestimmeStichGewinner(stich);
        assertEquals(s2, gewinner);
    }

    @Test
    public void testZieheTrumpf() {
        KartenDeck deck = new KartenDeck();
        TrumpfAuswahl auswahl = zieheTrumpf(deck);
        Regel regel = auswahl.getRegel();
        assertNotNull(regel);
        assertTrue(List.of("Normal", "Obenabe", "Undenufe").contains(regel.regel));
    }

    @Test
    public void testKarte2IstHoeher() {
        Regel regel = new Regel("", "Obenabe");
        Karte k1 = new Karte("Herz", "8");
        Karte k2 = new Karte("Herz", "Ass");
        List<String> reihenfolge = regel.reihenfolgeObenabe;
        assertTrue(regel.karte2IstHoeher(k1, k2, reihenfolge));
        assertFalse(regel.karte2IstHoeher(k2, k1, reihenfolge));
    }

    @Test
    public void testKartenDieStichGewinnen() {
        Regel regel = new Regel("", "Obenabe");
        StichStapel stich = new StichStapel();
        stich.gespielteKarten.put(s1, new Karte("Herz", "7"));
        stich.gespielteKarten.put(s2, new Karte("Herz", "10"));

        List<Karte> handKarten = List.of(new Karte("Herz", "Ass"), new Karte("Eidex", "6"));
        List<Karte> gewinnende = regel.kartenDieStichGewinnen(stich, handKarten);
        assertEquals(1, gewinnende.size());
        assertEquals("Ass", gewinnende.get(0).getWert());
    }

    @Test
    public void testKartenDieStichVerlieren() {
        Regel regel = new Regel("", "Obenabe");
        StichStapel stich = new StichStapel();
        stich.gespielteKarten.put(s1, new Karte("Herz", "10"));

        List<Karte> handKarten = List.of(new Karte("Herz", "7"), new Karte("Herz", "9"));
        List<Karte> verlierer = regel.kartenDieStichVerlieren(stich, handKarten);
        assertEquals(2, verlierer.size());
    }

    @Test
    public void testGetNiedrigsteKarte() {
        Regel regel = new Regel("", "Obenabe");
        List<Karte> karten = List.of(
                new Karte("Herz", "9"),
                new Karte("Herz", "7"),
                new Karte("Herz", "Ass")
        );
        Karte niedrigste = regel.getNiedrigsteKarte(karten);
        assertEquals("7", niedrigste.getWert());
    }

    @Test
    public void testGetHoechsteKarte() {
        Regel regel = new Regel("", "Obenabe");
        List<Karte> karten = List.of(
                new Karte("Herz", "9"),
                new Karte("Herz", "7"),
                new Karte("Herz", "Ass")
        );
        Karte hoechste = regel.getHoechsteKarte(karten);
        assertEquals("Ass", hoechste.getWert());
    }

    @Test
    public void testGetKartePunkteNormal() {
        Regel regel = new Regel("Herz", "Normal");
        Karte assTrumpf = new Karte("Herz", "Ass");
        Karte bubeNichtTrumpf = new Karte("Rabe", "Bube");
        assertEquals(11, regel.getKartePunkte(assTrumpf));
        assertEquals(2, regel.getKartePunkte(bubeNichtTrumpf));
    }

    @Test
    public void testGetKartePunkteMitUngueltigemWert() {
        Regel regel = new Regel("Herz", "Normal");
        Karte sonderKarte = new Karte("Herz", "5"); // nicht im Wert-Switch
        assertEquals(0, regel.getKartePunkte(sonderKarte));
    }



}