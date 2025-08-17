package cosmic.eidex.Service;

import cosmic.eidex.Lobby.Nachricht;
import cosmic.eidex.repository.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Service Klasse fuer Chat fuer bessere Strukturierung und Trennung
 */
@Service
public class ChatService {

    private final ChatRepository chatRepository;

    @Autowired
    public ChatService(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    /**
     * Ruft Methode aus ChatRepository auf, um Nachrichten der letzten 24h zu finden
     * @return Liste mit Nachrichten der letzten 24h
     */
    public List<Nachricht> getMessagesLast24Hours() {
        OffsetDateTime since = OffsetDateTime.now().minusHours(24);
        return chatRepository.findByZeitstempelAfter(since);
    }
}
