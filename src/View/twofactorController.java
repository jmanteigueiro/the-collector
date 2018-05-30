package View;


import Authenticator.GAuth;
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

import java.io.*;


public class twofactorController {


    @FXML
    private TextField authcodefield;

    @FXML
    private ImageView authimg;

    @FXML
    private Button authokbutton;


    Stage stage;


    String code = null;

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



        //ir ao config ver se o codigo existe
        //se não existe cria-se um novo
        //

        String fileName = "/home/arodrigues/Desktop/codeauth.txt";

        String line = null;



        File f = new File("/home/arodrigues/Desktop/codeauth.txt");

        if(f.exists() && !f.isDirectory()) {
            // do something

        try {
            FileReader fileReader = new FileReader(fileName);

            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while((line = bufferedReader.readLine()) != null) {
                code = line;
            }
            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '" +
                            fileName + "'");
        }


        }else {

            GAuth.NewGoogleAuthenticator("andre", "/home/arodrigues/Desktop/");

            code = GAuth.gkey;

            File file = new File("/home/arodrigues/Desktop/QRcode.png");
            Image image = new Image(file.toURI().toString());
            authimg.setImage(image);

            firsttime = true;

            FileWriter fileWriter =
                    new FileWriter(fileName);

            // Always wrap FileWriter in BufferedWriter.
            BufferedWriter bufferedWriter =
                    new BufferedWriter(fileWriter);

            // Note that write() does not automatically
            // append a newline character.
            bufferedWriter.write(String.valueOf(GAuth.gkey));
            bufferedWriter.close();
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
        Boolean valid = GAuth.validateTOTPCode(code, authcodefield.getText());
        if (counter < 5 && control==true)
        {
            if (valid){
                System.out.println("Valido!");
                if (firsttime == true){
                    File file = new File("/home/arodrigues/Desktop/QRcode.png");

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
            File file = new File("/home/arodrigues/Desktop/codeauth.txt");

            file.delete();
            System.exit(1);
        }
    }

}
