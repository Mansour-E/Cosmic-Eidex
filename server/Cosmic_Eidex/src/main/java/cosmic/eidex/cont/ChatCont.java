package cosmic.eidex.cont;

import cosmic.eidex.Lobby.Nachricht;
import cosmic.eidex.Service.ChatService;
import cosmic.eidex.repository.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller für den globalen Chat (WebSocket und REST).
 */
@RestController
public class ChatCont {

    private ChatRepository chatRepository;
    private ChatService chatService;

    /**
     * Konstruktor für ChatCont
     * @param chatRepository fuer Zugriff auf Datenbank fuer den Chat
     * @param chatService fuer Zugriff auf die Service Klasse
     */
    @Autowired
    public ChatCont(ChatRepository chatRepository, ChatService chatService) {
        this.chatRepository = chatRepository;
        this.chatService = chatService;
    }


    /**
     * Empfängt und speichert Chatnachrichten, sendet sie über WebSocket weiter.
     * @param nachricht die empfangene Nachricht
     * @return die gespeicherte Nachricht
     */
    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public Nachricht send(Nachricht nachricht) {

        if (nachricht.getZeitstempel() == null) {
            nachricht.setZeitstempel(java.time.OffsetDateTime.now());
        }

        chatRepository.save(nachricht);

        return nachricht;
    }

    /**
     * Gibt die Chatnachrichten der letzten 24 Stunden zurück.
     * @return Liste von Nachrichten
     */
    @GetMapping("/chat/history")
    public List<Nachricht> getChatHistoryLast24h() {
        return chatService.getMessagesLast24Hours();
    }
}
