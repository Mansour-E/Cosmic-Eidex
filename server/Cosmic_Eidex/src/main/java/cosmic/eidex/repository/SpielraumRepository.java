package cosmic.eidex.repository;

import cosmic.eidex.Lobby.Spielraum;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Verbindung zur Datenbank fuer Spielraum-Data-Table
 */
public interface SpielraumRepository extends JpaRepository<Spielraum, Long> {

    /**
     * Sucht einen Spielraum anhand des Namens
     * @param name Name des SPielraums der gesucht wird
     * @return Eine Optionale Spieleraum Instanz, kann null sein wenn nicht gefunden
     */
    Optional<Spielraum> findByName(String name);

    /**
     * Sucht einen Spielraum anhand des Namens und schaut nur ob er existiert
     * @param name Name des Spielraums der gesucht wird
     * @return Boolean: True wenn existiert, False wenn nicht
     */
    boolean existsByName(String name);

    /**
     * Sucht Spieler anhand ihrer Id
     * @param id
     * @return
     */
    @EntityGraph(attributePaths = "spieler")
    Optional<Spielraum> findWithSpielerById(Long id);

    /**
     * Löscht alle Spielraeume
     */
    @Transactional
    @Modifying
    @Query("DELETE FROM Spielraum")
    void deleteAllSpielraeume();

    /**
     * Löscht anhand der Id
     * @param id Raum-Id
     */
    @Transactional
    void deleteById(Long id);
}
