import Model.CredentialsList;
import ViewModel.CredentialsViewModel;
import Security.*;

public class Main {

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

        System.out.println("Hello World!");

        CredentialsViewModel viewModel = new CredentialsViewModel();
        viewModel.loadCredentials();
        viewModel.saveCredentials();


        String s = "OLÁ SOU O ANDRÉ";
        Security sec = new Security();
        AESValues aes = sec.encryptAES(s);
        String pt = sec.decryptAES(aes.getKey(), aes.getIv(), aes.getCiphertext());
        System.out.println(pt);

    }
}
