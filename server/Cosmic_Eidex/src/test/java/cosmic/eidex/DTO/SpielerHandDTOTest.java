package cosmic.eidex.DTO;

import cosmic.eidex.spielmodell.Karte;
import cosmic.eidex.spielmodell.Spieler;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SpielerHandDTOTest {

    @Test
    void testKonstruktorUndGetter() {
        Spieler spieler = new Spieler("TestUser", "password");
        Karte k1 = new Karte("Herz", "10");
        Karte k2 = new Karte("Eidex", "Ass");
        Karte gedrueckte = new Karte("Rabe", "9");

        ArrayList<Karte> hand = new ArrayList<>(List.of(k1, k2));
        List<Karte> gueltigeKarten = List.of(k1);

        SpielerHandDTO dto = new SpielerHandDTO(hand, gueltigeKarten, gedrueckte, spieler);

        assertEquals(2, dto.getHand().size());
        assertEquals(1, dto.getGueltigeKarten().size());
        assertEquals("Rabe", dto.getGedrueckteKarte().getFarbe());
        assertEquals("TestUser", dto.getSpieler().getNickname());
    }

    @Test
    void testSetterUndGetter() {
        Spieler spieler = new Spieler("AnotherUser", "1234");
        SpielerHandDTO dto = new SpielerHandDTO();

        List<Karte> hand = List.of(new Karte("Stern", "Bube"));
        List<Karte> gueltigeKarten = List.of(new Karte("Herz", "8"));
        Karte gedrueckt = new Karte("Rabe", "7");

        dto.setHand(hand);
        dto.setGueltigeKarten(gueltigeKarten);
        dto.setGedrueckteKarte(gedrueckt);
        dto.setSpieler(spieler);

        assertEquals("Stern", dto.getHand().get(0).getFarbe());
        assertEquals("Herz", dto.getGueltigeKarten().get(0).getFarbe());
        assertEquals("Rabe", dto.getGedrueckteKarte().getFarbe());
        assertEquals("AnotherUser", dto.getSpieler().getNickname());
    }
}
