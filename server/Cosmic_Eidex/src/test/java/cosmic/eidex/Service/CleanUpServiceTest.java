package cosmic.eidex.Service;

import cosmic.eidex.repository.SpielerRepository;
import cosmic.eidex.repository.SpielraumRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class CleanUpServiceTest {

    private CleanUpService cleanUpService;
    private SpielerRepository spielerRepository;
    private SpielraumRepository spielraumRepository;

    @BeforeEach
    void setUp() {
        spielerRepository = mock(SpielerRepository.class);
        spielraumRepository = mock(SpielraumRepository.class);
        cleanUpService = new CleanUpService(spielerRepository, spielraumRepository);
    }

    @Test
    void onShutdown() {
        cleanUpService.onShutdown();
        verify(spielerRepository).deleteAllNonSpieler();
        verify(spielraumRepository).deleteAllSpielraeume();
        verify(spielerRepository).setRaumIdToNull();
    }
}