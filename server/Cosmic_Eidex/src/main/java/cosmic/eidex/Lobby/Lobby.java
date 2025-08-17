package cosmic.eidex.Lobby;

import cosmic.eidex.spielmodell.Spieler;

import java.util.ArrayList;
import java.util.List;

/**
 * Die Lobby Klasse in der Spieler Spielraeumen beitreten oder diese erstellen koennen sowie Nachrichten schreiben oder ihr Profil bearbeiten
 */
public class Lobby {

    public List<Spielraum> spielraeume;
    public  List<Spieler> spieler;

    /**
     * Konstruktor fuer Lobby
     */
    public Lobby() {
        this.spielraeume = new ArrayList<Spielraum>();
        this.spieler = new ArrayList<Spieler>();
    }

    /**
     * Konstruktor fuer Lobby
     */
    public Lobby(List<Spielraum> spielraeume, List<Spieler> spieler) {
        this.spielraeume = spielraeume;
        this.spieler = spieler;
    }

    /**
     * Gibt Liste der Spielraume zurueck
     * @return Liste von spielraeumen
     */
    public List<Spielraum> spielrauemeAnzeigen (){
        System.out.println("Die Spielraeume sind: " );
        System.out.println();
        return this.spielraeume;
        }

    /**
     * Erzeugt neuen Raum
     * @param name des Raums
     */
    public void erzeugeNeuenRaum(String name) {
        for (Spielraum raum : spielraeume) {
            if (raum.getName().equalsIgnoreCase(name)) {
                System.out.println("Dieser Raumname existiert bereits.");
                return;
            }
        }

        Spielraum neuerSpielraum = new Spielraum(name);
        spielraeume.add(neuerSpielraum);
        System.out.println("Raum wurde erfolgreich erzeugt.");
    }
}




