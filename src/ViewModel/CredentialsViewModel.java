package ViewModel;

import Data.ConfigJSON;
import Data.Exceptions.CredentialsIntegrityException;
import Model.Config;
import Model.CredentialsList;

import java.io.IOException;
import java.security.*;

/**
 * Ligação entre a janela principal do programa (lista de credenciais) e os objetos.
 */
public class CredentialsViewModel {
    private String fileConfig = "config.cfg";

    private Config config;
    private CredentialsList credentialsList;

    private ConfigJSON configJSON;

    public CredentialsViewModel(PrivateKey privateKey){
        initProgram(privateKey);

        privateKey = null;
    }

    /**
     * Inicializa o programa depois do Login, carregando as configurações e as credenciais.
     * @param privateKey Chave privada de autenticação RSA para decifrar a chave simétrica.
     */
    private void initProgram(PrivateKey privateKey){
        configJSON = new ConfigJSON(fileConfig);

        loadAllInformation(privateKey);

        // Save after initialization so the AES symmetric key changes
        saveAllInformation();
    }

    /**
     * Guarda configurações e credenciais.
     * Deve ser chamado sempre que algum destes objetos for modificado.
     */
    private void saveAllInformation() {
        config.setCredentialsList(credentialsList);
        try {
            config = configJSON.saveConfig(config);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadAllInformation(PrivateKey sk){
        try {
            config = configJSON.loadConfig(sk);
        } catch (CredentialsIntegrityException e) {
            e.printStackTrace();
        }
        credentialsList = config.getCredentialsList();
    }

    public CredentialsList getCredentialsList() {
        return credentialsList;
    }

    public boolean saveInformation(CredentialsList credentialsList){
        config.setCredentialsList(credentialsList);

        try {
            config = configJSON.saveConfig(config);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private void disposeCredentials(){
        credentialsList.dispose();
        credentialsList = null;
    }
}