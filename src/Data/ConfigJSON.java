package Data;

import CryptoPackage.Security;
import Data.Exceptions.CredentialsIntegrityException;
import Data.Helpers.GsonHelpers;
import Model.Config;
import com.google.gson.Gson;

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

        byte[] hmac = Security.computeHMAC(cipherCredentials, integrityKey);
        config.setHmac(hmac);

       /* byte[] googlekey = GAuth.gkey;
        config.setGkey(googlekey);*/

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
            System.out.println("File not found: " + filename);
            return new Config();
        }
        catch (Exception e){
            e.printStackTrace();
            return new Config();
        }

        Gson gson = GsonHelpers.buildCustomGson();
        config = gson.fromJson(new String(wholeConfig), config.getClass());
        gson = null;

        if (config == null)
            return new Config();

        return config;
    }

    public Config decryptConfig(Config config) throws CredentialsIntegrityException {
        if (! Security.verifyHMAC(config.getCredentialsBytes(), config.getHmac(), config.getIntegrityKey()) )
            throw new CredentialsIntegrityException();

        byte[] plainCredentials = Security.decryptAES(config.getCredentialsBytes(), config.getSymmetricKey(), config.getInitVector());
        config.setCredentialsBytes(plainCredentials);

        return config;
    }
}
