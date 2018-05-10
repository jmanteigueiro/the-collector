package View;

import Model.Credential;
import Model.CredentialsList;
import ViewModel.CredentialsViewModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class MainViewController implements Initializable {

    private static Stage stage;
    private CredentialsViewModel credentialsViewModel;
    private CredentialsList credentialsList;

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
    private TableColumn<Credential, String> website;

    @FXML
    private TableColumn<Credential, char[]> password;

    @FXML
    private TableColumn<Credential, String> name;


    /**
     * method used to initialize components
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        website.setCellValueFactory(
                new PropertyValueFactory<>("website"));
        name.setCellValueFactory(
                new PropertyValueFactory<>("username"));
        password.setCellValueFactory(
                new PropertyValueFactory<>("password"));

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
            //System.out.println(index);
            displayDetailCredential(c, index);
        });

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
        Stage stage = getStage();

//        FileChooser fileChooser = new FileChooser();
//        fileChooser.setTitle("Open passwords File");
//        fileChooser.getExtensionFilters().addAll(
//                new FileChooser.ExtensionFilter("TheCollectorFile", "*.tclt"),
//                new FileChooser.ExtensionFilter("All files", "*.*")
//        );
//        File file = fileChooser.showOpenDialog(stage);
//        if (file != null) {
//            String fileData = file.getName();
                // decifrar com sk
        // }

            //credentialsViewModel = new CredentialsViewModel(privateKey);

        credentialsList = new CredentialsList();
        credentialsList.addCredential("face", "ee", "bb");
        credentialsList.addCredential("google", "eeffff", "bb");
        credentialsList.addCredential("slack", "kkkk", "bb");

       fillDataTable(credentialsList);

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

    private void fillDataTable(CredentialsList credentialsList){
        ObservableList<Credential> obsListCredentials = FXCollections.observableArrayList(credentialsList);
        dataTable.setItems(obsListCredentials);

    }

    private void displayDetailCredential(Credential credential, int index){
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
            credentialsList.remove(credential);
            credentialsList.addCredential(newC, index);
            dataTable.getItems().clear();
            fillDataTable(credentialsList);
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
