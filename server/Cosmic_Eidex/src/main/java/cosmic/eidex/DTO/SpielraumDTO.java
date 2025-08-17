package cosmic.eidex.DTO;

import cosmic.eidex.spielmodell.Spieler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DTO - Data Transfer Object, das der Controller dem Client übergeben kann.
 * SpielraumDTO kann verwendet werden um Informationen über Spielräume zu übertragen.
 */
public class SpielraumDTO {
    private Long id;
    private String name;
    private List<Spieler> spieler;
    private Map<Integer, String> platzMap;

    /**
     * Leerer Konstruktor
     */
    public SpielraumDTO() {}

    /**
     * Konstruktor für SpielraumDTO
     * @param id die Raum Id
     * @param name der Name des Raumes
     * @param spieler die Spielerliste des Raumes
     * @param platzMap die Platzmap die jedem Spieler seinen Platz zuordnet
     */
    public SpielraumDTO(Long id, String name, List<Spieler> spieler, Map<Integer, String> platzMap) {
        this.id = id;
        this.name = name;
        this.spieler = spieler;
        this.platzMap = platzMap != null ? platzMap : new HashMap<>();
    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public List<Spieler> getSpieler() {
        return spieler;
    }

    public Map<Integer, String> getPlatzMap() {
        return platzMap;
    }

    public void setPlatzMap(Map<Integer, String> platzMap) {
        this.platzMap = platzMap;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSpieler(List<Spieler> spieler) {
        this.spieler = spieler;
    }
}
