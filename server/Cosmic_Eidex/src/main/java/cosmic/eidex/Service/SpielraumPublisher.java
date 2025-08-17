package cosmic.eidex.Service;

import cosmic.eidex.Lobby.Spielraum;
import cosmic.eidex.Service.SpielNachrichten.InfoNachricht;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service-Komponente, die Spielraum-Updates über WebSocket an Clients sendet.
 */
@Service
public class SpielraumPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    public SpielraumPublisher(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Sendet eine Liste von Spielraum-Namen über das Topic {@code /topic/spielraum}.
     * @param spielraeume die Liste der Spielräume, deren Namen übertragen werden sollen
     */
    public void sendSpielraumUpdate(List<Spielraum> spielraeume) {
        List<String> namen = spielraeume.stream()
                .map(Spielraum::getName)
                .toList();

        messagingTemplate.convertAndSend("/topic/spielraum", namen);
    }

    /**
     * Sendet eine SpielraumInfo über das Topic {/topic/spielrauminfo}
     * @param raumId die ID des Raumes an de dei Info get
     * @param nachricht vom Typ Infonachricht
     */
    public void sendeInfoNachricht(String raumId, InfoNachricht nachricht) {
        messagingTemplate.convertAndSend("/topic/spielrauminfo/" + raumId, nachricht);

    }

}
