package cosmic.eidex.cont;

import cosmic.eidex.Service.SpielNachrichten.SpielraumChatNachricht;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

/**
 * REST-Controller für den Spielraum-Chat.
 */
@Controller
public class SpielraumChatCont {

    /**
     * Empfängt und sendet Chat-Nachrichten im angegebenen Spielraum.
     */
    @MessageMapping("/spielraumchat/{raumId}")
    @SendTo("/topic/spielraumchat/{raumId}")
    public SpielraumChatNachricht handleChatMessage(
            @DestinationVariable Long raumId,
            SpielraumChatNachricht nachricht) {
        nachricht.setRaumId(raumId);
        return nachricht;
    }
}

