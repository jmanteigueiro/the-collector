package View;

import Model.Credential;
import Model.CredentialsList;
import ViewModel.CredentialsViewModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;


public class MainViewController implements Initializable {

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

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Splash - The Collector");
        alert.setHeaderText("Welcome to The Collector");
        alert.setContentText("Choose one of the following options.");

        ButtonType buttonTypeCreate = new ButtonType("Create a new file");
        ButtonType buttonTypeLoad = new ButtonType("Load an existing file");

        alert.getButtonTypes().setAll(buttonTypeCreate, buttonTypeLoad);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeLoad) {
            do {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Open a Credentials Database");
                fileChooser.setInitialDirectory(new File("."));
                fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("TheCollectorFile", "*.cfg")
                );
                file = fileChooser.showOpenDialog(stage);

                if (file == null)
                    showExitDialog();
            }
            while (file == null);
        }
        else if (result.get() == buttonTypeCreate){
            do {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Open a Credentials Database");
                fileChooser.setInitialDirectory(new File("."));
                fileChooser.setInitialFileName("creddb.cfg");
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("TheCollectorFile", "*.cfg")
                );
                file = fileChooser.showSaveDialog(stage);

                if (file == null)
                    showExitDialog();
            }
            while (file == null);
        }
        else {
            System.exit(1);
        }

        //credentialsViewModel = new CredentialsViewModel(file.getAbsolutePath());

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/twofactor.fxml"));
            Parent root = loader.load();
            twofactorController twofauth = loader.getController();
            twofauth.open(stage, root);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //fillDataTable(credentialsViewModel.getCredentialsList());

        website.setCellValueFactory(
                new PropertyValueFactory<>("website"));
        name.setCellValueFactory(
                new PropertyValueFactory<>("username"));
        website.setCellFactory(TextFieldTableCell.forTableColumn());
        website.setOnEditCommit(event ->
                event.getTableView().getItems().get(event.getTablePosition().getRow()).setWebsite(event.getNewValue()));

        name.setCellFactory(TextFieldTableCell.forTableColumn());
        name.setOnEditCommit(event ->
                event.getTableView().getItems().get(event.getTablePosition().getRow()).setUsername(event.getNewValue())
        );

        dataTable.setOnMouseClicked(event -> {
            Credential c = dataTable.getSelectionModel().getSelectedItem();
            int index = dataTable.getSelectionModel().getSelectedIndex();
            displayDetailCredential(c, index);
        });

    }

    /**
     * Show exit dialog
     */
    private void showExitDialog(){
        Alert alert;
        Optional<ButtonType> result;
        alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("Are you sure you want to exit The Collector?");
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
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Error saving file");
            alert.showAndWait();
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

            dataTable.getItems().clear();
            fillDataTable(list);
            credentialsViewModel.setCredentialsList(list);
        }
        else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Error adding Credential");
        }
    }

    public static Stage getStage() {
        return stage;
    }

    public static void setStage(Stage stage) {
        MainViewController.stage = stage;
    }

}
