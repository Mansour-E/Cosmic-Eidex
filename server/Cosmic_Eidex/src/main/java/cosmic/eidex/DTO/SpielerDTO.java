package cosmic.eidex.DTO;

import cosmic.eidex.spielmodell.Spieler;

import java.util.Objects;

/**
 * DTO - Data Transfer Object, das der Controller dem Client übergeben kann.
 * SpielerDTO kann verwendet werden um einfache Informationen des Spielers weiterzugeben.
 */
@SuppressWarnings("unused") // DTO
public class SpielerDTO {

    public String nickname;
    public String getNickname() { return nickname; }

    public Integer siege;
    public Integer getSiege() { return siege; }

    /**
     * Leerer Konstruktor
     */
    public SpielerDTO() {}

    /**
     * Konstruktor für SpielerDTO
     * @param spieler
     */
    public SpielerDTO(Spieler spieler) {
        this.nickname = spieler.getNickname();
        this.siege = spieler.getSiege();
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpielerDTO that = (SpielerDTO) o;
        return nickname.equals(that.nickname) && siege.equals(that.siege);

    }
    @Override
    public int hashCode() {
        return Objects.hash(nickname);
    }
}
