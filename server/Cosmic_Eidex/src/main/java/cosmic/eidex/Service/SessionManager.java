package cosmic.eidex.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Die {@code SessionManager}-Klasse verwaltet statisch alle aktiven Sitzungen (Sessions) von Spielern.
 */
public class SessionManager {

    private static Map<String, String> activeSessions = new HashMap<>();

    public static void saveToken(String nickname, String token) {
        activeSessions.put(nickname, token);
    }

    public static String getToken(String nickname) {
        return activeSessions.get(nickname);
    }

    public static void removeToken(String nickname) {
        activeSessions.remove(nickname);
    }

    public static String getNickname(){
        return activeSessions.keySet().iterator().next();
    }

    public static boolean isLoggedIn(String nickname) {
        return activeSessions.containsKey(nickname);
    }

    /**
     * Gibt alle aktiven Sessions aus.
     */
    public static void printAllActiveSessions() {
        if (activeSessions.isEmpty()) {
            System.out.println("Keine aktiven Sessions.");
            return;
        }

        System.out.println("Aktive Sessions:");
        for (Map.Entry<String, String> entry : activeSessions.entrySet()) {
            System.out.println("Nickname: " + entry.getKey() + " → Token: " + entry.getValue());
        }
    }


}
