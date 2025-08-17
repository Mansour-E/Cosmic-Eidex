package cosmic.eidex.Bots;

import cosmic.eidex.spielmodell.Karte;
import cosmic.eidex.spielmodell.Spieler;
import cosmic.eidex.spielmodell.SpielerHand;
import cosmic.eidex.spielmodell.StichStapel;
import cosmic.eidex.spielmodell.Regel;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;

import java.util.List;
import java.util.Random;

/**
 * Einfache Bot-Implementierung, die zufällige und gültige Entscheidungen trifft.
 * Bot verhält sich wie ein normaler Spieler.
 */
@Entity
@DiscriminatorValue("easy")
public class EinfacherBot extends Bot {

    // Random Generator mit Seed: jede Instanzierung stellt sicher, dass
    // die gleiche Folge von Zufallszahlen generiert wird.
    @Transient
    private final Random random = new Random();

    public EinfacherBot(String nickname) {
        super(nickname);
        this.setTyp("easy");
    }

    public EinfacherBot(){
        super();
        this.setTyp("easy");
    }

    public void entscheideStrategie(Regel regel) {
        System.out.println(".");
    }

    /**
     * Entscheidet eine gültige Karte bzgl. der gegebenen Regel und Stapel.
     * @param regel Aktuelle Regel (Trumpf etc.)
     * @param stich Aktueller StichStapel
     * @return entschiedene Karte
     */
    public Karte zugSpielen(Regel regel, StichStapel stich) {
        List<Karte> gueltigeKarten = spielerHand.getHand()
                .stream()
                .filter(karte -> regel.istGueltigeKarte(spielerHand, karte, stich))
                .toList();
        List<Karte> kartenMitPunktGewinn = gueltigeKarten
                .stream()
                .filter(karte -> regel.getKartePunkte(karte) != 0)
                .toList();

        Karte entschieden;

        if (kartenMitPunktGewinn.isEmpty()) {
                // entscheide zufällig eine gültige Karte ohne Punktgewinn
                entschieden = gueltigeKarten.get(random.nextInt(0, gueltigeKarten.size()));
        } else {
                // entscheide zufällig eine gültige Karte mit Punktgewinn
                entschieden = kartenMitPunktGewinn.get(random.nextInt(0, kartenMitPunktGewinn.size()));
        }

        spielerHand.karteAusspielen(entschieden);
        return entschieden;
    }


    /**
     * Wähle zufällig eine Karte aus der Hand zum Drücken.
     */
    public Karte drueckeKarte(Regel regel) {
        Karte karte = spielerHand.getKarte(random.nextInt(0,spielerHand.getHand().size()));
        //spielerHand.karteDruecken(karte);
        return karte;
    }


}
