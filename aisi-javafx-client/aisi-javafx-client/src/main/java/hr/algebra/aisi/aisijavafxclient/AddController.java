package hr.algebra.aisi.aisijavafxclient;

import hr.algebra.aisi.aisijavafxclient.model.NetflixShow;
import hr.algebra.aisi.aisijavafxclient.service.ApiService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import java.net.URL;
import java.util.ResourceBundle;

public class AddController implements Initializable {

    @FXML
    private TextField titleField;
    @FXML
    private ComboBox<String> showTypeCombo;
    @FXML
    private TextField directorField;
    @FXML
    private TextField castMembersField;
    @FXML
    private TextField countryField;
    @FXML
    private TextField dateAddedField;
    @FXML
    private TextField releaseYearField;
    @FXML
    private TextField ratingField;
    @FXML
    private TextField durationField;
    @FXML
    private TextField listedInField;
    @FXML
    private TextArea descriptionArea;


    private final ApiService apiService = new ApiService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        showTypeCombo.getItems().addAll("MOVIE", "TV_SHOW");
    }

    @FXML
    protected void onSaveButtonClick() {
        if (titleField.getText().isBlank()) {
            showAlert(Alert.AlertType.ERROR, "Title is required.");
            return;
        }

        if (showTypeCombo.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Type is required.");
            return;
        }

        if (releaseYearField.getText().isBlank()) {
            showAlert(Alert.AlertType.ERROR, "Release year is required.");
            return;
        }

        if (dateAddedField.getText().isBlank()) {
            showAlert(Alert.AlertType.ERROR, "Date added is required.");
            return;
        }

        try {
            NetflixShow show = new NetflixShow();
            show.setTitle(titleField.getText());
            show.setShowType(showTypeCombo.getValue());
            show.setDirector(directorField.getText());
            show.setCastMembers(castMembersField.getText());
            show.setCountry(countryField.getText());
            show.setRating(ratingField.getText());
            show.setDuration(durationField.getText());
            show.setListedIn(listedInField.getText());
            show.setDescription(descriptionArea.getText());

            if (!releaseYearField.getText().isBlank()) {
                show.setReleaseYear(Integer.parseInt(releaseYearField.getText().trim()));
            }
            if (!dateAddedField.getText().isBlank()) {
                show.setDateAdded(dateAddedField.getText().trim());
            }

            if (apiService.createShow(show)) {
                showAlert(Alert.AlertType.INFORMATION, "Show successfully saved.");
                openMainScreen();
            } else {
                showAlert(Alert.AlertType.ERROR, "Failed to create the show.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Please check the input fields.");
        }
    }

    @FXML
        protected void onBackButtonClick () {
            try {
                openMainScreen();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void openMainScreen () throws Exception {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
            Scene scene = new Scene(loader.load(), 900, 600);
            HelloApplication.getCentralStage().setTitle("Netflix Shows");
            HelloApplication.getCentralStage().setScene(scene);
            HelloApplication.getCentralStage().show();
        }

        private void showAlert (Alert.AlertType type, String message){
            Alert alert = new Alert(type);
            alert.setContentText(message);
            alert.showAndWait();
        }
    }

