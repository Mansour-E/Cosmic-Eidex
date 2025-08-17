package cosmic.eidex.gui;

import cosmic.eidex.gui.ControllerFX.LobbyController;
import cosmic.eidex.gui.Fx.StageReadyEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Verwaltet Szenenwechsel in der JavaFX-Anwendung.
 * Bindet Spring mit JavaFX zusammen.
 */
@Component
public class StageManager implements ApplicationListener<StageReadyEvent> {

    private final ApplicationContext applicationContext;
    private Stage primaryStage;

    @Autowired
    public StageManager(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * Wird aufgerufen, wenn die JavaFX-Stage bereit ist.
     * Setzt die Startszene.
     */
    @Override
    public void onApplicationEvent(StageReadyEvent event) {
        this.primaryStage = event.getStage();
        switchScene("/login.fxml"); // Start-View

    }


    /**
     * Lädt eine neue Szene anhand des FXML-Pfads.
     */
    public void switchScene(String fxmlPath) {
        try {
            Resource resource = applicationContext.getResource("classpath:" + fxmlPath);
            FXMLLoader loader = new FXMLLoader(resource.getURL());
            loader.setControllerFactory(applicationContext::getBean);
            Parent root = loader.load();
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Setzt eine Szene direkt ohne FXML-Laden.
     */
    public void setScene(Parent root) {
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    /**
     * Lädt eine Szene und gibt den zugehörigen Controller zurück.
     */
    public <T> T switchSceneAndReturnController(String fxmlPath) {
        try {
            Resource resource = applicationContext.getResource("classpath:" + fxmlPath);
            FXMLLoader loader = new FXMLLoader(resource.getURL());
            loader.setControllerFactory(applicationContext::getBean);
            Parent root = loader.load();
            T controller = loader.getController();
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
            return controller;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
