package ViewModel;

import Data.ConfigJSON;
import Data.Exceptions.CredentialsIntegrityException;
import GoogleAuthenticator.GAuth;
import GoogleAuthenticator.TOTP;
import Model.Config;
import Model.CredentialsList;

import Security.PortugueseEID;
import Security.Security;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.PublicKey;

/**
 * Ligação entre a janela principal do programa (lista de credenciais) e os objetos.
 */
public class CredentialsViewModel {
    private String fileConfig = "config.cfg";

    private Config config;
    private CredentialsList credentialsList;

    private ConfigJSON configJSON;
    private boolean auth = false;

    public CredentialsViewModel(){
        initProgram();
    }

    /**
     * Inicializa o programa depois do Login, carregando as configurações e as credenciais.
     */
    private void initProgram(){
        configJSON = new ConfigJSON(fileConfig);

        File f = new File(fileConfig);
        if(f.exists() && !f.isDirectory()) {    // Verifica se o ficheiro Config existe
            loadAllInformation();

            if (config.getAuthenticationPublicKey() == null){
                // Registar user outra vez
                registerUser();
            }
            else {
                // Google Authenticator
                boolean gauthValid = false;
                do{
                    gauthValid = googleAuthentication();
                }
                while (!gauthValid);
            }
        }
        else {
            registerUser();
        }

        // Save after initialization so the AES symmetric key changes
        //saveAllInformation();

        this.auth = false;
    }

    /**
     * Método a ser chamado quando o ficheiro já existe, i.e. o utilizador já está registado.
     */
    private void loadAllInformation(){
        // Carregar chave pública e dados cifrados
        config = configJSON.loadConfig();

        if (config.getAuthenticationPublicKey() == null)
            return;

        // Obter resposta ao Nonce
        PortugueseEID pid = new PortugueseEID();

        String nonce = Security.Security.generateNonce(256);

        boolean verified = pid.signNonceAndVerify(nonce, Security.publicKeyFromBytes( config.getAuthenticationPublicKey() ));

        if (!verified){
            System.exit(1);
        }

        // TODO: fazer google auth

        // Obter chaves de cifra e integridade
        Security.DBKeys dbKeys = pid.getKeysFromCC();

        config.setSymmetricKey( dbKeys.getSymmetricKey().getBytes() );
        config.setIntegrityKey( dbKeys.getIntegrityKey().getBytes() );

        pid.closeConnection();

        // Decifrar os dados
        try {
            config = configJSON.decryptConfig(config);
        } catch (CredentialsIntegrityException e) {
            e.printStackTrace();
        }

        credentialsList = config.getCredentialsList();
    }

    public CredentialsList getCredentialsList() {
        return credentialsList;
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

        config.setAuthenticationPublicKey( publicKey.getEncoded() );

        pid.closeConnection();

        String gKey = GAuth.generateNewKey();
        config.setGkey(gKey.getBytes());
        // TODO: Devolver o QR Code da Google Key - André Rodrigues
    }

    /**
     * Método que faz a autenticação com as OTPs do Google Authenticator
     * @return boolean que indica se a OTP é válida ou não
     */
    private boolean googleAuthentication() {
        try {
            // TODO: Receber código do telemóvel do utilizador
            String QRCode = null;
            // Validar o código introduzido pelo utilizador
            boolean TOTPValid = GAuth.validateTOTPCode(config, QRCode);

            return TOTPValid;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Guarda configurações e credenciais.
     * Deve ser chamado sempre que algum destes objetos for modificado.
     */
    private void saveAllInformation(){
        config.setCredentialsList(credentialsList);
        config = configJSON.saveConfig(config);

        PortugueseEID pid = new PortugueseEID();
        pid.writeKeysToCC(new String(config.getSymmetricKey()), new String(config.getIntegrityKey()));
        pid.closeConnection();
    }
}