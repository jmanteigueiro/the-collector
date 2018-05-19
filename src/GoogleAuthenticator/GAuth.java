package GoogleAuthenticator;

import Model.Config;
import Model.CredentialsList;
import Security.Security;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.PrivateKey;
import java.security.SecureRandom;



public class GAuth {

    public static byte[] gkey;

    /**
     * Cria uma chave geradora secreta aleatória e guarda-a cifrada num ficheiro.
     * Esta chave é o segredo que vai ser partilhado entre o programa e as apps do Google Authenticator.
     * É a partir desta chave que são gerados os códigos temporários.
     *
     * Este método APENAS deve ser usado na PRIMEIRA VEZ que o programa é executado ou se for necessário usar gerar um novo 2FA.
     *
     * @param username  -> Nome do utilizador ou email
     * @param path      -> Localização do ficheiro que vai guardar a chave
     * @param publickey -> Chave publica para encriptar a chave TOTP
     * @throws IOException
     */
    public static void NewGoogleAuthenticator(String username, String path) throws IOException {

        String key = generateNewKey();

        getQRCode("TheCollector", username, key, path);

        gkey = key.getBytes(StandardCharsets.ISO_8859_1);
    }


    /**
     * Gera uma secreta aleatória.
     *
     * Depende da biblioteca Apache Commons Codec v1.11 (Instalar com o maven) para o uso da Base32. Procurar "commons-codec:commons-codec:1.11"
     *
     * @return chave
     */
    public static String generateNewKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[50];
        random.nextBytes(bytes);
        Base32 base32 = new Base32();
        String key = base32.encodeToString(bytes);
        return key;
    }

    /**
     * Gera o QR code para ser lido na app do telemovel e guarda-o numa localização à escolha
     *
     * @param issuer   -> A entidade que requere o 2FA authentication
     * @param user     -> O nome/email do cliente da aplicação
     * @param key      -> A chave gerada em generateNewKey(), lida de um ficheiro encriptado
     * @param PNGPath  -> Localização do QR code
     * @throws IOException
     */
    public static void getQRCode(String issuer, String user, String key, String PNGPath) throws IOException {

        String format = "https://www.google.com/chart?chs=200x200&chld=M%%7C0&cht=qr&chl=otpauth://totp/%s+(%s)%%3Fsecret%%3D%s";

        try {
            InputStream img = new URL(String.format(format, issuer, user, key)).openStream();
            Files.copy(img, Paths.get(PNGPath+"QRcode.png"), StandardCopyOption.REPLACE_EXISTING);
        }catch (IOException e){

            throw new IOException(e);
        }
    }

    /**
     * Gera os códigos temporários sincronizados com a app.
     * Usa o algoritmo de TOTP autorizado pelo IETF
     *
     * @param key
     * @return chave temporária, a cada 30 segundos a chave muda
     */
    public static String TOTPCode(String key) {
        Base32 base32 = new Base32();
        byte[] bytes = base32.decode(key);
        String hexKey = Hex.encodeHexString(bytes);
        long time = (System.currentTimeMillis() / 1000) / 30;
        String hexTime = Long.toHexString(time);
        return TOTP.generateTOTP(hexKey, hexTime, "6");
    }


    /**
     * Valida os códigos temporários inseridos pelo utilizador (que este lê do telemóvel).
     *
     * @param key           -> Chave gerada por generateNewKey()
     * @param insertedTOTP  -> Chave temporaria introduzida pelo utilizador
     * @return True se o código introduzido estiver correto, False se o código estiver errado
     */
    public static Boolean validateTOTPCode(Config config, String insertedTOTP) throws UnsupportedEncodingException {
        byte[] ciphertext = new byte[2];
        ciphertext[0] = 1;
        ciphertext[1] = 1;

        //gkey = config.getGkey();
        gkey = ciphertext;


        String strKey = new String(gkey);
        System.out.println(strKey);

        try {
            if (insertedTOTP.equals(TOTPCode(strKey))){
                return true;
            }
            else
                return false;
        }
        catch (IllegalArgumentException e ){
            e.printStackTrace();
            return false;
        }

    }


}
