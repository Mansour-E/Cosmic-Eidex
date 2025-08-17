package cosmic.eidex.spielmodell;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RundeTest {

    private Spieler Alice;
    private Spieler Bob;
    private Spieler Charlie;
    private Runde runde;
    private StichStapel stichstapel;
    private Partie partie;
    private Karte karte1;
    private Karte karte2;
    private Karte karte3;
    private Karte karte4;
    private Karte karte5;
    private Karte karte6;
    private Karte karte7;
    private Karte karte8;
    private Karte karte9;
    private Karte karte10;
    private Karte karte11;
    private Karte karte12;
    private Regel regel1;
    private Regel regel2;
    private Regel regel3;



    //Testspieler
    private Spieler createMockSpieler(String name) {
        Spieler spieler = mock(Spieler.class);
        when(spieler.getNickname()).thenReturn(name);
        return spieler;


    }

    //TestPartie
    private Partie createMockPartie(Regel regel) {
        return mock(Partie.class);
    }

    @BeforeEach
    void setUp() {
        partie = mock(Partie.class);
        Alice = createMockSpieler("Alice");
        Bob = createMockSpieler("Bob");
        Charlie= createMockSpieler("Charlie");
        Regel regel = new Regel("Eidex", "Normal");
        List<Spieler> spielerliste = Arrays.asList(Bob, Alice, Charlie);
        partie = createMockPartie(regel);
        karte1 = new Karte("Eidex", "6");
        karte2 = new Karte("Eidex", "10");
        karte3 = new Karte("Eidex", "Bube");
        karte4 = new Karte("Herz", "8");
        karte5 = new Karte("Herz", "9");
        karte6 = new Karte("Herz", "10");
        karte7 = new Karte("Stern", "7");
        karte8 = new Karte("Stern", "Koenig");
        karte9 = new Karte("Stern", "Dame");
        karte10 = new Karte("Eidex", "Ass");
        karte11 = new Karte("Eidex", "Koenig");
        karte12 = new Karte("Eidex", "9");

        runde = new Runde(partie, regel, 1, spielerliste, Bob);
        stichstapel = runde.stichstapel;
        regel1 = new Regel("Eidex", "Normal");
        regel2 = new Regel("", "Obenabe");
        regel3 = new Regel("", "Undenufe");


    }

    @Test
    void bewerteStichNormal1() {
        stichstapel.addGespielteKarte(Alice , karte1);
        stichstapel.addGespielteKarte(Bob, karte2);
        stichstapel.addGespielteKarte(Charlie, karte3);
        runde.setRegel(regel1);
        assertEquals(30 ,runde.bewerteStich());
        assertEquals(Charlie ,runde.getStichGewinner());
    }
    @Test
    void bewerteStichNormal2() {
        stichstapel.addGespielteKarte(Alice , karte9);
        stichstapel.addGespielteKarte(Bob, karte8);
        stichstapel.addGespielteKarte(Charlie, karte7);
        runde.setRegel(regel1);
        assertEquals(7 ,runde.bewerteStich());
        assertEquals(Bob ,runde.getStichGewinner());
    }
    @Test
    void bewerteStichNormal3() {
        stichstapel.addGespielteKarte(Alice , karte10);
        stichstapel.addGespielteKarte(Bob, karte11);
        stichstapel.addGespielteKarte(Charlie, karte12);
        runde.setRegel(regel1);
        assertEquals(29 ,runde.bewerteStich());
        assertEquals(Charlie ,runde.getStichGewinner());
    }

    @Test
    void bewerteStichObenabe1() {
        stichstapel.addGespielteKarte(Alice , karte1);
        stichstapel.addGespielteKarte(Bob, karte2);
        stichstapel.addGespielteKarte(Charlie, karte3);
        runde.setRegel(regel2);
        assertEquals(12,runde.bewerteStich());
        assertEquals(Charlie ,runde.getStichGewinner());
    }

    @Test
    void bewerteStichObenabe2() {
        stichstapel.addGespielteKarte(Alice , karte4);
        stichstapel.addGespielteKarte(Bob, karte5);
        stichstapel.addGespielteKarte(Charlie, karte6);
        runde.setRegel(regel2);
        assertEquals(10,runde.bewerteStich());
        assertEquals(Charlie ,runde.getStichGewinner());
    }
    @Test
    void bewerteStichObenabe3() {
        stichstapel.addGespielteKarte(Alice , karte10);
        stichstapel.addGespielteKarte(Bob, karte11);
        stichstapel.addGespielteKarte(Charlie, karte12);
        runde.setRegel(regel2);
        assertEquals(15,runde.bewerteStich());
        assertEquals(Alice ,runde.getStichGewinner());
    }

    @Test
    void bewerteStichUndenufe1() {
        stichstapel.addGespielteKarte(Alice , karte2);
        stichstapel.addGespielteKarte(Bob, karte1);
        stichstapel.addGespielteKarte(Charlie, karte3);
        runde.setRegel(regel3);
        assertEquals(23,runde.bewerteStich());
        assertEquals(Bob ,runde.getStichGewinner());
    }
    @Test
    void bewerteStichUndenufe2() {
        stichstapel.addGespielteKarte(Alice , karte5);
        stichstapel.addGespielteKarte(Bob, karte6);
        stichstapel.addGespielteKarte(Charlie, karte1);
        runde.setRegel(regel3);
        assertEquals(21,runde.bewerteStich());
        assertEquals(Alice,runde.getStichGewinner());
    }
    @Test
    void bewerteStichUndenufe3() {
        stichstapel.addGespielteKarte(Alice , karte4);
        stichstapel.addGespielteKarte(Bob, karte8);
        stichstapel.addGespielteKarte(Charlie, karte9);
        runde.setRegel(regel3);
        assertEquals(15,runde.bewerteStich());
        assertEquals(Alice ,runde.getStichGewinner());
    }
    @Test
    void startRunde() {
        assertEquals("Runde 1 startet: Spieler Bob ist am Zug!", runde.startRunde());
        assertEquals(1,runde.getRundenzahl());
    }

    @Test
    void getNaechsterSpieler() {
        assertEquals(Bob,runde.getAktuellerSpieler());
        stichstapel.addGespielteKarte(Alice , karte1);
        stichstapel.addGespielteKarte(Bob, karte2);
        stichstapel.addGespielteKarte(Charlie, karte3);
        runde.setRegel(regel3);
        assertEquals(23,runde.bewerteStich());
        assertEquals(Alice , runde.getStichGewinner());
        assertEquals(Alice , runde.getNaechsterSpieler());
        assertEquals(Alice , runde.getAktuellerSpieler());
    }
}