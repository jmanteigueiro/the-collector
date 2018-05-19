package ViewModel;

import Data.ConfigJSON;
import Data.Exceptions.CredentialsIntegrityException;
import GoogleAuthenticator.GAuth;
import Model.Config;
import Model.CredentialsList;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.*;

/**
 * Ligação entre a janela principal do programa (lista de credenciais) e os objetos.
 */
public class CredentialsViewModel {
    private String fileConfig = "config.cfg";

    private Config config;
    private CredentialsList credentialsList;

    private ConfigJSON configJSON;
    private boolean auth = false;

    public CredentialsViewModel(PrivateKey privateKey, String qrCode ) throws UnsupportedEncodingException {
        initProgram(privateKey, qrCode);

        privateKey = null;
    }

    /**
     * Inicializa o programa depois do Login, carregando as configurações e as credenciais.
     * @param privateKey Chave privada de autenticação RSA para decifrar a chave simétrica.
     */
    private void initProgram(PrivateKey privateKey, String QRCode) throws UnsupportedEncodingException {
        configJSON = new ConfigJSON(fileConfig);

        //Se o ficheiro config ainda não existir, gerar uma googleKey
        GAuth.generateNewKey();
        //Gravar no config a googlekey
        //Devolver o QR code da googlekey

        //Se o ficheiro config já existir então começar aqui:
        //Abrir o config
        Config config = null;
        Boolean TOTPValid = false;
        //O utilizador introduz o código do telemóvel e depois:
        //Validar o codigo introduzido pelo utilizador
        TOTPValid = GAuth.validateTOTPCode(config, QRCode);

        //oadAllInformation(privateKey);

        // Save after initialization so the AES symmetric key changes
        //saveAllInformation();

        this.auth = false;
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

    public boolean isAuth() {
        return auth;
    }
}