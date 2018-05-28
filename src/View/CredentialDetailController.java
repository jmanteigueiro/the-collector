package View;


import Model.Credential;
import Model.PasswordGenerator;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;




public class CredentialDetailController {

    @FXML
    private TextField tf_website;

    @FXML
    private TextField tf_pass;

    @FXML
    private TextField tf_username;

    @FXML
    private CheckBox cb_pass;

    @FXML
    private MenuButton bt_genPass;

    @FXML
    private MenuItem bt_128bit;

    @FXML
    private MenuItem bt_256bit;

    @FXML
    private Button bt_cancel;

    @FXML
    private Button bt_save;

    private Credential newC;

    private final boolean[] change = new boolean[1];

    protected  Credential open(Stage parentStage, Parent root, Credential c1){
        initialize(c1, root, parentStage); // initialize scene, listeners and opens stage

        // when stage is closed
        if(change[0]){
            newC.setWebsite(tf_website.getText());
            newC.setUsername(tf_username.getText());
            return newC;
        }
        if (!c1.getWebsite().equals(newC.getWebsite()) || !String.valueOf(c1.getPassword()).equals(String.valueOf(newC.getPassword())) || !c1.getUsername().equals(newC.getUsername())){
            return newC;
        }

        return c1;

    }

    private void initialize( Credential c1, Parent root, Stage parentStage){
        newC = new Credential(c1.getWebsite(), c1.getUsername(), c1.getPassword());
        Stage stage = new Stage();

        change[0] = false;
        try {
            Scene scene = new Scene(root, 550, 450);
            stage.setTitle("Credential Info");
            stage.setResizable(false);
            stage.setScene(scene);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(parentStage);

            setListenners(stage, c1);
            tf_website.setText(c1.getWebsite());
            tf_username.setText(c1.getUsername());
            tf_pass.setText("***************");

            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setListenners(Stage stage, Credential c1){
        stage.setOnCloseRequest(event -> {
            if (!c1.getWebsite().equals(tf_website.getText()) || !String.valueOf(c1.getPassword()).equals(tf_pass.getText()) || !c1.getUsername().equals(tf_username.getText())) { //something change
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setContentText("SAVE?");
                Optional<ButtonType> bt = alert.showAndWait();
                if (bt.isPresent() && bt.get() == ButtonType.OK) {
                    newC.setUsername(tf_username.getText());
                    newC.setWebsite(tf_website.getText());
                    newC.setPassword(tf_pass.getText().toCharArray());
                    change[0] = true;
                    stage.close();
                } else {
                    event.consume();
                }
            }
            stage.close();

        });

        cb_pass.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if(cb_pass.isSelected())
                tf_pass.setText(String.valueOf(c1.getPassword()));
            else
                tf_pass.setText("***************");
        });

        bt_128bit.setOnAction(event -> {
            String newPass = genPassword(128);
            tf_pass.setText(newPass);
            newC.setPassword(newPass.toCharArray());
        });

        bt_256bit.setOnAction(event -> {
            String newPass = genPassword(256);
            tf_pass.setText(newPass);
            newC.setPassword(newPass.toCharArray());
        });

        bt_cancel.setOnAction(event -> {
            stage.close();
        });

        bt_save.setOnAction(event -> {
            newC.setUsername(tf_username.getText());
            newC.setWebsite(tf_website.getText());
            newC.setPassword(tf_pass.getText().toCharArray());
            stage.close();
        });

    }

    private String genPassword(int size){
        String rand = null;

        if(size == 256){
            PasswordGenerator generator = new PasswordGenerator(size);
            try {
               rand = generator.generator();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }

        else{ // size = 128
            PasswordGenerator generator = new PasswordGenerator(size);
            try {
                rand = generator.generator();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }

        return rand;
    }


}
