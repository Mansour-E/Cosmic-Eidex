package cosmic.eidex.Service;

import cosmic.eidex.Lobby.Nachricht;
import cosmic.eidex.repository.ChatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ChatServiceTest {

    private ChatRepository chatRepository;
    private ChatService chatService;

    @BeforeEach
    void setUp() {
        chatRepository = mock(ChatRepository.class);
        chatService = new ChatService(chatRepository);
    }

    @Test
    void testGetMessagesLast24Hours() {
        // Nachrichten mit konkreten Zeitstempeln erzeugen
        Nachricht n1 = new Nachricht("Alice", "Hi!");
        n1.setZeitstempel(OffsetDateTime.now().minusHours(2));
        Nachricht n2 = new Nachricht("Bob", "Hallo!");
        n2.setZeitstempel(OffsetDateTime.now().minusHours(1));

        List<Nachricht> expectedMessages = Arrays.asList(n1, n2);

        // Mock das Repository so, dass es die Liste zurückgibt
        when(chatRepository.findByZeitstempelAfter(any(OffsetDateTime.class))).thenReturn(expectedMessages);

        // Methode testen
        List<Nachricht> result = chatService.getMessagesLast24Hours();

        // Ergebnis prüfen
        assertEquals(expectedMessages, result);

        // Überprüfen, ob die Methode mit einem OffsetDateTime-Parameter aufgerufen wurde
        verify(chatRepository).findByZeitstempelAfter(any(OffsetDateTime.class));
    }

    @Test
    void testGetMessagesLast24HoursReturnsEmptyList() {
        when(chatRepository.findByZeitstempelAfter(any(OffsetDateTime.class))).thenReturn(List.of());

        List<Nachricht> result = chatService.getMessagesLast24Hours();

        assertTrue(result.isEmpty());
        verify(chatRepository).findByZeitstempelAfter(any(OffsetDateTime.class));
    }
}
