package cosmic.eidex;

import cosmic.eidex.gui.Fx;
import javafx.application.Application;
import org.springframework.context.annotation.Configuration;

/**
 * Main zum Starten eines Clients
 */
@Configuration
public class ClientMain {
    public static void main(String[] args) {
        Application.launch(Fx.class, args);
    }
}
