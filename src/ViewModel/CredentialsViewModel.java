package ViewModel;

import CryptoPackage.DBKeys;
import CryptoPackage.PortugueseEID;
import CryptoPackage.Security;
import Data.ConfigJSON;
import Data.Exceptions.CredentialsIntegrityException;
import Authenticator.GAuth;
import Model.Config;
import Model.CredentialsList;
import javafx.scene.control.Alert;

import java.io.File;
import java.io.IOException;
import java.security.PublicKey;
import java.util.Base64;

/**
 * Ligação entre a janela principal do programa (lista de credenciais) e os objetos.
 */
public class CredentialsViewModel {
    private String fileConfig = "creddb.cfg";

    public Config config;
    private CredentialsList credentialsList;
    private byte[] credentialsHash;
    private boolean credentialsChanged = false;
    private String ownerName;
    private ConfigJSON configJSON;

    public CredentialsViewModel() {
        initProgram();
    }

    public CredentialsViewModel(String path) {
        this.fileConfig = path;
        initProgram();
    }

    /**
     * Inicializa o programa depois do Login, carregando as configurações e as credenciais.
     */
    private void initProgram() {
        configJSON = new ConfigJSON(fileConfig);

        File f = new File(fileConfig);
        if(f.exists() && !f.isDirectory()) {    // Verifica se o ficheiro Config existe
            loadAllInformation();

            if (config.getAuthenticationPublicKey() == null){
                // Registar user outra vez
                registerUser();
            }
        }
        else {
            registerUser();
        }

        // Save after initialization so the AES symmetric key and the Integrity key change
        credentialsChanged = true;
        saveAllInformation();
    }

    public String getOwnerName(){
        return ownerName;
    }

    /**
     * Método a ser chamado quando o ficheiro já existe, i.e. o utilizador já está registado.
     */
    private void loadAllInformation() {
        // Carregar chave pública e dados cifrados
        config = configJSON.loadConfig();
        System.out.println(config.getGkey());

        if (config.getAuthenticationPublicKey() == null)
            return;

        // Obter resposta ao Nonce
        PortugueseEID pid = new PortugueseEID();

        String nonce = Security.generateNonce(256);

        boolean verified = pid.signNonceAndVerify(nonce, Security.publicKeyFromBytes( config.getAuthenticationPublicKey() ));

        if (!verified){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("The application was not able to verify your identity.");
            alert.showAndWait();
            System.exit(2);
        }

        // TODO: fazer google auth

        // Obter chaves de cifra e integridade
        DBKeys dbKeys = pid.getKeysFromCC();

        config.setSymmetricKey( Base64.getDecoder().decode(dbKeys.getSymmetricKey()) );
        config.setIntegrityKey( Base64.getDecoder().decode(dbKeys.getIntegrityKey()) );

        pid.closeConnection();

        // Decifrar os dados
        try {
            config = configJSON.decryptConfig(config);
        } catch (CredentialsIntegrityException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Credentials integrity is compromised.");
            alert.showAndWait();
            System.exit(3);
        }

        credentialsList = config.getCredentialsList();
        credentialsHash = Security.computeHash( credentialsList.toByteArray() );
    }

    public CredentialsList getCredentialsList() {
        return credentialsList;
    }

    public void setCredentialsList(CredentialsList list) {
        credentialsList = list;
        config.setCredentialsList(list);
    }

    /**
     * Método a ser chamado quando não existe um ficheiro Config, ou quando a chave pública não existe
     */
    private void registerUser(){
        config = new Config();
        credentialsList = new CredentialsList();
        config.setCredentialsList(credentialsList);

        PortugueseEID pid = new PortugueseEID();
        PublicKey publicKey = pid.getPublicKey();
        ownerName = pid.getOwnerName();
        config.setAuthenticationPublicKey( publicKey.getEncoded() );

        pid.closeConnection();
    }


    /**
     * Guarda configurações e credenciais.
     * Deve ser chamado sempre que algum destes objetos for modificado.
     */
    public boolean saveAllInformation(){
        if (!credentialsChanged){
            return true;
        }

        config.setCredentialsList(credentialsList);

        Config configBackup = config.clone();

        try {
            config = configJSON.saveConfig(config);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        PortugueseEID pid = new PortugueseEID();
        boolean result = pid.writeKeysToCC(config.getSymmetricKey(), config.getIntegrityKey());
        pid.closeConnection();

        if (!result) {
            config = configBackup;
            try {
                config = configJSON.saveConfig(config);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        credentialsChanged = false;

        return result;
    }

    public byte[] getCredentialsHash() {
        return credentialsHash;
    }

    public void setCredentialsHash(byte[] credentialsHash) {
        this.credentialsHash = credentialsHash;
        credentialsChanged = true;
    }

    public boolean isCredentialsChanged() {
        return credentialsChanged;
    }

    public byte[] getGoogleKey() {
        return config.getGkey();
    }

    public void setGoogleKey(byte[] gKey) {
        config.setGkey(gKey);
    }

    public String getFilename() {
        return fileConfig;
    }
}