package View;

import GoogleAuthenticator.GAuth;
import Model.Config;
import ViewModel.CredentialsViewModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.management.StandardEmitterMBean;
import java.io.UnsupportedEncodingException;
import java.security.PrivateKey;

public class loginController {


    private Config config;
    private boolean auth;
    private CredentialsViewModel credentialsViewModel;
    private Stage stage;
    private PrivateKey priv;

    @FXML
    private Button gLogin;

    @FXML
    private TextArea gCode;

    @FXML
    void onGLogin(ActionEvent event) {
        //this.auth = new CredentialsViewModel();
        try {
            credentialsViewModel = new CredentialsViewModel(priv, gCode.getText());
            if (credentialsViewModel.isAuth())
                stage.close();
            else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Error login");
                alert.showAndWait();
            }
        } catch (UnsupportedEncodingException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Error login");
            alert.showAndWait();
        }

    }


    protected  boolean open(Stage parentStage, Parent root, PrivateKey priv){
        initialize(root, parentStage, priv); // initialize scene, listeners and opens stage
        return true;
    }

    private void initialize(Parent root, Stage parentStage, PrivateKey priv){

        stage = new Stage();
        this.priv = priv;

        Scene scene = new Scene(root, 550, 450);
        stage.setTitle("Login");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(parentStage);
        stage.show();


    }


}
