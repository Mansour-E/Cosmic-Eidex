package cosmic.eidex.guiLogik;

import cosmic.eidex.Service.ErgebnisStompClient;
import cosmic.eidex.gui.StageManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ErgebnisControllerLogikTest {
    ErgebnisControllerLogik erg;
    ErgebnisStompClient stompClient1;
    StageManager stageMock;

    @BeforeEach
    void setUp() {
        stageMock = mock(StageManager.class);
        erg = new ErgebnisControllerLogik(stageMock) {
            @Override
            protected ErgebnisStompClient createErgebnisStompClient(Long id) {
                stompClient1 = mock(ErgebnisStompClient.class);
                return stompClient1;
            }
        };
    }

    @Test
    void setRaumId() {
        ErgebnisStompClient oldClient = mock(ErgebnisStompClient.class);
        erg.setStompClient(oldClient);
        erg.setRaumId(123L);

        verify(oldClient).disconnect();
        verify(stompClient1).connect();
        assertEquals(stompClient1, erg.getStompClient());
    }

    @Test
    void setRaumId_withoutOldClient_connectsNewClient() {
        erg.setStompClient(null);
        erg.setRaumId(456L);
        verify(stompClient1).connect();
        assertEquals(stompClient1, erg.getStompClient());
    }

    @Test
    void setRaumId_whenCreateErgebnisStompClientThrowsException() {
        ErgebnisControllerLogik ergWithException = new ErgebnisControllerLogik(stageMock) {
            @Override
            protected ErgebnisStompClient createErgebnisStompClient(Long id) throws Exception {
                throw new Exception("Test Exception");
            }
        };
        ergWithException.setStompClient(null);
        ergWithException.setRaumId(999L);
    }
}