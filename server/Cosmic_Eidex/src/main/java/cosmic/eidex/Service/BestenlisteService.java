package cosmic.eidex.Service;

import cosmic.eidex.spielmodell.Spieler;
import cosmic.eidex.repository.SpielerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

/**
 * Service Klasse fuer Bestenliste fuer bessere Strukturierung und Trennung
 */
@Service
public class BestenlisteService {

    private final SpielerRepository spielerRepository;

    @Autowired
    public BestenlisteService(SpielerRepository spielerRepository) {
        this.spielerRepository = spielerRepository;
    }

    /**
     * Ruft Methode aus SpielerRepository auf um top 10 der Spieler zu finden
     * @return Liste von Spielern (10 mit hoechsten Siegen)
     */
    public List<Spieler> getTop10Spieler() {
        return spielerRepository.findTop10ByOrderBySiegeDesc().stream()
                .filter(s -> !s.isBot()).toList();

    }

}