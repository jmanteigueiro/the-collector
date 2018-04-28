package Data;

import Model.Config;
import Model.CredentialsList;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.Reader;
import java.nio.file.NoSuchFileException;

import Security.Security;

public class CredentialsJSON {
    private String filename;

    public CredentialsJSON(String filename) {
        this.filename = filename;
    }

    /**
     * Passa a lista de credenciais para JSON, cifra o conteúdo na integra e guarda em ficheiro.
     * @param config Objeto que contém a chave simétrica AES e o vetor de inicialização
     * @param list Lista de credenciais a cifrar
     * @return Nova configuração, com nova chave simétrica e novo vetor de inicialização
     */
    public Config saveCredentials(Config config, CredentialsList list){
        Gson gson = new Gson();
        String listGSON = gson.toJson(list);

        byte[] newSimmetricKey = Security.generateAESKey();
        byte[] newInitVector = Security.generateRandomBytes(16);

        config.setSimmetricKey(newSimmetricKey);
        config.setInitVector(newInitVector);

        byte[] ciphertext = Security.encryptAES(listGSON, config);

        try (FileOutputStream fos = new FileOutputStream(filename)) {
            fos.write(ciphertext);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return config;
    }

    /**
     * Lê o ficheiro de credenciais cifrado e decifra-o usando chave simétrica
     * para obter a lista de credenciais
     * @param config Objeto Config que contém a chave simétrica AES e o vetor de inicialização
     * @return Lista de credenciais
     */
    public CredentialsList loadCredentials(Config config){
        CredentialsList list = new CredentialsList();

        byte[] ciphertext;

        try (FileInputStream fis = new FileInputStream(filename)) {
            ciphertext = fis.readAllBytes();
        }
        catch (Exception e) {
            e.printStackTrace();
            return new CredentialsList();
        }

        String plaintext = Security.decryptAES(ciphertext, config);

        Gson gson = new Gson();
        list = gson.fromJson(plaintext, list.getClass());

        if (list == null)
            list = new CredentialsList();

        return list;
    }
}
