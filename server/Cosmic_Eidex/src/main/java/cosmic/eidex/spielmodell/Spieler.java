package cosmic.eidex.spielmodell;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import cosmic.eidex.Lobby.Spielraum;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Die Klasse Spieler repräsentiert einen einzelnen Spieler im Spiel Eidex.
 * Sie enthält Daten zur Identität, Spielfortschritt und Spiellogik-relevanten Attributen.
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "typ", discriminatorType = DiscriminatorType.STRING)
public class Spieler {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nickname;

    private String passwort;

    @Column(name = "spieler_alter")
    private int alter;

    @Column(insertable = false, updatable = false)
    private String typ;

    private int siege;

    @Transient
    private int punkte;

    @Transient
    @JsonIgnore
    private String token;

    @Transient
    @JsonIgnore
    public SpielerHand spielerHand;

    @Transient
    private boolean bereit;

    // Konstruktoren
    public Spieler() {}

    public Spieler(String nickname, String passwort) {
        this.nickname = nickname;
        this.passwort = passwort;
        spielerHand = getSpielerHand();
        token = null;
        siege = 0;
        punkte = 0;
        bereit = false;
    }

    public Spieler(String nickname, String passwort, int alter) {
        this.nickname = nickname;
        this.passwort = passwort;
        this.alter = alter;
        spielerHand = getSpielerHand();
        token = null;
        siege = 0;
        punkte = 0;
        bereit = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Spieler spieler = (Spieler) o;
        return alter == spieler.alter && nickname.equals(spieler.nickname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nickname, alter);
    }

    // Getter und Setter
    public Long getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPasswort() {
        return passwort;
    }

    public void setPasswort(String passwort) {
        this.passwort = passwort;
    }

    /**
     * Ueberprueft anhand von Namen ob Spielerobjekt
     * ein Bot ist
     * @return True wenn Bot, sonst False
     */
    public boolean isBot() {
        return nickname != null && nickname.toLowerCase().contains("bot");
    }

    public String getTyp() {
        return typ;
    }

    public void setTyp(String typ) {
        this.typ = typ;
    }

    public int getAlter() {
        return alter;
    }

    public void setAlter(int alter) {
        this.alter = alter;
    }

    public int getSiege() {
        return siege;
    }

    public void setSiege(int siege) {
        this.siege = siege;
    }

    public int getPunkte() {
        return punkte;
    }

    public void setPunkte(int punkte) {
        this.punkte = punkte;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isBereit() {
        return bereit;
    }

    public void setBereit(boolean bereit) {
        this.bereit = bereit;
    }

    public SpielerHand getSpielerHand() {
        if (this.spielerHand == null) {
            this.spielerHand = new SpielerHand(new ArrayList<>(), this);
        }
        return spielerHand;
    }

    public void setSpielerHand(SpielerHand spielerHand) {
        this.spielerHand = spielerHand;
    }

    /**
     * Gibt eine Nachricht aus.
     * @param text Nachrichtentext
     */
    public void schreibeNachricht(String text) {
        System.out.println(text);
    }

}
