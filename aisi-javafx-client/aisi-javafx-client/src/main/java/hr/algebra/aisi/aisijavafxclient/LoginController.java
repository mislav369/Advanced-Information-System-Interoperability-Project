package hr.algebra.aisi.aisijavafxclient;

import hr.algebra.aisi.aisijavafxclient.service.ApiService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    private final ApiService apiService = new ApiService();

    @FXML
    protected void onLoginButtonClick() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        if (username.isBlank() || password.isBlank()) {
            messageLabel.setText("Please enter username and password.");
            return;
        }
        try {
            if (apiService.login(username, password)) {
                openMainScreen();
            } else {
                messageLabel.setText("Invalid username or password.");
            }
        } catch (Exception e) {
            messageLabel.setText("Error connecting to the server.");
        }
    }

    private void openMainScreen() throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 900, 600);
        HelloApplication.getCentralStage().setTitle("Netflix Shows");
        HelloApplication.getCentralStage().setScene(scene);
        HelloApplication.getCentralStage().show();
    }
}