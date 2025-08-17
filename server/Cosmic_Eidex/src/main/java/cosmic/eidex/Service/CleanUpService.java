package cosmic.eidex.Service;


import cosmic.eidex.repository.SpielerRepository;
import cosmic.eidex.repository.SpielraumRepository;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

/**
 * Klasse um bei Server ShutDown die Datenbank von Daten die nicht gespeichert werden muessen zu bereinigen.
 */
@Component
public class CleanUpService {

    private final SpielerRepository spielerRepository;
    private final SpielraumRepository spielraumRepository;

    public CleanUpService(SpielerRepository spielerRepository,
                          SpielraumRepository spielraumRepository) {
        this.spielerRepository = spielerRepository;
        this.spielraumRepository = spielraumRepository;
    }

    /**
     * Loescht offen gebliebene Spielraeume
     * Loescht alle Bots
     * Setzt RaumId von Spielern auf NULL
     */
    @PreDestroy
    public void onShutdown() {
        System.out.println("Beginne mit loeschen von nicht zu speichernden Daten.");
        spielerRepository.deleteAllNonSpieler();
        spielraumRepository.deleteAllSpielraeume();
        spielerRepository.setRaumIdToNull();
        System.out.println("Datenbank bereinigt. Shutdown");
    }
}
