package View;


import Model.Credential;
import Model.PasswordGenerator;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.security.NoSuchAlgorithmException;
import java.util.Optional;


public class CredentialDetailController {

    @FXML
    private TextField tf_website;

    @FXML
    private TextField tf_pass;

    @FXML
    private TextField tf_username;

    @FXML
    private MenuButton bt_genPass;

    @FXML
    private MenuItem bt_40bit;

    @FXML
    private MenuItem bt_128bit;

    @FXML
    private MenuItem bt_256bit;

    @FXML
    private Button bt_cancel;

    @FXML
    private Button bt_save;

    @FXML
    private Button bt_delete;

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
            Scene scene = new Scene(root);
            stage.setTitle("Credential Info");
            stage.setResizable(false);
            stage.setScene(scene);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(parentStage);

            setListeners(stage, c1);
            tf_website.setText(c1.getWebsite());
            tf_username.setText(c1.getUsername());
            tf_pass.setText(new String(c1.getPassword()));

            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setListeners(Stage stage, Credential c1){
        stage.setOnCloseRequest(event -> {
            if (!c1.getWebsite().equals(tf_website.getText()) || !String.valueOf(c1.getPassword()).equals(tf_pass.getText()) || !c1.getUsername().equals(tf_username.getText())) { //something change
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setContentText("Save?");
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

        bt_40bit.setOnAction(event -> {
            String newPass = genPassword(40);
            tf_pass.setText(newPass);
            newC.setPassword(newPass.toCharArray());
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
            newC = null;
            stage.close();
        });

        bt_save.setOnAction(event -> {
            newC.setUsername(tf_username.getText());
            newC.setWebsite(tf_website.getText());
            newC.setPassword(tf_pass.getText().toCharArray());
            stage.close();
        });

        bt_delete.setOnAction(event -> {
            newC.setUsername("-9999999");
            newC.setWebsite("-9999999");
            newC.setPassword("-9999999".toCharArray());
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
