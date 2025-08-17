package cosmic.eidex.Lobby;


import cosmic.eidex.Bots.EinfacherBot;
import cosmic.eidex.Bots.SchwererBot;
import cosmic.eidex.Service.Spielstatus;
import cosmic.eidex.spielmodell.Partie;
import cosmic.eidex.spielmodell.Spieler;
import cosmic.eidex.spielmodell.Turnier;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.List;

/**
 * Repraesentiert einen Spielraum der auch in der Datenbank gespeichert wird.
 */
@Entity
@Table(name = "spielraeume")
public class Spielraum {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String passwort;

    @Column(nullable = false)
    private boolean isStarted = false;

    @Column(nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "spielraum_id")
    private List<Spieler> spieler = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "spielraum_bereit_status", joinColumns = @JoinColumn(name = "spielraum_id"))
    @MapKeyColumn(name = "nickname")
    @Column(name = "bereit")
    private Map<String, Boolean> bereitMap = new HashMap<>();

    @ElementCollection
    @CollectionTable(name = "spielraum_platz_map", joinColumns = @JoinColumn(name = "spielraum_id"))
    @MapKeyColumn(name = "platz")
    @Column(name = "nickname")
    private Map<Integer, String> platzMap = new HashMap<>();

    @Transient
    private Turnier turnier;

    @Transient
    private Partie partie;

    @Transient
    private Spielstatus status;

    /**
     * leerer Konstruktor
     */
    public Spielraum() {}

    /**
     * Konstruktor fuer Spielraum.
     * @param name
     */
    public Spielraum(String name) {
        this.name = name;
    }

    /**
     * Methode fuer das beitreten in einen Spielraum, solange nicht mehr als 3 Spieler in einem Raum sind.
     * @param spieler der beitretende Spieler
     * @return True/False je nach Erfolg
     */
    public boolean beitreten(Spieler spieler) {
        if (this.spieler.size() < 3 && this.spieler.stream().noneMatch(s -> s.getNickname().equals(spieler.getNickname()))) {
            this.spieler.add(spieler);
            return true;
        }
        return false;
    }

    /**
     * Entfernt einen Spieler aus einem Spielraum
     * @param nickname Nickname des Spielers
     * @return
     */
    public boolean entferneSpieler(String nickname) {
        return spieler.removeIf(s -> s.getNickname().equals(nickname));
    }

    /**
     * Startet das Spiel und setzt den richtigen Status
     */
    public void starteSpiel() {
        this.isStarted = true;
        this.turnier = new Turnier(spieler);
        this.status = Spielstatus.TRUMPF_WIRD_GEZOGEN;
        System.out.println("Spiel gestartet mit " + spieler.size() + " Spielern. Trumpf wird gezogen.");

    }

    public Spielstatus getStatus() {
        return status;
    }

    public void setStatus(Spielstatus status) {
        this.status = status;
    }

    // Getter/Setter
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPasswort() {
        return passwort;
    }

    public boolean isStarted() {
        return isStarted;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public List<Spieler> getSpieler() {
        return spieler;
    }

    public Partie getPartie() {
        return partie;
    }

    public Turnier getTurnier() {
        return turnier;
    }

    public boolean isBereit(String nickname) {
        return bereitMap.getOrDefault(nickname, false);
    }

    public Map<Integer, String> getPlatzMap() {
        return platzMap;
    }

    public void setPlatzMap(Map<Integer, String> platzMap) {
        this.platzMap = platzMap;
    }

    public void setBereitStatus(String nickname, boolean status) {
        bereitMap.put(nickname, status);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPasswort(String passwort) {
        this.passwort = passwort;
    }

    public void setStarted(boolean started) {
        isStarted = started;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setSpieler(List<Spieler> spieler) {
        this.spieler = spieler;
    }

    public void setPartie(Partie partie) {
        this.partie = partie;
    }

    public void setTurnier(Turnier turnier) {
        this.turnier = turnier;
    }
}
