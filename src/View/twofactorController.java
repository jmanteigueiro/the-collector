package View;


import Authenticator.GAuth;
import ViewModel.CredentialsViewModel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;


public class twofactorController {


    @FXML
    private TextField authcodefield;

    @FXML
    private ImageView authimg;

    @FXML
    private Button authokbutton;


    Stage stage;


    byte[] code = null;

    int counter = 0;

    boolean control = true;
    boolean firsttime = false;


    protected void open(Stage parentStage, Parent root) throws IOException {
        initialize(root, parentStage); // initialize scene, listeners and opens stage


    }

    private void initialize( Parent root, Stage parentStage) throws IOException {

         stage = new Stage();


        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {

            //Encriptar o config

            public void handle(WindowEvent we) {
                System.exit(1);
            }
        });


        String line = null;

        System.out.println(CredentialsViewModel.config);


        if (CredentialsViewModel.config.getGkey() != null){

            code=CredentialsViewModel.config.getGkey();

        }else {

            GAuth.NewGoogleAuthenticator("andre");

            code = GAuth.gkey;

            File file = new File("QRcode.png");
            Image image = new Image(file.toURI().toString());
            authimg.setImage(image);

            firsttime = true;

            CredentialsViewModel.config.setGkey(code);
        }



        try {
            Scene scene = new Scene(root);
            stage.setTitle("2 factor authentication");
            stage.setResizable(false);
            stage.setScene(scene);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(parentStage);

            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void validar(ActionEvent event) throws UnsupportedEncodingException, InterruptedException {
        Boolean valid = GAuth.validateTOTPCode(CredentialsViewModel.config.getGkey(), authcodefield.getText());
        if (counter < 5 && control==true)
        {
            if (valid){
                System.out.println("Valido!");
                if (firsttime == true){
                    File file = new File("QRcode.png");

                    file.delete();
                }
                stage.close();
            }else {
                System.out.println("Invalido");
                counter++;
                control=false;
                Thread.currentThread().sleep(5000);

            }
            control=true;
        }
        else {
            //delete config file
            File file = new File(MainViewController.filename);

            file.delete();
            System.exit(1);
        }
    }

}
