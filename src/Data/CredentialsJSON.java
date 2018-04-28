package Data;

import Model.Config;
import Model.CredentialsList;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.nio.file.NoSuchFileException;

import Security.Security;

/**
 * Classe para ler e escever as credenciais em ficheiro.
 */
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

        byte[] newSymmetricKey = Security.generateAESKey();
        byte[] newInitVector = Security.generateRandomBytes(16);

        config.setSymmetricKey(newSymmetricKey);
        config.setInitVector(newInitVector);

        byte[] ciphertext = Security.encryptAES(listGSON, config);

        try (FileOutputStream fos = new FileOutputStream(filename)) {
            fos.write(ciphertext);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        byte[] hmac = Security.computeHMAC(ciphertext, config);

        config.setHmac(hmac);

        return config;
    }

    /**
     * Lê o ficheiro de credenciais cifrado e decifra-o usando chave simétrica
     * para obter a lista de credenciais
     * @param config Objeto Config que contém a chave simétrica AES e o vetor de inicialização
     * @return Lista de credenciais
     */
    public CredentialsList loadCredentials(Config config) throws Exception {
        CredentialsList list = new CredentialsList();

        byte[] ciphertext;

        try (FileInputStream fis = new FileInputStream(filename)) {
            ciphertext = fis.readAllBytes();
        }
        catch (FileNotFoundException e) {
            System.out.println("File not found: " + filename);
            return new CredentialsList();
        }

        if (! Security.verifyHMAC(ciphertext, config)){
            throw new CredentialsIntegrityException();
        }

        String plaintext = Security.decryptAES(ciphertext, config);

        Gson gson = new Gson();
        list = gson.fromJson(plaintext, list.getClass());

        if (list == null)
            list = new CredentialsList();

        return list;
    }
}

/*** Este tipo de exceção é lançado quando o HMAC-SHA256 não é verificado corretamente */
class CredentialsIntegrityException extends Exception{
    public CredentialsIntegrityException(){
        super("Integridade das credenciais comprometida.");
    }
}