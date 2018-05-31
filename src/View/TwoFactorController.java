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


public class TwoFactorController {
    
    @FXML
    private TextField authcodefield;

    @FXML
    private ImageView authimg;

    @FXML
    private Button authokbutton;

    private Stage stage;

    private byte[] code = null;

    private int counter = 0;

    private boolean control = true;
    private boolean firsttime = false;


    private CredentialsViewModel credentialsViewModel;


    protected void open(Stage parentStage, Parent root, CredentialsViewModel viewModel) throws IOException {
        this.credentialsViewModel = viewModel;
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

        byte[] gKey = credentialsViewModel.getGoogleKey();

        if (gKey != null){
            code = gKey;
        }
        else {
            GAuth.NewGoogleAuthenticator(new String(credentialsViewModel.getOwnerName()));

            code = GAuth.gkey;

            File file = new File("QRcode.png");
            Image image = new Image(file.toURI().toString());
            authimg.setImage(image);

            firsttime = true;

            credentialsViewModel.setGoogleKey(code);
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
        Boolean valid = GAuth.validateTOTPCode(credentialsViewModel.getGoogleKey(), authcodefield.getText());
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
            File file = new File(credentialsViewModel.getFilename());

            file.delete();
            System.exit(1);
        }
    }

}
