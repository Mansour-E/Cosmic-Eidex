package cosmic.eidex.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;



/**
 * Standard Config fuer WebSocket
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     *Configuration fuer MessageBroker
     * /topic Adresse: Server sendet an alle Clients die diese abonniert haben
     * /app Adresse: Wird fuer Client Nachrichten verwendet
     * @param config Konfiguration des Brokers
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    /**
     *Registrierung von STOMP Endpunkten
     * /chat : fuer globalen Chat
     * /bestenliste : fuer globale Bestenliste
     * /spielraum : fuer die anzeige der spielraume
     * /imspielraum : fuer verschiedenes im spielraum
     * /ende : fuer das Ende des Spiels
     * /spielraumchat : fuer den Chat im Spielraum
     * /spielraumzug : fuer zuege im spielfluss
     * /spielraumstart : fuer den start des spiels
     * /spielraumtrumpf : fuer den trumpf
     * /spielraumausteilen : fuer das austeilen der karten
     * /spielraumdruecken : fuer das druecken der karten
     * /spielraumstatus : fuer den spielstatus
     * /spielraumgueltige : fuer die gueltigen Karten
     * /spielrauminfo : fuer allgemeine infos des spielraumes
     * /spielraumrundepunkte : fuer die punkte der runde
     * /spielraumpartiepunkte : fuer die punkte der partie
     * /spielraumendergebnis : fuer das ergebnis des Spiels
     * @param registry fuer registrierung der Endpunkte
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/chat").setAllowedOriginPatterns("*");
        registry.addEndpoint("/bestenliste").setAllowedOriginPatterns("*");
        registry.addEndpoint("/spielraum").setAllowedOriginPatterns("*");
        registry.addEndpoint("/imspielraum").setAllowedOriginPatterns("*");
        registry.addEndpoint("/ende").setAllowedOriginPatterns("*");
        registry.addEndpoint("/spielraumchat").setAllowedOriginPatterns("*");
        registry.addEndpoint("/spielraumzug").setAllowedOriginPatterns("*");
        registry.addEndpoint("/spielraumstart").setAllowedOriginPatterns("*");
        registry.addEndpoint("/spielraumtrumpf").setAllowedOriginPatterns("*");
        registry.addEndpoint("/spielraumausteilen").setAllowedOriginPatterns("*");
        registry.addEndpoint("/spielraumdruecken").setAllowedOriginPatterns("*");
        registry.addEndpoint("/spielraumstatus").setAllowedOriginPatterns("*");
        registry.addEndpoint("/spielraumgueltige").setAllowedOriginPatterns("*");
        registry.addEndpoint("/spielrauminfo").setAllowedOriginPatterns("*");
        registry.addEndpoint("/spielraumrundepunkte").setAllowedOriginPatterns("*");
        registry.addEndpoint("/spielraumpartiepunkte").setAllowedOriginPatterns("*");
        registry.addEndpoint("/spielraumendergebnis").setAllowedOriginPatterns("*");

    }
}
