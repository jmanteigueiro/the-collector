package Tests;

import Data.ConfigJSON;
import Data.Exceptions.CredentialsIntegrityException;
import Model.Config;
import Model.CredentialsList;
import Security.Security;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;

public class CredentialsViewModelTest {
    private String fileConfig = "config.cfg";

    private Config config;
    private CredentialsList credentialsList;

    private ConfigJSON configJSON;

    public CredentialsViewModelTest(PrivateKey privateKey){
        PublicKey publicKey = null;
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair keyPair = keyGen.genKeyPair();
            publicKey = keyPair.getPublic();
            privateKey = keyPair.getPrivate();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        try (FileInputStream fis = new FileInputStream("sk.pem")) {
            byte[] skBytes = fis.readAllBytes();
            privateKey = Security.privateKeyFromBytes(skBytes);
        }
        catch (Exception e) {
            System.out.println("Secret key not found.");
            try (FileOutputStream fos = new FileOutputStream("sk.pem")) {
                fos.write(privateKey.getEncoded());
            }
            catch (Exception e2) { System.out.println("SK file created."); }
        }


        // Normal behavior from now on
        initProgram(privateKey, publicKey);

        privateKey = null;
    }

    private void initProgram(PrivateKey privateKey, PublicKey publicKey){
        configJSON = new ConfigJSON(fileConfig);

        loadAllInformation(privateKey);

        credentialsList.addCredential("a","b","c");
        credentialsList.addCredential("d","e","f");

        if (config.getAuthenticationPublicKey() == null)
            config.setAuthenticationPublicKey(publicKey.getEncoded());

        saveAllInformation();
    }

    private void saveAllInformation(){
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
}
