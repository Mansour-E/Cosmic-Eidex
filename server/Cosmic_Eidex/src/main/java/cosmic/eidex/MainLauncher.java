package cosmic.eidex;

import java.util.Arrays;
import java.util.List;

/**
 * Main die in Jar aufgerufen wird. Entscheidet anhand von command line param welche Main aufgerufen wird
 */
public class MainLauncher {
    public static void main(String[] args) {
        List<String> argList = Arrays.asList(args);

        boolean runServer = argList.contains("--server");
        boolean runClient = argList.contains("--client");


        String[] cleanedArgs = argList.stream()
                .filter(arg -> !arg.equalsIgnoreCase("--server") && !arg.equalsIgnoreCase("--client"))
                .toArray(String[]::new);

        if (runServer) {
            System.out.println("Starte Server...");
            ServerMain.main(cleanedArgs);
        } else if (runClient) {
            System.out.println("Starte Client...");
            ClientMain.main(cleanedArgs);
        } else {
            System.err.println("Gib '--server' oder '--client' als Parameter an.");
            System.err.println("z.B. java -jar cosmic-eidex.jar --server");
            System.exit(1);
        }
    }
}
