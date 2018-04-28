package ViewModel;

import Data.ConfigJSON;
import Data.CredentialsJSON;
import Model.Config;
import Model.Credential;
import Model.CredentialsList;

import javax.crypto.KeyGenerator;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;

/**
 * Ligação entre a janela principal do programa (lista de credenciais) e os objetos.
 */
public class CredentialsViewModel {
    private String fileCredentials = "data.dat";
    private String fileConfig = "config.cfg";

    private Config config;
    private CredentialsList credentials;

    private ConfigJSON configJSON;
    private CredentialsJSON credentialsJSON;

    public CredentialsViewModel(PrivateKey privateKey){
        initProgram(privateKey);

        privateKey = null;
    }

    /**
     * Inicializa o programa depois do Login, carregando as configurações e as credenciais.
     * @param sk Chave privada de autenticação RSA para decifrar a chave simétrica.
     */
    private void initProgram(PrivateKey privateKey){
        configJSON = new ConfigJSON(fileConfig);
        credentialsJSON = new CredentialsJSON(fileCredentials);

        loadConfig(privateKey);

        loadCredentials();

        // Save after initialization so the AES symmetric key changes
        saveAllInformation();
    }

    /**
     * Guarda configurações e credenciais.
     * Deve ser chamado sempre que algum destes objetos for modificado.
     */
    private void saveAllInformation(){
        config = saveCredentials();
        saveConfig();
    }

    private void loadConfig(PrivateKey sk){
        config = configJSON.loadConfig(sk);
    }

    private void saveConfig(){
        configJSON.saveConfig(config);
    }

    private void loadCredentials(){
        try {
            credentials = credentialsJSON.loadCredentials(config);
        } catch (Exception e) {
            e.printStackTrace();
            // Entra aqui se o HMAC está errado!
            // TODO: Handle this exception! E.g. show error!
            System.exit(1);
        }
    }

    private Config saveCredentials(){
        return credentialsJSON.saveCredentials(config, credentials);
    }

//    private void disposeCredentials(){
//        credentials.dispose();
//        credentials = null;
//    }
}