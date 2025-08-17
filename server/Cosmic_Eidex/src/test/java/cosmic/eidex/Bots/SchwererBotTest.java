package cosmic.eidex.Bots;

import cosmic.eidex.spielmodell.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SchwererBotTest {

    private SchwererBot bot;
    private Regel regelNor;
    private Regel regelObe;
    private Regel regelUnde;
    private Karte karte1;
    private Karte karte2;
    private Karte karte3;
    private Karte karte4;
    private Karte karte5;
    private Karte karte6;
    private Karte karte7;


    @BeforeEach
    public void setup() {
        bot = new SchwererBot("botty");
        regelNor = new Regel("Rabe", "Normal");
        regelObe = new Regel("", "Obenabe");
        regelUnde = new Regel("", "Undenufe");
        karte1 = new Karte("Herz", "9");
        karte2 = new Karte("Eidex", "8");
        karte3 = new Karte("Rabe", "Bube");
        karte4 = new Karte("Rabe", "Ass");
        karte5 = new Karte("Rabe", "10");
        karte6 = new Karte("Rabe", "7");
        karte7 = new Karte("Rabe", "6");


        bot.spielerHand.addToHand(List.of(karte1, karte2, karte3));
    }

    @Test
    public void setStratLowNormal(){

        bot.entscheideStrategie(regelNor);
        assertEquals("low", bot.getStrat());

    }

    @Test
    public void setStratLowObenabe(){

        bot.entscheideStrategie(regelObe);
        assertEquals("low", bot.getStrat());

    }

    @Test
    public void setStratLowUndenufe(){

        bot.entscheideStrategie(regelUnde);
        assertEquals("low", bot.getStrat());

    }

    @Test
    public void setStratHighNormal(){
        bot.spielerHand.addToHand(List.of(karte4));
        bot.entscheideStrategie(regelNor);
        assertEquals("high", bot.getStrat());

    }

    @Test
    public void setStratHighObenabe(){
        bot.spielerHand.addToHand(List.of(karte4, karte5));
        bot.entscheideStrategie(regelObe);
        assertEquals("high", bot.getStrat());

    }

    @Test
    public void setStratHighUndenufe(){
        bot.spielerHand.addToHand(List.of(karte5, karte7));
        bot.entscheideStrategie(regelUnde);
        assertEquals("high", bot.getStrat());

    }

    @Test
    public void drueckeKarte() {
        bot.setStrat("high");
        Karte karte = bot.drueckeKarte(regelNor);
        bot.spielerHand.karteDruecken(karte);
        // Die höchste Karte auf der Hand ist karte3 (Rabenbube); sie wird gedrückt und der Zähler aktualisiert.
        assertNotEquals(null, bot.spielerHand.gedrueckteKarte);
        assertEquals(karte3, bot.spielerHand.gedrueckteKarte);
        assertTrue(bot.getCounter() > 0);
    }


    //================ REGEL NORMAL TESTS ======================

    @Test
    public void zugSpielenLowNormal1() {
        bot.setStrat("low");
        StichStapel stich = new StichStapel();
        Karte gespielt = bot.zugSpielen(regelNor, stich);
        // Als erster: niedrigste gültige Karte = karte2 (Eidex 8) oder karte1 (Herz 9)
        assertTrue(List.of(karte1, karte2).contains(gespielt));
    }

    @Test
    public void zugSpielenLowNormal2() {
        bot.setStrat("low");
        StichStapel stich = new StichStapel();
        stich.addGespielteKarte(new Spieler("A", ""), new Karte("Rabe", "10"));
        stich.addGespielteKarte(new Spieler("B", ""), new Karte("Rabe", "Ass"));
        Karte gespielt = bot.zugSpielen(regelNor, stich);
        // Letzter ist bot, versucht zu verlieren, also spielt stärkste verlierende Karte (z.B. karte2 Eidex 8)
        //TODO : test ist richtig wenn Trumpfbuben regel richtig implementiert
        assertEquals(karte2, gespielt);
        assertTrue(bot.spielerHand.getHand().stream().noneMatch(k -> k.equals(gespielt)));
    }

    @Test
    public void zugSpielenHighNormal1() {
        bot.setStrat("high");
        bot.setCounter(0);
        // sollte karte3 drücken (Rabe Bube)
        Karte karte = bot.drueckeKarte(regelNor);
        bot.spielerHand.karteDruecken(karte);
        StichStapel stich = new StichStapel();
        Karte gespielt = bot.zugSpielen(regelNor, stich);
        assertEquals(karte3, bot.spielerHand.gedrueckteKarte);
        // entscheidet zufällig zwischen karte1 und karte2, also:
        assertTrue(List.of(karte1, karte2).contains(gespielt));
    }

    @Test
    public void zugSpielenHighNormal2() {
        bot.setStrat("high");
        bot.setCounter(0);
        Karte karte = bot.drueckeKarte(regelNor);
        bot.spielerHand.karteDruecken(karte);
        StichStapel stich = new StichStapel();
        stich.addGespielteKarte(new Spieler("A", ""), new Karte("Rabe", "10"));
        stich.addGespielteKarte(new Spieler("B", ""), new Karte("Rabe", "Dame"));
        Karte gespielt = bot.zugSpielen(regelNor, stich);
        assertNotNull(gespielt);
        assertTrue(bot.getCounter() >= regelNor.getKartePunkte(gespielt));
    }


    //================ REGEL OBENABE TESTS ======================
    @Test
    public void zugSpielenLowObenabe1() {
        bot.setStrat("low");
        StichStapel stich = new StichStapel();
        Karte gespielt = bot.zugSpielen(regelObe, stich);
        // Erster Zug, low → niedrigste gültige Karte nach Obenabe-Ordnung
        // Obenabe: Ass>König>Dame>Bube>10>9>8>7>6 → niedrigste im Blatt ist 8
        assertEquals(karte2, gespielt);
    }

    @Test
    public void zugSpielenLowObenabe2() {
        bot.setStrat("low");
        StichStapel stich = new StichStapel();
        stich.addGespielteKarte(new Spieler("A", ""), new Karte("Rabe", "10"));
        stich.addGespielteKarte(new Spieler("B", ""), new Karte("Rabe", "Ass"));
        Karte gespielt = bot.zugSpielen(regelObe, stich);
        // Letzter, low → stärkste verlierende Karte (Rabe Bube verliert gegen Ass)
        assertEquals(karte3, gespielt);
    }

    @Test
    public void zugSpielenHighObenabe1() {
        bot.setStrat("high");
        bot.setCounter(0);
        Karte karte = bot.drueckeKarte(regelObe);
        bot.spielerHand.karteDruecken(karte);
        StichStapel stich = new StichStapel();
        Karte gespielt = bot.zugSpielen(regelObe, stich);
        // Erster, high → höchste gültige Karte (Rabe Bube)
        assertEquals(karte1, gespielt);
        assertTrue(bot.getCounter() >= regelObe.getKartePunkte(gespielt));
    }

    @Test
    public void zugSpielenHighObenabe2() {
        bot.setStrat("high");
        bot.setCounter(0);
        // gedrückte Karte ist Rabe Bube (karte3)
        Karte karte = bot.drueckeKarte(regelObe);
        bot.spielerHand.karteDruecken(karte);
        StichStapel stich = new StichStapel();
        stich.addGespielteKarte(new Spieler("A", ""), new Karte("Rabe", "7"));
        stich.addGespielteKarte(new Spieler("B", ""), new Karte("Rabe", "6"));
        Karte gespielt = bot.zugSpielen(regelObe, stich);
        assertEquals(karte2, gespielt);
        assertTrue(bot.getCounter() >= regelObe.getKartePunkte(gespielt));
    }


    //============== REGEL UNDENUFE TESTS =====================
    @Test
    public void zugSpielenLowUndenufe1() {
        bot.setStrat("low");
        StichStapel stich = new StichStapel();
        Karte gespielt = bot.zugSpielen(regelUnde, stich);
        // Erster, low → niedrigste gültige Karte nach Undenufe-Ordnung
        // Undenufe: 6>7>8>9>10>Bube>…>Ass → niedrigste im Blatt ist Ass (nicht da), dann König… dann Bube
        assertEquals(karte3, gespielt);
    }

    @Test
    public void zugSpielenLowUndenufe2() {
        bot.setStrat("low");
        StichStapel stich = new StichStapel();
        stich.addGespielteKarte(new Spieler("A", ""), new Karte("Rabe", "10"));
        stich.addGespielteKarte(new Spieler("B", ""), new Karte("Rabe", "Ass"));
        Karte gespielt = bot.zugSpielen(regelUnde, stich);
        // Letzter, low → stärkste verlierende Karte (Rabe Bube verliert gegen 6>...>Ass)
        assertEquals(karte3, gespielt);
    }

    @Test
    public void zugSpielenHighUndenufe1() {
        bot.setStrat("high");
        bot.setCounter(0);
        // drückt zuerst Eidex-8 (karte2) weg:
        Karte karte = bot.drueckeKarte(regelUnde);
        bot.spielerHand.karteDruecken(karte);
        StichStapel stich = new StichStapel();
        Karte gespielt = bot.zugSpielen(regelUnde, stich);
        assertEquals(karte1, gespielt);
        // Und der Counter wurde um die Punkte dieser Karte erhöht:
        assertTrue(bot.getCounter() >= regelUnde.getKartePunkte(gespielt));
    }

    @Test
    public void zugSpielenHighUndenufe2() {
        bot.setStrat("high");
        bot.setCounter(0);
        Karte karte = bot.drueckeKarte(regelUnde);
        bot.spielerHand.karteDruecken(karte);
        StichStapel stich = new StichStapel();
        stich.addGespielteKarte(new Spieler("A", ""), new Karte("Rabe", "10"));
        stich.addGespielteKarte(new Spieler("B", ""), new Karte("Rabe", "Dame"));
        Karte gespielt = bot.zugSpielen(regelUnde, stich);
        // Letzter, high → kann keinen Stich gewinnen, Zufall aus gültigen (nur Rabe Bube)
        assertEquals(karte3, gespielt);
        assertTrue(bot.getCounter() >= regelUnde.getKartePunkte(gespielt));
    }

}
