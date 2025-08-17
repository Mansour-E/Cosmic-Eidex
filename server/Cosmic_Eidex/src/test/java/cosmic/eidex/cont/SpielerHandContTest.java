package cosmic.eidex.cont;

import cosmic.eidex.DTO.SpielerHandDTO;
import cosmic.eidex.spielmodell.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SpielerHandContTest {

    private SpielerHandCont controller;
    private StichStapel stichStapel;
    private Regel regel;

    @BeforeEach
    void setUp() {
        stichStapel = new StichStapel();
        controller = new SpielerHandCont();
        controller.stichStapel = stichStapel;
        regel = new Regel("Rabe", "Obenabe"); // Vereinfachung für Tests
    }

    @Test
    void testKarteAusspielen_Gueltig() {
        Spieler spieler = new Spieler("test", "pw", 20);
        Karte karte = new Karte("Rabe", "9");
        ArrayList<Karte> hand = new ArrayList<>();
        hand.add(karte);

        SpielerHand spielerHand = new SpielerHand(new ArrayList<>(hand), null, spieler);
        List<Karte> gueltigeKarten = regel.getGueltigeKarten(spielerHand, stichStapel);

        SpielerHandDTO dto = new SpielerHandDTO(hand, gueltigeKarten, null, spieler);

        SpielerHandDTO result = controller.karteAusspielen(dto, "Rabe", "9",regel);

        assertFalse(result.getHand().contains(karte));
        assertEquals("Rabe", stichStapel.getStichKarte(spieler).getFarbe());
        assertEquals("9", stichStapel.getStichKarte(spieler).getWert());
        assertEquals(1, stichStapel.getGespielteKarten().size());
        assertEquals(karte, stichStapel.getStichKarte(spieler));
    }

    @Test
    void testKarteAusspielen_Ungueltig() {
        Spieler spieler = new Spieler("test", "pw", 20);
        Karte karte = new Karte("Eidex", "6");
        ArrayList<Karte> hand = new ArrayList<>();
        hand.add(karte);

        SpielerHand spielerHand = new SpielerHand(new ArrayList<>(hand), null, spieler);
        List<Karte> gueltigeKarten = regel.getGueltigeKarten(spielerHand, stichStapel);

        SpielerHandDTO dto = new SpielerHandDTO(hand, gueltigeKarten, null, spieler);

        assertThrows(ResponseStatusException.class, () -> {
            controller.karteAusspielen(dto, "Herz", "7",regel);
        });
    }

    @Test
    void testKarteDruecken() {
        Spieler spieler = new Spieler("test", "pw", 20);
        Karte karte = new Karte("Herz", "7");
        ArrayList<Karte> hand = new ArrayList<>();
        hand.add(karte);

        SpielerHandDTO dto = new SpielerHandDTO(hand, null, null, spieler);

        SpielerHandDTO result = controller.karteDruecken(dto, "Herz", "7");

        assertFalse(result.getHand().contains(karte));
        assertEquals("Herz", result.getGedrueckteKarte().getFarbe());
        assertEquals("7", result.getGedrueckteKarte().getWert());
    }
}
