package cosmic.eidex.cont;


import cosmic.eidex.DTO.SpielerHandDTO;
import cosmic.eidex.spielmodell.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

/**
 * REST-Controller für Kartenaktionen eines Spielers (ausspielen und drücken).
 */
@RestController
@RequestMapping("/SpielerHand")
public class SpielerHandCont {

    @Autowired
    public StichStapel stichStapel;

    //Regel regel = new Regel("Rabe", "Normal");

    /**
     * Spielt eine Karte aus, prüft auf Gültigkeit und aktualisiert den Stichstapel.
     */
    @PostMapping("/KarteAusspielen")
    public SpielerHandDTO karteAusspielen(@RequestBody SpielerHandDTO handdto,
                                          @RequestParam String farbe,
                                          @RequestParam String wert,
                                          @RequestParam Regel regel){
        Karte zuSpielendeKarte = new Karte(farbe, wert);

        // Hand aktualisieren aus DTO
        SpielerHand hand = new SpielerHand(new ArrayList<>(handdto.getHand()), handdto.getGedrueckteKarte(), handdto.getSpieler());

        // Gültige Karten bestimmen
        List<Karte> gueltigeKarten = regel.getGueltigeKarten(hand, stichStapel);

        // Prüfen, ob Karte erlaubt ist
        if (!(gueltigeKarten.contains(zuSpielendeKarte))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ungültiger Spielzug: Diese Karte darf nicht gespielt werden.");
        }

        // Karte ausspielen
        hand.karteAusspielen(zuSpielendeKarte);
        stichStapel.addGespielteKarte(hand.getSpieler(), zuSpielendeKarte);

        // Neue gültige Karten berechnen
        List<Karte> neueGueltigeKarten = regel.getGueltigeKarten(hand, stichStapel);

        // Rückgabe mit neuen gültigen Karten
        return new SpielerHandDTO(hand.getHand(), neueGueltigeKarten, hand.getGedrueckteKarte(), hand.getSpieler());
    }

    /**
     * Markiert eine Karte als gedrückt.
     */
    @PostMapping("/karteDruecken")
    public SpielerHandDTO karteDruecken(@RequestBody SpielerHandDTO handdto,
                                        @RequestParam String farbe,
                                        @RequestParam String wert) {
        SpielerHand hand = new SpielerHand(new ArrayList<>(handdto.getHand()), null, handdto.getSpieler());
        Karte zuDrueckendeKarte = new Karte(farbe, wert);
        hand.karteDruecken(zuDrueckendeKarte);
        return new SpielerHandDTO(hand.getHand(), handdto.getGueltigeKarten(),hand.getGedrueckteKarte(), hand.getSpieler());
    }
}
