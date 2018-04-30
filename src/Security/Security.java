package Security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

/**
 * Esta classe contém os métodos de segurança necessários.
 */
public class Security {

    /**
     * Cifra um texto qualquer com o algoritmo AES-256 em modo CBC.
     * @param text Texto a cifrar em bytes
     * @param keyBytes Chave de cifra simétrica
     * @param ivBytes Vetor de inicialização
     * @return Texto cifrado em formato de array de bytes
     */
    public static byte[] encryptAES(byte[] text, byte[] keyBytes, byte[] ivBytes){
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");

            IvParameterSpec iv = new IvParameterSpec(ivBytes);
            SecretKeySpec sk = new SecretKeySpec(keyBytes, "AES");

            cipher.init(Cipher.ENCRYPT_MODE, sk, iv);

            return cipher.doFinal(text);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Decifra um qualquer array de bytes através de AES-256 em modo CBC.
     * @param ciphertext Array de bytes a decifrar
     * @param keyBytes Chafe de cifra simétrica
     * @param ivBytes Vetor de inicialização
     * @return Texto limpo original
     */
    public static byte[] decryptAES(byte[] ciphertext, byte[] keyBytes, byte[] ivBytes){
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");

            IvParameterSpec iv = new IvParameterSpec(ivBytes);
            SecretKeySpec sk = new SecretKeySpec(keyBytes, "AES");

            cipher.init(Cipher.DECRYPT_MODE, sk, iv);

            return cipher.doFinal(ciphertext);
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

            return cipher.doFinal(text);
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

            return cipher.doFinal(ciphertext);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Calcula o HMAC-SHA256 de um texto cifrado dado.
     * @param ciphertext Texto cifrado sobre o qual é calculado o HMAC
     * @param integrityKey Chave de integridade de 256 bits
     * @return HMAC calculado em formato de array de bytes
     */
    public static byte[] computeHMAC(byte[] ciphertext, byte[] integrityKey){
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            Key key = new SecretKeySpec(integrityKey, "HmacSHA256");
            mac.init(key);

            return mac.doFinal(ciphertext);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Verifica a integridade dos dados comparando os valores de HMAC.
     * @param ciphertext Texto cifrado sobre o qual é calculado o HMAC
     * @param hmac HMAC original, previamente calculado
     * @param integrityKey Chave de integridade de 256 bits
     * @return 'True' se a integridade foi mantida, 'False' caso contrário
     */
    public static boolean verifyHMAC(byte[] ciphertext, byte[] hmac, byte[] integrityKey){
        byte[] computedHMAC = computeHMAC(ciphertext, integrityKey);

        return Arrays.equals(computedHMAC, hmac);
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
    public static byte[] generate256BitKey(){
        byte[] key = null;

        try {
            SecureRandom secureRandom = SecureRandom.getInstanceStrong();
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256, secureRandom);
            SecretKey s = keyGenerator.generateKey();
            key = s.getEncoded();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return key;
    }

    public static PrivateKey privateKeyFromBytes(byte[] bytes){
        try {
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePrivate(new PKCS8EncodedKeySpec(bytes));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
