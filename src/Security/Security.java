package Security;

import Model.Config;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

/**
 * Esta classe contém os métodos de segurança necessários.
 */
public class Security {

    /**
     * Cifra um texto qualquer com o algoritmo AES-256 em modo CBC.
     * @param text Texto a cifrar
     * @param config Objeto Config que contém a chave simétrica e o vetor de inicialização a utilizar
     * @return Texto cifrado em formato de array de bytes
     */
    public static byte[] encryptAES(String text, Config config){
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");

            IvParameterSpec iv = new IvParameterSpec( config.getInitVector() );
            SecretKeySpec sk = new SecretKeySpec(config.getSymmetricKey(), "AES");

            cipher.init(Cipher.ENCRYPT_MODE, sk, iv);

            byte[] ciphertext = cipher.doFinal(text.getBytes());

            return ciphertext;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Decifra um qualquer array de bytes através de AES-256 em modo CBC.
     * @param ciphertext Array de bytes a decifrar
     * @param config Objeto Config que contém a chave simétrica e o vetor de inicialização a utilizar
     * @return Texto limpo original
     */
    public static String decryptAES(byte[] ciphertext, Config config){
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");

            IvParameterSpec iv = new IvParameterSpec( config.getInitVector() );
            SecretKeySpec sk = new SecretKeySpec(config.getSymmetricKey(), "AES");

            cipher.init(Cipher.DECRYPT_MODE, sk, iv);

            byte[] plaintext = cipher.doFinal(ciphertext);

            return new String(plaintext);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Cifra um array de bytes utilizando uma chave pública RSA, em modo ECB.
     * @param text Texto (em bytes) a ser cifrado
     * @param key Chave pública codificada em bytes
     * @return Texto cifrado em formato de array de bytes.
     */
    public static byte[] encryptRSA(byte[] text, byte[] key){
        try {
            PublicKey pk = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(key));

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.PUBLIC_KEY, pk);

            byte[] ciphertext = cipher.doFinal(text);

            return ciphertext;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Decifra um array de bytes utilizando uma chave privada RSA.
     * @param ciphertext Texto cifrado para decifrar
     * @param key Chave privada RSA
     * @return Texto limpo em formato de array de bytes
     */
    public static byte[] decryptRSA(byte[] ciphertext, PrivateKey key){
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.PRIVATE_KEY, key);

            byte[] plaintext = cipher.doFinal(ciphertext);

            return plaintext;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Calcula o HMAC-SHA256 de um texto cifrado dado.
     * @param ciphertext Texto cifrado sobre o qual é calculado o HMAC
     * @param config Objeto config que contém a chave de integridade
     * @return HMAC calculado em formato de array de bytes
     */
    public static byte[] computeHMAC(byte[] ciphertext, Config config){
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            Key key = new SecretKeySpec(config.getIntegrityKey(), "HmacSHA256");
            mac.init(key);

            byte[] hmac = mac.doFinal(ciphertext);

            return hmac;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Verifica a integridade dos dados comparando os valores de HMAC.
     * @param ciphertext Texto cifrado sobre o qual é calculado o HMAC
     * @param config Objeto Config que contém a chave de integridade e o HMAC original
     * @return 'True' se a integridade foi mantida, 'False' caso contrário
     */
    public static boolean verifyHMAC(byte[] ciphertext, Config config){
        byte[] computedHMAC = computeHMAC(ciphertext, config);

        return Arrays.equals(computedHMAC, config.getHmac());
    }

    /**
     * Gera um array de bytes aleatório com o tamanho especificado em parâmetro.
     * É util para gerar um vetor de inicialização.
     * @param blockSize Tamanho do Array
     * @return Array de bytes aleatório
     */
    public static byte[] generateRandomBytes(int blockSize){
        byte[] bytes = null;

        try {
            SecureRandom randomSecureRandom = SecureRandom.getInstanceStrong();
            bytes = new byte[blockSize];
            randomSecureRandom.nextBytes(bytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return bytes;
    }

    /**
     * Gera uma chave simétrica aleatória de 256 bits para ser utilizada na cifra AES.
     * @return Chave simétrica AES de 256 bits.
     */
    public static byte[] generateAESKey(){
        byte[] key = null;

        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256);
            SecretKey s = keyGenerator.generateKey();
            key = s.getEncoded();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return key;
    }
}
