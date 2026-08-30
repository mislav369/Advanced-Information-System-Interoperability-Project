package hr.algebra.aisi.aisijavafxclient;

import hr.algebra.aisi.aisijavafxclient.model.NetflixShow;
import hr.algebra.aisi.aisijavafxclient.service.ApiService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class EditController implements Initializable {

    @FXML private TextField titleField;
    @FXML private ComboBox<String> showTypeCombo;
    @FXML private TextField directorField;
    @FXML private TextField castMembersField;
    @FXML private TextField countryField;
    @FXML private TextField dateAddedField;
    @FXML private TextField releaseYearField;
    @FXML private TextField ratingField;
    @FXML private TextField durationField;
    @FXML private TextField listedInField;
    @FXML private TextArea descriptionArea;

    private final ApiService apiService = new ApiService();
    private NetflixShow showToUpdate;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        showTypeCombo.getItems().addAll("MOVIE", "TV_SHOW");
    }

    public void setShow(NetflixShow show) {
        this.showToUpdate = show;
    }

    public void fillFields() {
        titleField.setText(showToUpdate.getTitle());
        showTypeCombo.setValue(showToUpdate.getShowType());
        directorField.setText(showToUpdate.getDirector());
        castMembersField.setText(showToUpdate.getCastMembers());
        countryField.setText(showToUpdate.getCountry());
        dateAddedField.setText(showToUpdate.getDateAdded());
        releaseYearField.setText(
                showToUpdate.getReleaseYear() == null ? "" : String.valueOf(showToUpdate.getReleaseYear()));
        ratingField.setText(showToUpdate.getRating());
        durationField.setText(showToUpdate.getDuration());
        listedInField.setText(showToUpdate.getListedIn());
        descriptionArea.setText(showToUpdate.getDescription());
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
            show.setDateAdded(dateAddedField.getText().trim());

            try {
                show.setReleaseYear(Integer.parseInt(releaseYearField.getText().trim()));
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Release year must be a number.");
                return;
            }

            if (apiService.updateShow(showToUpdate.getId(), show)) {
                showAlert(Alert.AlertType.INFORMATION, "Show successfully updated.");
                openMainScreen();
            } else {
                showAlert(Alert.AlertType.ERROR, "Failed to update the show.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Please check the input fields.");
        }
    }

    @FXML
    protected void onBackButtonClick() {
        try {
            openMainScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openMainScreen() throws Exception {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(loader.load(), 900, 600);
        HelloApplication.getCentralStage().setTitle("Netflix Shows");
        HelloApplication.getCentralStage().setScene(scene);
        HelloApplication.getCentralStage().show();
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
