package cosmic.eidex.gui;

import cosmic.eidex.Config.ClientConfig;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Startet die JavaFX-Anwendung und integriert Spring Boot.
 */
public class Fx extends Application {
    private ConfigurableApplicationContext applicationContext;

    /**
     * Initialisiert Spring Boot ohne Webserver.
     */
    @Override
    public void init(){
        applicationContext = new SpringApplicationBuilder(ClientConfig.class)
                .web(WebApplicationType.NONE)
                .run();
    }

    /**
     * Startet die JavaFX GUI und gibt den Stage an Spring weiter.
     */
    @Override
    public void start(Stage stage){
        applicationContext.publishEvent(new StageReadyEvent(stage));
    }

    /**
     * Beendet Spring und JavaFX.
     */
    @Override
    public void stop(){
        applicationContext.close();
        Platform.exit();
    }

    /**
     * Event zum Weiterreichen des JavaFX-Stages an Spring.
     */
    static class StageReadyEvent extends ApplicationEvent {
        public StageReadyEvent(Stage stage) {
            super(stage);
        }

        public Stage getStage() {
            return ((Stage) getSource());
        }
    }
}
