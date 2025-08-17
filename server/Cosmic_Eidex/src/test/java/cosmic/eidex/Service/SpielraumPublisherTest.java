package cosmic.eidex.Service;

import cosmic.eidex.Lobby.Spielraum;
import cosmic.eidex.Service.SpielNachrichten.InfoNachricht;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SpielraumPublisherTest {

    private SimpMessagingTemplate messagingTemplate;
    private SpielraumPublisher spielraumPublisher;

    @BeforeEach
    void setUp() {
        messagingTemplate = mock(SimpMessagingTemplate.class);
        spielraumPublisher = new SpielraumPublisher(messagingTemplate);
    }


    @Test
    void TestSendSpielraumUpdate() {
        Spielraum spielraum1 = mock(Spielraum.class);
        Spielraum spielraum2 = mock(Spielraum.class);
        when(spielraum1.getName()).thenReturn("RaumA");
        when(spielraum2.getName()).thenReturn("RaumB");

        List<Spielraum> spielraums = List.of(spielraum1, spielraum2);
        spielraumPublisher.sendSpielraumUpdate(spielraums);
        ArgumentCaptor<List<String>> payload = ArgumentCaptor.forClass(List.class);
        verify(messagingTemplate, times(1)).convertAndSend(eq("/topic/spielraum"), payload.capture());
        assertEquals(List.of("RaumA", "RaumB"), payload.getValue());
    }

    @Test
    void testSendSpielraumUpdateLeer() {
        spielraumPublisher.sendSpielraumUpdate(Collections.emptyList());
        verify(messagingTemplate, times(1)).convertAndSend("/topic/spielraum", Collections.emptyList());
    }

    @Test
    void testSendeInfonachricht() {
        InfoNachricht info = new InfoNachricht(123L,"Infotyp A", "Wichtige Info");
        spielraumPublisher.sendeInfoNachricht("123",info);
        ArgumentCaptor<InfoNachricht> payload = ArgumentCaptor.forClass(InfoNachricht.class);
        verify(messagingTemplate, times(1)).convertAndSend(eq("/topic/spielrauminfo/123"), payload.capture());
        assertEquals(info, payload.getValue());
    }
}