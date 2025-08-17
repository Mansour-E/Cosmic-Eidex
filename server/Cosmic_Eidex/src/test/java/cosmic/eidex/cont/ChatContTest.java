package cosmic.eidex.cont;

import cosmic.eidex.Lobby.Nachricht;
import cosmic.eidex.Service.ChatService;
import cosmic.eidex.cont.ChatCont;
import cosmic.eidex.repository.ChatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChatContTest {

    @Mock
    private ChatRepository chatRepository;

    @Mock
    private ChatService chatService;

    @InjectMocks
    private ChatCont chatCont;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        chatCont = new ChatCont(chatRepository, chatService);
    }

    @Test
    void testSend_WithTimestamp() {
        Nachricht nachricht = new Nachricht();
        OffsetDateTime now = OffsetDateTime.now();
        nachricht.setZeitstempel(now);

        Nachricht result = chatCont.send(nachricht);

        verify(chatRepository, times(1)).save(nachricht);
        assertEquals(now, result.getZeitstempel());
    }

    @Test
    void testSend_WithoutTimestamp() {
        Nachricht nachricht = new Nachricht();
        nachricht.setZeitstempel(null);
        // Zeitstempel = null muss explizit gesetzt werden

        Nachricht result = chatCont.send(nachricht);

        verify(chatRepository, times(1)).save(nachricht);
        assertEquals(result.getZeitstempel(), nachricht.getZeitstempel());
        assertNotNull(result.getZeitstempel());
    }

    @Test
    void testGetChatHistoryLast24h() {
        List<Nachricht> mockList = List.of(new Nachricht(), new Nachricht());
        when(chatService.getMessagesLast24Hours()).thenReturn(mockList);

        List<Nachricht> result = chatCont.getChatHistoryLast24h();

        assertEquals(mockList, result);
        verify(chatService).getMessagesLast24Hours();
    }
}
