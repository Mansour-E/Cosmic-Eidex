package cosmic.eidex.Service.SpielNachrichten;

import cosmic.eidex.Lobby.Nachricht;

/**
 * Nachricht um Chatnachrichten innerhalb eines Spielraums zu senden
 */
public class SpielraumChatNachricht extends Nachricht implements SpielraumNachricht {

    private Long raumId;

    public SpielraumChatNachricht(Long raumId, String sender, String inhalt) {
        super(sender, inhalt);
        this.raumId = raumId;
    }

    @Override
    public Long getRaumId() {
        return raumId;
    }

    public void setRaumId(Long raumId) {
        this.raumId = raumId;
    }
}
