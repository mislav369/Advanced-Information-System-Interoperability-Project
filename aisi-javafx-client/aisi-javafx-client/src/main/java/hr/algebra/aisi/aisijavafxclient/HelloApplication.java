package hr.algebra.aisi.aisijavafxclient;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    private static Stage centralStage;

    @Override
    public void start(Stage stage) throws IOException {
        centralStage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 300);
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }

    public static Stage getCentralStage() {
        return centralStage;
    }

    public static void main(String[] args) {
        launch();
    }
}
