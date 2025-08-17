package cosmic.eidex.repository;

import cosmic.eidex.Lobby.Nachricht;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Verbindung zur Datenbank fuer Chat-Data-Table
 */
public interface ChatRepository extends JpaRepository<Nachricht, Long> {

    /**
     * Sucht alle Nachrichten seit einem Zeitpunkt raus fuer die Chat-History
     * @param seit Zeitwert ab wann gesucht werden soll (bis jetzt)
     * @return Liste an Nachrichten, welche im Chat angezeit werden
     */
    List<Nachricht> findByZeitstempelAfter(OffsetDateTime seit);
}
