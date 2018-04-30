package View;

import Data.CredentialsJSON;
import Model.Config;
import Model.Credential;
import Model.CredentialsList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.io.File;
import java.util.List;


public class MainViewController
{

    private static Stage stage;
    private Desktop desktop = Desktop.getDesktop();
    private CredentialsList credentialList;
    private Config config;

    @FXML
    private MenuItem openFile;

    @FXML
    private TableView<?> dataTable;

    @FXML
    private MenuItem save;

    @FXML
    private MenuItem newFile;

    @FXML
    private MenuItem close;

    /**
     * method to handle new file creation
     */
    @FXML
    void onNewFile(ActionEvent event) {

    }
    /**
     * method to handle file opening
     */
    @FXML
    void onOpenFile(ActionEvent event) {
        Stage stage = getStage();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open passwords File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("TheCollectorFile", "*.tclt"),
                new FileChooser.ExtensionFilter("All files", "*.*")
        );
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            System.out.println(file.getName());
        }
    }

    /**
     * method to save changes
     */
    @FXML
    void onSaveFile(ActionEvent event) {

    }
    /**
     * method to close file
     */
    @FXML
    void onCloseFile(ActionEvent event) {

    }

    public static Stage getStage() {
        return stage;
    }

    public static void setStage(Stage stage) {
        MainViewController.stage = stage;
    }
}
