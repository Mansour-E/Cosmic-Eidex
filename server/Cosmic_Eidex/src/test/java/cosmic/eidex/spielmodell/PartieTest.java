package cosmic.eidex.spielmodell;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PartieTest {

    public Turnier turnier;
    public Partie partie;
    public SpielStand spielstand;
    public Regel regel1;
    public Regel regel2;
    public Spieler Alice;
    public Spieler Bob;
    public Spieler Charlie;
    public List<Spieler> spieler;
    public KartenDeck deck;
    public Karte k1;
    public Karte k2;
    public Karte k3;
    public Karte k4;
    SpielerHand hand;
    ArrayList<Karte> karten;

    @BeforeEach
    void setUp() {
        Bob = new Spieler("Bob", "placeholder1");
        Alice = new Spieler("Alice", "placeholder2");
        Charlie = new Spieler("Charlie", "placeholder3");
        spieler = Arrays.asList(Bob, Alice, Charlie);
        turnier = new Turnier(spieler);
        spielstand = new SpielStand(spieler);
        regel1 = mock(Regel.class);
        regel2 = mock(Regel.class);
        deck = new KartenDeck();
        karten = new ArrayList<>();
        k1 = new Karte("Herz","Dame");
        k2 = new Karte("Stern","6");
        k3 = new Karte("Rabe","Dame");
        karten.add(k1);
        karten.add(k2);
        karten.add(k3);
        karten.add(k4);
        hand = new SpielerHand(karten,Alice);
        partie = new Partie(turnier, regel1, spieler, deck, true);



    }

    @Test
    void getSpielstand() {
        //assertEquals(spielstand, partie.getSpielstand());
        assertEquals(spielstand.getPunkte(Bob), partie.getSpielstand().getPunkte(Bob));
    }

    @Test
    void testGetRegel() {
        assertEquals(regel1, partie.getRegel());
    }

    @Test
    void getPunkteZuSpieler() {
        partie.aktualisierePunkte(Alice,11);
        assertEquals(11,partie.getPunkteZuSpieler(Alice));
    }

    @Test
    void testStarteUndVerteileKarten() {
        partie.starte();
        assertEquals(1, partie.getRundenzahl());
        for (Spieler spieler : spieler) {
            ArrayList<Karte> hand = spieler.getSpielerHand().getHand();
            int anzahlKarten = hand.size();
            assertEquals(12,anzahlKarten);
        }
    }

    @Test
    void testNaechsteRunde() {
        assertEquals(0, partie.getRundenzahl());
        assertNull(partie.getAktuelleRunde());
        partie.naechsteRunde();
        assertEquals(1, partie.getRundenzahl());
        assertNotNull(partie.getAktuelleRunde());
        assertEquals(1, partie.getAktuelleRunde().getRundenzahl());
    }


    @Test
    void testBestimmeErsterSpieler() {
        assertInstanceOf(Spieler.class, partie.bestimmeErsterSpieler());
    }

    @Test
    void testBeende() {
        partie.setRundenzahl(12);
        HashMap<Spieler, Integer> liste = new HashMap<>();
        liste.put(Alice, 2);
        liste.put(Bob, 0);
        liste.put(Charlie, 0);
        turnier.setSiegPunkteListe(liste);
        partie.beende();
        assertNotEquals(partie, turnier.getAktuellePartie());
    }

    @Test
    void testBeendeTurnier() {
        partie.setRundenzahl(12);
        HashMap<Spieler, Integer> liste = new HashMap<>();
        liste.put(Alice, 7);
        liste.put(Bob, 0);
        liste.put(Charlie, 0);
        turnier.setSiegPunkteListe(liste);
        partie.beende();
        assertTrue(turnier.istAbgeschlossen);
    }

    @Test
    void aktualisierePunkte() {
        assertEquals(0, partie.getSpielstand().getPunkte(Alice));
        partie.aktualisierePunkte(Alice,4);
        assertEquals(4, partie.getSpielstand().getPunkte(Alice));
    }


    @Test
    void testGetHandVonSpieler() {
        Alice.setSpielerHand(hand);
        assertEquals(karten, partie.getHandVonSpieler("Alice"));

    }

    @Test
    void testBeendeMitRegel1() {
        partie.getSpielstand().setPunkte(Alice,157);
        partie.setRundenzahl(12);
        partie.beende();
        assertEquals(2, turnier.getSiegPunkteListe().get(Alice));
    }

    @Test
    void testBeendeMitRegel2() {
        partie.getSpielstand().setPunkte(Alice,101);
        partie.getSpielstand().setPunkte(Bob,30);
        partie.getSpielstand().setPunkte(Charlie,26);
        partie.setRundenzahl(12);
        partie.beende();
        assertEquals(1, turnier.getSiegPunkteListe().get(Bob));
        assertEquals(1, turnier.getSiegPunkteListe().get(Charlie));
    }

    @Test
    void testBeendeMitRegel3() {
        partie.getSpielstand().setPunkte(Alice,60);
        partie.getSpielstand().setPunkte(Bob,50);
        partie.getSpielstand().setPunkte(Charlie,47);
        partie.setRundenzahl(12);
        partie.beende();
        assertEquals(1, turnier.getSiegPunkteListe().get(Alice));
        assertEquals(1, turnier.getSiegPunkteListe().get(Charlie));
    }

    @Test
    void testBeendeMitRegel4() {
        partie.getSpielstand().setPunkte(Alice,60);
        partie.getSpielstand().setPunkte(Bob,60);
        partie.getSpielstand().setPunkte(Charlie,37);
        partie.setRundenzahl(12);
        partie.beende();
        assertEquals(2, turnier.getSiegPunkteListe().get(Charlie));
    }

    @Test
    void testBeendeMitRegel4v2() {
        partie.getSpielstand().setPunkte(Alice,30);
        partie.getSpielstand().setPunkte(Bob,30);
        partie.getSpielstand().setPunkte(Charlie,97);
        partie.setRundenzahl(12);
        partie.beende();
        assertEquals(2, turnier.getSiegPunkteListe().get(Charlie));
    }

    @Test
    void testBeendeMitSonderRegel1() {
        turnier.aktualisiereSiegpunkteliste(Alice,6);
        turnier.aktualisiereSiegpunkteliste(Bob,6);
        turnier.aktualisiereSiegpunkteliste(Charlie,2);
        partie.getSpielstand().setPunkte(Alice,30);
        partie.getSpielstand().setPunkte(Bob,10);
        partie.getSpielstand().setPunkte(Charlie,107);
        partie.setRundenzahl(12);
        partie.beende();
        assertEquals(6, turnier.getSiegPunkteListe().get(Alice));
        assertEquals(5, turnier.getSiegPunkteListe().get(Bob));
        assertEquals(2, turnier.getSiegPunkteListe().get(Charlie));

    }

    @Test
    void testBeendeMitSonderRegel2() {
        turnier.aktualisiereSiegpunkteliste(Alice,6);
        turnier.aktualisiereSiegpunkteliste(Bob,6);
        turnier.aktualisiereSiegpunkteliste(Charlie,2);
        partie.getSpielstand().setPunkte(Alice,20);
        partie.getSpielstand().setPunkte(Bob,20);
        partie.getSpielstand().setPunkte(Charlie,117);
        partie.setRundenzahl(12);
        partie.beende();
        assertEquals(6, turnier.getSiegPunkteListe().get(Alice));
        assertEquals(6, turnier.getSiegPunkteListe().get(Bob));
        assertEquals(2, turnier.getSiegPunkteListe().get(Charlie));
    }

    @Test
    void drueckeKarte() {
        assertNull(Alice.getSpielerHand().getGedrueckteKarte());
        Alice.getSpielerHand().addToHand(List.of(k1));
        partie.drueckeKarte("Alice", k1);
        assertNotNull(Alice.getSpielerHand().getGedrueckteKarte());
    }

    @Test
    void alleGedrueckt() {
        assertFalse(partie.alleGedrueckt());
        Alice.getSpielerHand().addToHand(List.of(k1));
        partie.drueckeKarte("Alice", k1);
        Bob.getSpielerHand().addToHand(List.of(k2));
        partie.drueckeKarte("Bob", k2);
        Charlie.getSpielerHand().addToHand(List.of(k3));
        partie.drueckeKarte("Charlie", k3);
        assertTrue(partie.alleGedrueckt());
    }
}