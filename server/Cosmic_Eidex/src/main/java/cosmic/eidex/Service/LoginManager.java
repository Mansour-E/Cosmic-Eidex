package cosmic.eidex.Service;

import cosmic.eidex.repository.SpielerRepository;
import cosmic.eidex.spielmodell.Spieler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Der {@code LoginManager} verwaltet Registrierungen, Anmeldungen,
 * Sitzungen und Änderungen von Spielerinformationen im System.
 * Er nutzt ein {@code SpielerRepository}, um Spieler dauerhaft zu speichern,
 * und eine In-Memory-Map, um aktive Sessions mit Token zu verwalten.
 */
@Service
public class LoginManager {

    private final SpielerRepository spielerRepository;
    private final Map<String, Spieler> aktiveSessions = new HashMap<>();


    @Autowired
    public LoginManager(SpielerRepository spielerRepository) {
        this.spielerRepository = spielerRepository;
    }

    /**
     * Registriert einen neuen Spieler, sofern der Nickname noch nicht vergeben ist.
     * @param nickn   der gewünschte Nickname
     * @param passwort das gewünschte Passwort
     * @param alter   das Alter des Spielers
     * @return das registrierte Spielerobjekt oder {@code null}, wenn der Nickname bereits existiert
     */
    public Spieler registrieren(String nickn, String passwort, int alter) {
        if (spielerRepository.findByNickname(nickn).isPresent()) {
            return null;
        }
        Spieler spieler = new Spieler(nickn, passwort, alter);
        return spielerRepository.save(spieler);
    }

    /**
     * Meldet einen Spieler an, wenn die Anmeldedaten korrekt sind.
     * @param nickn    der Nickname
     * @param passwort das Passwort
     * @return ein generierter Token bei Erfolg, sonst {@code null}
     */
    public String anmelden(String nickn, String passwort) {
        Optional<Spieler> optSpieler = spielerRepository.findByNickname(nickn);

        if (optSpieler.isPresent() && aktiveSessions.values().stream()
                .anyMatch(spieler -> spieler.getNickname().equals(nickn))) {
            System.out.println("Spieler ist bereits angemeldet.");
            return null;
        }
        
        if (optSpieler.isPresent() && optSpieler.get().getPasswort().equals(passwort)) {
            String token = UUID.randomUUID().toString();
            aktiveSessions.put(token, optSpieler.get());
            optSpieler.get().setToken(token);
            return token;
        }
        return null;
    }

    /**
     * Meldet den Spieler mit dem gegebenen Token ab.
     * @param token der Authentifizierungstoken
     */
    public void abmelden(String token){
        Spieler spieler = getSpielerZuToken(token);
        spieler.setToken(null);
        aktiveSessions.remove(token);
    }

    /**
     * Löscht ein Konto, wenn Nickname und Passwort übereinstimmen.
     * @param nickname  der Nickname
     * @param passwort  das Passwort
     * @return {@code true} bei erfolgreichem Löschen, sonst {@code false}
     */
    public boolean kontoLoeschen(String nickname, String passwort) {
        Optional<Spieler> optSpieler = spielerRepository.findByNickname(nickname);
        if (optSpieler.isPresent() && optSpieler.get().getPasswort().equals(passwort)) {
            spielerRepository.delete(optSpieler.get());
            optSpieler.get().setToken(null);
            aktiveSessions.entrySet().removeIf(entry -> entry.getValue().getNickname().equals(nickname));
            return true;

        }
        return false;
    }

    /**
     * Ändert den Nickname eines Spielers, wenn ein gültiger Token vorhanden ist.
     * @param token        der Authentifizierungstoken
     * @param nicknameAlt  der alte Nickname
     * @param nicknameNeu  der neue Nickname
     * @return {@code true} bei Erfolg, sonst {@code false}
     */
    public boolean aenderNickName(String token, String nicknameAlt, String nicknameNeu){

        Spieler spieler = aktiveSessions.get(token);
        if (spieler != null) {
            aktiveSessions.remove(token);
            spieler.setNickname(nicknameNeu);
            spielerRepository.save(spieler);
            aktiveSessions.put(token, spieler);

            return true;
        }else {
            System.out.println("Spieler " + nicknameAlt + " nicht gefunden");
            return false;
        }
    }

    /**
     * Ändert das Passwort eines Spielers, wenn das alte Passwort korrekt ist.
     * @param token         der Authentifizierungstoken
     * @param nickname      der Nickname
     * @param passwortNeu   das neue Passwort
     * @param passwortAlt   das alte Passwort
     * @return {@code true} bei Erfolg, sonst {@code false}
     */
    public boolean aenderPasswort(String token, String nickname, String passwortNeu, String passwortAlt){
        Spieler spieler = aktiveSessions.get(token);
        if (spieler != null && spieler.getPasswort().equals(passwortAlt)) {
            aktiveSessions.remove(token);
            spieler.setPasswort(passwortNeu);
            spielerRepository.save(spieler);
            aktiveSessions.put(token, spieler);
            return true;
        }else {
            System.out.println("Spieler " + nickname + " nicht gefunden");
            return false;
        }
    }

    /**
     * Gibt den Spieler zurück, der mit dem gegebenen Token angemeldet ist.
     * @param token der Authentifizierungstoken
     * @return der Spieler oder {@code null}, wenn kein Spieler gefunden wurde
     */
    public Spieler getSpielerZuToken(String token) {
        return aktiveSessions.get(token);
    }

    /**
     * Gibt alle derzeit aktiven Sessions aus.
     */
    public void printAlleAktiven() {
        if (aktiveSessions.isEmpty()) {
            System.out.println("Keine aktiven Sessions.");
            return;
        }

        System.out.println("Aktive Sessions:");
        for (Map.Entry<String, Spieler> entry : aktiveSessions.entrySet()) {
            System.out.println("Token: " + entry.getKey() + " → Spieler: " + entry.getValue().getNickname());
        }
    }

    /**
     * Sucht den Token für einen gegebenen Spieler.
     * @param spieler der Spieler
     * @return der zugehörige Token oder {@code null}, wenn nicht vorhanden
     */
    public String getTokenZuSpieler(Spieler spieler) {
        return aktiveSessions.get(spieler.getNickname()).getToken();
    }

    /**
     * Überprüft, ob ein Token aktuell gültig ist (d.h. eine aktive Session existiert).
     * @param token der Authentifizierungstoken
     * @return {@code true}, wenn gültig, sonst {@code false}
     */
    public boolean istTokenGueltig(String token) {
        return aktiveSessions.containsKey(token);
    }

    /**
     * Prüft, ob ein Spieler mit dem gegebenen Nickname existiert.
     * @param nickname der zu prüfende Nickname
     * @return {@code true}, wenn Spieler existiert, sonst {@code false}
     */
    public boolean findPlayer (String nickname){
        Optional<Spieler> optSpieler = spielerRepository.findByNickname(nickname);
        return optSpieler.isPresent();
    }
}