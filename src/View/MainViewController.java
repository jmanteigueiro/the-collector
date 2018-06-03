package View;

import CryptoPackage.Security;
import Model.Credential;
import Model.CredentialsList;
import ViewModel.CredentialsViewModel;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.*;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;


public class MainViewController implements Initializable {

    private Clipboard clipboard = Clipboard.getSystemClipboard();
    private ClipboardContent content = new ClipboardContent();
    private static Stage stage;
    private CredentialsViewModel credentialsViewModel;

    @FXML
    private MenuItem openFile;

    @FXML
    private TableView<Credential> dataTable;

    @FXML
    private MenuItem save;

    @FXML
    private MenuItem newFile;

    @FXML
    private MenuItem close;

    @FXML
    private Button btnAddCredential;

    @FXML
    private TableColumn<Credential, String> website;

    @FXML
    private TableColumn<Credential, String> name;

    /**
     * method used to initialize components
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        File file = null;

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("The Collector");
        alert.setHeaderText("Your personal password safe");
        alert.setContentText("Choose one of the following options:");

        ButtonType buttonTypeCreate = new ButtonType("Create a new file");
        ButtonType buttonTypeLoad = new ButtonType("Load an existing file");

        alert.getButtonTypes().setAll(buttonTypeCreate, buttonTypeLoad);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeLoad) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open a Credentials Database");
            fileChooser.setInitialDirectory(new File("."));
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("TheCollectorFile", "*.cfg")
            );
            file = fileChooser.showOpenDialog(stage);

            if (file == null) {
                file = new File("");
                System.exit(2002);
            }
        }
        else if (result.get() == buttonTypeCreate){
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open a Credentials Database");
            fileChooser.setInitialDirectory(new File("."));
            fileChooser.setInitialFileName("creddb.cfg");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("TheCollectorFile", "*.cfg")
            );
            file = fileChooser.showSaveDialog(stage);

            if (file == null) {
                file = new File("");
                System.exit(2001);
            }

            try {
                if(file.exists())
                    file.delete();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        else {
            System.exit(4000);
        }

        credentialsViewModel = new CredentialsViewModel(file.getAbsolutePath());

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/twofactor.fxml"));
            Parent root = loader.load();
            TwoFactorController twofauth = loader.getController();
            twofauth.open(stage, root, credentialsViewModel);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Save after initialization so the AES symmetric key and the Integrity key change
        credentialsViewModel.saveAllInformation();

        fillDataTable(credentialsViewModel.getCredentialsList());

        website.setCellValueFactory(
                new PropertyValueFactory<>("website"));
        name.setCellValueFactory(
                new PropertyValueFactory<>("username"));
        //website.setCellFactory(TextFieldTableCell.forTableColumn());
        //name.setCellFactory(TextFieldTableCell.forTableColumn());


        dataTable.setOnMouseClicked(event -> {
            Credential c = dataTable.getSelectionModel().getSelectedItem();
            int index = dataTable.getSelectionModel().getSelectedIndex();
            if (event.getClickCount() == 2 && (!dataTable.getSelectionModel().getSelectedItem().getWebsite().isEmpty())) {
                displayDetailCredential(c, index);
            }
        });

        ScheduledService<Boolean> svc = new ScheduledService<Boolean>() {
            protected Task<Boolean> createTask() {
                return new Task<Boolean>() {
                    protected Boolean call() {
                        Platform.runLater(() -> {
                            Clipboard cb = Clipboard.getSystemClipboard();
                            cb.clear();

                        });
                        return true;
                    }
                };

            }
        };
        svc.setPeriod(Duration.millis(20000));
        svc.start();

        dataTable.setOnKeyPressed(event -> {
            KeyCombination keyCombinationShiftC = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN);

            if (keyCombinationShiftC.match(event)) {
                Credential c = dataTable.getSelectionModel().getSelectedItem();
                content.putString(String.valueOf(c.getPassword()));
                clipboard.setContent(content);
                //scheduler.scheduleAtFixedRate( runner, 0, 2, TimeUnit.SECONDS);
                //clipService.restart();

            }


        });

    }

    /**
     * Show exit dialog
     */
    public void showExitDialog(boolean areCredentialsOpen){
        Alert alert;
        Optional<ButtonType> result;

        if (credentialsViewModel != null) {
            if (credentialsViewModel.isCredentialsChanged() && areCredentialsOpen) {
                alert = new Alert(Alert.AlertType.WARNING);
                alert.setContentText("There are changes that haven't been saved. Do you want to save them now?");
                result = alert.showAndWait();

                ButtonType buttonTypeSave = new ButtonType("Yes", ButtonBar.ButtonData.YES);
                ButtonType buttonTypeCancel = new ButtonType("No", ButtonBar.ButtonData.NO);

                alert.getButtonTypes().setAll(buttonTypeSave, buttonTypeCancel);

                if (result.get() == buttonTypeSave) {
                    credentialsViewModel.saveAllInformation();
                }
            }
        }

        alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("Are you sure you want to exit?");
        result = alert.showAndWait();

        if (result.get() == ButtonType.OK) {
            System.exit(0);
        }
    }

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
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open a Credentials Database");
        File file = fileChooser.showOpenDialog(stage);
        credentialsViewModel = new CredentialsViewModel(file.getAbsolutePath());
    }

    /**
     * method to save changes
     */
    @FXML
    void onSaveFile(ActionEvent event) {
        boolean done = credentialsViewModel.saveAllInformation();
        if ( !done ) {

            Alert alert = new Alert(Alert.AlertType.ERROR, "Wasn't possible to save your changes.");
            alert.setTitle("Data not saved");
            alert.setResizable(false);

            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.setHeaderText("An error occurred while saving.");
            //alert.setContentText("Insert Citizen Card, or verify that it is correctly inserted, then open this application again.");
            alert.showAndWait();
            //System.exit(3001);
        }
    }
    /**
     * method to close file
     */
    @FXML
    void onCloseFile(ActionEvent event) {
        dataTable.getItems().clear();
    }

    /**
     * method to handle adding a new credential through the button
     */
    @FXML
    void onAddCredential(ActionEvent event) {
        displayDetailCredential(new Credential("","", new char[] {}), -1);
    }

    private void fillDataTable(CredentialsList credentialsList){
        ObservableList<Credential> obsListCredentials = FXCollections.observableArrayList(credentialsList);
        dataTable.setItems(obsListCredentials);
    }

    private void displayDetailCredential(Credential credential, int index){
        if (credential == null)
            return;

        Credential newC = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/credentialDetail.fxml"));
            Parent root = loader.load();
            CredentialDetailController credentialDetailController = loader.getController();
            newC = credentialDetailController.open(stage, root, credential);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if( newC != null ) {
            CredentialsList list = credentialsViewModel.getCredentialsList();

            if (index >= 0 ) {
                list.remove(index);
                list.add(index, newC);
            }
            else {
                list.add(newC);
            }

            // Check if list was edited
            byte[] hash = Security.computeHash(list.toByteArray());
            if (! hash.equals( credentialsViewModel.getCredentialsHash() )){
                credentialsViewModel.setCredentialsHash(hash);
            }

            dataTable.getItems().clear();
            fillDataTable(list);
            credentialsViewModel.setCredentialsList(list);
        }
        else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Error adding Credential");
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

}
