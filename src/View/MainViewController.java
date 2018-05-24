package View;

import Model.Credential;
import Model.CredentialsList;
import ViewModel.CredentialsViewModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;


public class MainViewController
{

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
            String fileData = file.getName();
            // e para remover o seguinte codigo e adicionar a parte do ze
            PrivateKey privateKey = null;
            try{
                File filePrivateKey = new File("/home/fabio/Documents/MEI_2017/SSS" + "/sk.pem");
                FileInputStream fis = new FileInputStream("/home/fabio/Documents/MEI_2017/SSS" + "/sk.pem");
                byte[] encodedPrivateKey = new byte[(int) filePrivateKey.length()];
                fis.read(encodedPrivateKey);
                fis.close();

                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedPrivateKey);
                privateKey = keyFactory.generatePrivate(privateKeySpec);

            }
            catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
                e.printStackTrace();
            }

            credentialsViewModel = new CredentialsViewModel();
            credentialsList = credentialsViewModel.getCredentialsList();

            fillDataTable(credentialsList);
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

    private void fillDataTable(CredentialsList credentialsList){
        dataTable = new TableView<>();
        ObservableList<Credential> obsListCredentials = FXCollections.observableArrayList(credentialsList);
        dataTable.setItems(obsListCredentials);

        TableColumn<Credential, String> columnUsername = new TableColumn<Credential, String>("Username");
        columnUsername.setCellValueFactory(new PropertyValueFactory("username"));
        TableColumn<Credential, char[]> columnPass = new TableColumn<Credential, char[]>("Password");
        columnPass.setCellValueFactory(new PropertyValueFactory("password"));


    }

    public static Stage getStage() {
        return stage;
    }

    public static void setStage(Stage stage) {
        MainViewController.stage = stage;
    }
}
