package Data;

import Model.Config;
import com.google.gson.Gson;

import java.io.*;
import java.security.PrivateKey;
import Security.Security;

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
    public void saveConfig(Config config){
        byte[] symmetricKey = config.getSymmetricKey();
        byte[] integrityKey = config.getIntegrityKey();

        byte[] cipherSymmetricKey = Security.encryptRSA(config.getSymmetricKey(), config.getAuthenticationPublicKey());
        config.setSymmetricKey(cipherSymmetricKey);

        byte[] cipherIntegrityKey = Security.encryptRSA(config.getIntegrityKey(), config.getAuthenticationPublicKey());
        config.setIntegrityKey(cipherIntegrityKey);

        Gson gson = new Gson();
        String configJson = gson.toJson(config);

        try (FileOutputStream fos = new FileOutputStream(filename)) {
            fos.write(configJson.getBytes());
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        config.setSymmetricKey(symmetricKey);
        config.setIntegrityKey(integrityKey);
    }

    /**
     * Lê o ficheiro JSON completo e decifra a chave AES simétrica.
     * @param sk Chave privada RSA para decifrar a chave AES, que foi cifrada com chave pública RSA
     * @return Objeto com as configurações
     */
    public Config loadConfig(PrivateKey sk){
        Config config = new Config();

        byte[] wholeconfig;

        try (FileInputStream fis = new FileInputStream(filename)) {
            wholeconfig = fis.readAllBytes();
        }
        catch (FileNotFoundException e) {
            System.out.println("File not found: " + filename);
            return new Config();
        }
        catch (Exception e){
            e.printStackTrace();
            return new Config();
        }

        Gson gson = new Gson();
        config = gson.fromJson(new String(wholeconfig), config.getClass());

        if (config == null)
            return new Config();

        config.setSymmetricKey(Security.decryptRSA(config.getSymmetricKey(), sk));
        config.setIntegrityKey(Security.decryptRSA(config.getIntegrityKey(), sk));

        return config;
    }
}
