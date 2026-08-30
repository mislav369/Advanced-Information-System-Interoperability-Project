package hr.algebra.aisi.aisijavafxclient;

import hr.algebra.aisi.aisijavafxclient.model.NetflixShow;
import hr.algebra.aisi.aisijavafxclient.service.ApiService;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class HelloController implements Initializable {

    @FXML private TableView<NetflixShow> showsTable;
    @FXML private TableColumn<NetflixShow, Long> idColumn;
    @FXML private TableColumn<NetflixShow, String> titleColumn;
    @FXML private TableColumn<NetflixShow, String> typeColumn;
    @FXML private TableColumn<NetflixShow, String> directorColumn;
    @FXML private TableColumn<NetflixShow, String> countryColumn;
    @FXML private TableColumn<NetflixShow, Integer> releaseYearColumn;
    @FXML private TableColumn<NetflixShow, String> ratingColumn;

    private final ApiService apiService = new ApiService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("showType"));
        directorColumn.setCellValueFactory(new PropertyValueFactory<>("director"));
        countryColumn.setCellValueFactory(new PropertyValueFactory<>("country"));
        releaseYearColumn.setCellValueFactory(new PropertyValueFactory<>("releaseYear"));
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));

        showsTable.setRowFactory(tableView -> {
            TableRow<NetflixShow> row = new TableRow<>();
            ContextMenu rowMenu = new ContextMenu();

            MenuItem editItem = new MenuItem("Edit");
            editItem.setOnAction(event -> openEditScreen(row.getItem()));

            MenuItem deleteItem = new MenuItem("Delete");
            deleteItem.setOnAction(event -> deleteShow(row.getItem()));

            rowMenu.getItems().addAll(editItem, deleteItem);

            row.contextMenuProperty().bind(
                    Bindings.when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(rowMenu));
            return row;
        });

        loadShows();
    }

    @FXML
    protected void onRefreshButtonClick() {
        loadShows();
    }

    @FXML
    protected void onAddButtonClick() {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("add-view.fxml"));
            Scene scene = new Scene(loader.load(), 900, 600);
            HelloApplication.getCentralStage().setTitle("Add Netflix Show");
            HelloApplication.getCentralStage().setScene(scene);
            HelloApplication.getCentralStage().show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openEditScreen(NetflixShow show) {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("edit-view.fxml"));
            Scene scene = new Scene(loader.load(), 900, 600);
            EditController controller = loader.getController();
            controller.setShow(show);
            controller.fillFields();
            HelloApplication.getCentralStage().setTitle("Edit Netflix Show");
            HelloApplication.getCentralStage().setScene(scene);
            HelloApplication.getCentralStage().show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteShow(NetflixShow show) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setContentText("Delete \"" + show.getTitle() + "\"?");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    if (apiService.deleteShow(show.getId())) {
                        loadShows();
                    } else {
                        Alert error = new Alert(Alert.AlertType.ERROR);
                        error.setContentText("Failed to delete the show.");
                        error.showAndWait();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void loadShows() {
        try {
            List<NetflixShow> shows = apiService.getAllShows();
            ObservableList<NetflixShow> data = FXCollections.observableArrayList(shows);
            showsTable.setItems(data);
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Failed to load shows.");
            alert.showAndWait();
        }
    }

    @FXML
    protected void onBackupButtonClick() {
        try {
            String fileName = apiService.createBackup();
            if (fileName != null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Backup created: " + fileName);
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Failed to create backup.");
                alert.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onRestoreButtonClick() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText(null);
        dialog.setContentText("Enter backup file name:");
        dialog.showAndWait().ifPresent(fileName -> {
            if (fileName.isBlank()) {
                return;
            }
            try {
                if (apiService.restoreBackup(fileName.trim())) {
                    loadShows();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setContentText("Database restored from: " + fileName);
                    alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Failed to restore backup.");
                    alert.showAndWait();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}