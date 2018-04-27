package Security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class Security {

    public static AESValues encryptAES(String text){
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");

            byte[] ivBytes = generateRandomBytes(cipher.getBlockSize());
            byte[] key = generateAESKey();

            IvParameterSpec iv = new IvParameterSpec(ivBytes);
            SecretKeySpec sk = new SecretKeySpec(key, "AES");

            cipher.init(Cipher.ENCRYPT_MODE, sk, iv);

            byte[] ciphertext = cipher.doFinal(text.getBytes());

            return new AESValues(key, ivBytes, ciphertext);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String decryptAES(byte[] keyBytes, byte[] ivBytes, byte[] ciphertext){
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");

            IvParameterSpec iv = new IvParameterSpec(ivBytes);
            SecretKeySpec sk = new SecretKeySpec(keyBytes, "AES");

            cipher.init(Cipher.DECRYPT_MODE, sk, iv);

            byte[] plaintext = cipher.doFinal(ciphertext);

            return new String(plaintext);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    private static byte[] generateRandomBytes(int blockSize){
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

    private static byte[] generateAESKey(){
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
