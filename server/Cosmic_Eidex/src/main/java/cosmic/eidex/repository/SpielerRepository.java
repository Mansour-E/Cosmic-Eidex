package cosmic.eidex.repository;

import cosmic.eidex.spielmodell.Spieler;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Verbindung zur Datenbank fuer Spieler-Data-Table
 */
public interface SpielerRepository extends JpaRepository<Spieler, Long> {

    /**
     * Sucht einen Spieler mit Namen in dem Table
     * @param nickName Nickname des Spielers der gesucht wird
     * @return Eine optionale Spieler-Instanz kann null sein wenn nicht gefunden
     */
    Optional<Spieler> findByNickname(String nickName);

    /**
     * Sucht nach 10 Spielern mit den meisten SIegen anhand der siege Spalte in dem Table
     * @return Liste von Spielern, welche dann in der Bestenliste angezeigt werden koennen
     */
    List<Spieler> findTop10ByOrderBySiegeDesc();

    /**
     * Löscht alle Bots aus der Spieler Datenbank
     */
    @Transactional
    @Modifying
    @Query("DELETE FROM Spieler s WHERE s.typ <> 'Spieler'")
    void deleteAllNonSpieler();

    /**
     * Aktualisiert die aktuelle Spielraum ID sodass diese wieder leer ist
     */
    @Transactional
    @Modifying
    @Query(value = "UPDATE spieler SET spielraum_id = NULL", nativeQuery = true)
    void setRaumIdToNull();

    /**
     * Aktualisiert den Spieler nach Spielende
     * @param id
     */
    @Transactional
    @Modifying
    @Query(value = "UPDATE spieler SET spielraum_id = NULL WHERE spielraum_id = :id", nativeQuery = true)
    void updateSpielerNachSpielende(@Param("id") Long id);

    /**
     * Löscht alle Bots aus alten Spielraeumen
     * @param id
     */
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM Spieler WHERE typ <> 'Spieler' AND spielraum_id = :id", nativeQuery = true)
    void deleteAllNonSpielerFromOldSSpielraum(@Param("id") Long id);
}
