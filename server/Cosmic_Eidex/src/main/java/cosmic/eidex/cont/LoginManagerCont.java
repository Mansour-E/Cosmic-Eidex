package cosmic.eidex.cont;

import cosmic.eidex.DTO.SpielerDTO;
import cosmic.eidex.Service.LoginManager;
import cosmic.eidex.spielmodell.Spieler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST-Controller für Login, Registrierung und Kontoverwaltung.
 */
@RestController
@RequestMapping("/login")
public class LoginManagerCont{

    @Autowired
    private LoginManager loginManager;

    /**
     * Registriert einen neuen Spieler.
     */
    @PostMapping("/register")
    public Spieler register(@RequestParam String nickname, @RequestParam String passwort, @RequestParam int alter) {
        Spieler spieler = loginManager.registrieren(nickname, passwort, alter);
        return spieler;
    }

    /**
     * Führt Login durch und gibt bei Erfolg Spieler mit Token zurück.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String nickname, @RequestParam String passwort) {
        String token = loginManager.anmelden(nickname, passwort);
        if (token != null) {
            Spieler spieler = loginManager.getSpielerZuToken(token);
            SpielerDTO dto = new SpielerDTO(spieler);
            return ResponseEntity.ok()
                    .header("Authorization", "Bearer " + token)
                    .body(dto);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login fehlgeschlagen");
    }

    /**
     * Loggt den Spieler über Token aus.
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Kein Token");
        }
        String token = authHeader.substring(7);
        loginManager.abmelden(token);
        return ResponseEntity.ok("Logout erfolgreich");
    }

    /**
     * Löscht ein Spielerkonto nach Prüfung von Token und Zugangsdaten.
     */
    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestHeader("Authorization") String authHeader, @RequestParam String nickname, @RequestParam String passwort) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Kein Token");
        }
        String token = authHeader.substring(7);
        Spieler spieler = loginManager.getSpielerZuToken(token);

        if (spieler == null || !spieler.getNickname().equals(nickname)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token ungültig");
        }

        boolean erfolgreich = loginManager.kontoLoeschen(nickname, passwort);

        if (erfolgreich) {
            return ResponseEntity.ok("Konto gelöscht");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Löschen fehlgeschlagen");
        }
    }

    /**
     * Ändert den Nicknamen des Spielers.
     */
    @PostMapping("/changeNickName")
    public ResponseEntity<?> changeNickname(@RequestHeader("Authorization") String authHeader, @RequestParam String nicknameAlt, @RequestParam String nicknameNeu) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Kein Token");
        }
        String token = authHeader.substring(7);
        Spieler spieler = loginManager.getSpielerZuToken(token);
       if (spieler == null) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token ungültig");
        }


        boolean erfolgreich = loginManager.aenderNickName(token, nicknameAlt, nicknameNeu);

        if (erfolgreich) {
            return ResponseEntity.ok("Erfolgreich");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Aendern fehlgeschlagen");
        }

    }

    /**
     * Ändert das Passwort des Spielers.
     */
    @PostMapping("/changePassword")
    public ResponseEntity<?> changePassword(@RequestHeader("Authorization") String authHeader, @RequestParam String passwortNeu, @RequestParam String passwortAlt) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Kein Token");
        }

        String token = authHeader.substring(7);
        Spieler spieler = loginManager.getSpielerZuToken(token);

        if (spieler == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token ungültig");
        }


        boolean erfolgreich = loginManager.aenderPasswort(token, spieler.getNickname(), passwortNeu, passwortAlt);

        if (erfolgreich) {
            return ResponseEntity.ok("Erfolgreich");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Aendern fehlgeschlagen");
        }
    }

    /**
     * Prüft, ob ein Spielername bereits existiert.
     */
    @PostMapping("/findExisting")
    public Boolean findExisting(@RequestParam String nickname) {
        return loginManager.findPlayer(nickname);
    }
}
