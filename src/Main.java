import Model.Config;
import Model.CredentialsList;
import ViewModel.CredentialsViewModel;
import Security.*;
import com.google.gson.Gson;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;

public class Main {

    /**
     * Este objeto contém todos os métodos de ligação entre a Main e os dados
     */
    private static CredentialsViewModel viewModel;

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

        viewModel = new CredentialsViewModel();

        //viewModel.initProgram(sk);
    }
}
