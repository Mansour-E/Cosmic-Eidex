package cosmic.eidex.guiLogik;

import cosmic.eidex.Service.StompClient;
import javafx.application.Platform;
import javafx.scene.control.ListView;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BestenlisteControllerLogikTest {

    BestenlisteControllerLogik bcl;
    StompClient stompClientMock;

    @BeforeAll
    public static void initJFX() throws InterruptedException {
        if (!Platform.isFxApplicationThread() && !Platform.isImplicitExit()) {
            final CountDownLatch latch = new CountDownLatch(1);
            Platform.startup(() -> {
                Platform.setImplicitExit(false);
                latch.countDown();
            });
            latch.await();
        }
    }

    @BeforeEach
    void setUp() {
        bcl = new BestenlisteControllerLogik();
        stompClientMock = mock(StompClient.class);
        bcl.setStompClient(stompClientMock);
        bcl.setBestenliste(new ListView<>());
        Platform.setImplicitExit(true);
    }

    @Test
    void initialize() {
        BestenlisteControllerLogik spy = spy(bcl);
        doAnswer(invocation -> {
            stompClientMock.connect();
            return null;
        }).when(spy).connectToWebSocket();
        spy.initialize();
        verify(spy, times(1)).connectToWebSocket();
        verify(stompClientMock, times(1)).connect();
    }

    @Test
    void connectToWebSocket() throws Exception {
        StompClient mockClient = mock(StompClient.class);

        BestenlisteControllerLogik controller = new BestenlisteControllerLogik() {
            @Override
            protected StompClient createStompClient() {
                return mockClient;
            }
        };

        controller.setBestenliste(new ListView<>());
        controller.connectToWebSocket();
        verify(mockClient).connect();
        assertEquals(mockClient, controller.getStompClient());
    }

    @Test
    void connectToWebSocket_whenFactoryThrowsException_doesNotThrowOutward() {
        BestenlisteControllerLogik controller = new BestenlisteControllerLogik() {
            @Override
            protected StompClient createStompClient() throws Exception {
                throw new RuntimeException("Fehler");
            }
        };
        assertDoesNotThrow(controller::connectToWebSocket);
    }
}