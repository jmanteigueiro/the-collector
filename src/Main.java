import View.MainViewController;
import ViewModel.CredentialsViewModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {
    /**
     * Este objeto contém todos os métodos de ligação entre a Main e os view
     */
    private MainViewController mainViewController;

    public static void main(String[] args) {
        // INFO SOBRE OS PACKAGES:
        // View       - Classes de UI
        // Model      - Classes de objetos a ser utilizados na aplicação
        // View Model - Classes de acesso e modificação dos dados
        // CryptoPackage   - Classes "helpers" que contêm os métodos de segurança
        //
        // Para obter os dados para a UI, deve invocar-se sempre métodos de classes ViewModel!
        // Instanciar um objeto da classe ViewModel e ir invocando métodos daí

        launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/View/mainView.fxml"));

        Scene scene = new Scene(root, 800, 450);

        mainViewController = new MainViewController();

        mainViewController.setStage(primaryStage);

        primaryStage.setOnCloseRequest((WindowEvent event) -> {
            mainViewController.showExitDialog(true);
        });

        primaryStage.setTitle("The Collector");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
