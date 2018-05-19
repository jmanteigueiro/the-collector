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
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.security.*;
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

        boolean load = false;



        PrivateKey priv;
        try {

            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA", "SUN");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            keyGen.initialize(1024, random);

            KeyPair pair = keyGen.generateKeyPair();
            priv = pair.getPrivate();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/login.fxml"));
            Parent root = loader.load();
            loginController loginController = loader.getController();
            load = loginController.open(stage, root, priv);

        } catch ( NoSuchAlgorithmException |  NoSuchProviderException | IOException  e) {
            e.printStackTrace();
        }
        if ( load ){
            try {

                credentialsList = new CredentialsList();
                credentialsList.addCredential("face", "ee", "bb");
                credentialsList.addCredential("google", "eeffff", "bb");
                credentialsList.addCredential("slack", "kkkk", "bb");

                fillDataTable(credentialsList);

            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Error while loading file");
                alert.showAndWait();
            }
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Error login");
            alert.showAndWait();
        }

    }

    /**
     * method to save changes
     */
    @FXML
    void onSaveFile(ActionEvent event) {
        boolean done = credentialsViewModel.saveInformation(credentialsList);
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
        credentialsList.dispose();

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
