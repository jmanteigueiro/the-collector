package View;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class loginController {


    protected  boolean open(Stage parentStage, Parent root){
        initialize(root, parentStage); // initialize scene, listeners and opens stage
        return false;
    }

    private void initialize(Parent root, Stage parentStage){

        Stage stage = new Stage();
        try {
            Scene scene = new Scene(root, 550, 450);
            stage.setTitle("Login");
            stage.setResizable(false);
            stage.setScene(scene);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(parentStage);

            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
