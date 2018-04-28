package ViewModel;

import Data.ConfigJSON;
import Data.CredentialsJSON;
import Model.Config;
import Model.Credential;
import Model.CredentialsList;

import java.io.FileInputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
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

    /**
     * Inicializa o programa depois do Login, carregando as configurações e as credenciais.
     * @param sk Chave privada de autenticação RSA para decifrar a chave simétrica.
     */
    public void initProgram(PrivateKey privateKey){
        configJSON = new ConfigJSON(fileConfig);
        credentialsJSON = new CredentialsJSON(fileCredentials);

//        try (FileInputStream fis = new FileInputStream("sk.pem")){
//            sk = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(fis.readAllBytes()));
//        }
//        catch(Exception e){}

        loadConfig(privateKey);
        loadCredentials();

        // Save after initialization so the AES simmetric key changes
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
        credentials = credentialsJSON.loadCredentials(config);
    }

    private Config saveCredentials(){
        return credentialsJSON.saveCredentials(config, credentials);
    }

//    private void disposeCredentials(){
//        credentials.dispose();
//        credentials = null;
//    }
}