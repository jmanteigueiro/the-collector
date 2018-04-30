import View.MainViewController;
import ViewModel.CredentialsViewModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    /**
     * Este objeto contém todos os métodos de ligação entre a Main e os dados
     */
    private static CredentialsViewModel viewModel;
    /**
     * Este objeto contém todos os métodos de ligação entre a Main e os view
     */
    private static MainViewController mainViewController;
    /**
     * Isto é um Javadoc, serve para descrever um método
     *
     * @param args isto é a descrição do parâmetro que é recebido
     * @return isto é para descrever o valor que o método retorna
     */
    public static void main(String[] args) {
        // Esta função deve ficar simplificada ao máximo
        //
        // INFO SOBRE OS PACKAGES:
        // View       - Classes de UI
        // Model      - Classes de objetos a ser utilizados na aplicação
        // View Model - Classes de acesso e modificação dos dados
        // Security   - Classes "helpers" que contêm os métodos de segurança
        //
        // Para obter os dados para a UI, deve invocar-se sempre métodos de classes ViewModel!
        // Instanciar um objeto da classe ViewModel e ir invocando métodos daí

        // TODO: receber a chave privada RSA
        //viewModel = new CredentialsViewModel(null);

        launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/View/mainView.fxml"));

        Scene scene = new Scene(root, 800, 450);
        mainViewController.setStage(primaryStage);
        primaryStage.setTitle("The Collector");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
