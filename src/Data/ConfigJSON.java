package Data;

import CryptoPackage.PortugueseEID;
import CryptoPackage.Security;
import Data.Exceptions.CredentialsIntegrityException;
import Data.Helpers.GsonHelpers;
import Model.Config;
import com.google.gson.Gson;
import Authenticator.GAuth;
import javafx.scene.control.Alert;
import javafx.scene.layout.Region;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Classe para ler e escrever as configurações para ficheiro.
 */
public class ConfigJSON {
    private String filename;

    public ConfigJSON(String filename){
        this.filename = filename;
    }

    /**
     * Cifra a chave AES simétrica com a chave pública RSA
     * e guarda como JSON o objeto num ficheiro.
     * @param config Objeto com a configuração
     */
    public Config saveConfig(Config config) throws IOException {
        byte[] symmetricKey = Security.generate256BitKey();
        byte[] integrityKey = Security.generate256BitKey();

        byte[] initializationVector = Security.generateRandomBytes(16);
        config.setInitVector(initializationVector);

        byte[] cipherCredentials = Security.encryptAES(config.getCredentialsBytes(), symmetricKey, initializationVector);
        config.setCredentialsBytes(cipherCredentials);

        PortugueseEID pteid = new PortugueseEID();
        byte[] hmac = pteid.signBytes(cipherCredentials);// Security.computeHMAC(cipherCredentials, integrityKey);
        pteid.closeConnection();
        config.setDigitalSignature(hmac);

        byte[] googlekey = config.getGkey();
        config.setGkey(googlekey);

        Gson gson = GsonHelpers.buildCustomGson();
        String configJson = gson.toJson(config);
        gson = null;

        FileOutputStream fos = new FileOutputStream(filename);
        fos.write(configJson.getBytes());

        config.setSymmetricKey(symmetricKey);
        config.setIntegrityKey(integrityKey);

        return config;
    }

    /**
     * Lê o ficheiro JSON completo e decifra a chave AES simétrica.
     * @return Objeto com as configurações
     */
    public Config loadConfig() {
        Config config = new Config();

        byte[] wholeConfig;

        try (FileInputStream fis = new FileInputStream(filename)) {
            wholeConfig = fis.readAllBytes();
        }
        catch (FileNotFoundException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, filename + " not found.");
            alert.setTitle("File not found");
            alert.setResizable(false);

            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.showAndWait();
            return new Config();
        }
        catch (Exception e){
            e.printStackTrace();
            return new Config();
        }

        Gson gson = GsonHelpers.buildCustomGson();
        config = gson.fromJson(new String(wholeConfig), config.getClass());
        //gson = null;

        if (config == null)
            return new Config();

        return config;
    }

    public Config decryptConfig(Config config) throws CredentialsIntegrityException {
        PortugueseEID pteid = new PortugueseEID();
        if (!pteid.verifySignature(config.getCredentialsBytes(), config.getDigitalSignature()))
            throw new CredentialsIntegrityException();
        pteid.closeConnection();

        byte[] plainCredentials = Security.decryptAES(config.getCredentialsBytes(), config.getSymmetricKey(), config.getInitVector());
        config.setCredentialsBytes(plainCredentials);

        return config;
    }
}
