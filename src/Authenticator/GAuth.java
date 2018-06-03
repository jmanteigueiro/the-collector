package Authenticator;

import Model.Config;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.SecureRandom;



public class GAuth {

    public static String gkey;
    //public static String gkey;

    /**
     * Cria uma chave geradora secreta aleatória e guarda-a cifrada num ficheiro.
     * Esta chave é o segredo que vai ser partilhado entre o programa e as apps do Google Authenticator.
     * É a partir desta chave que são gerados os códigos temporários.
     *
     * Este método APENAS deve ser usado na PRIMEIRA VEZ que o programa é executado ou se for necessário usar gerar um novo 2FA.
     *
     * @param username  -> Nome do utilizador ou email
     * @param path      -> Localização do ficheiro que vai guardar a chave
     * @throws IOException
     */
    public static void NewGoogleAuthenticator(String username) throws IOException {

        String key = generateNewKey();

        getQRCode("TheCollector", username, key);

        gkey = key;
        //gkey = key;
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
    public static void getQRCode(String issuer, String user, String key) throws IOException {

        String format = "https://www.google.com/chart?chs=200x200&chld=M%%7C0&cht=qr&chl=otpauth://totp/%s+(%s)%%3Fsecret%%3D%s";

        try {
            InputStream img = new URL(String.format(format, issuer, user, key)).openStream();
            Files.copy(img, Paths.get("QRcode.png"), StandardCopyOption.REPLACE_EXISTING);
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
     * @param config
     * @param configkey
     * @param insertedTOTP  -> Chave temporaria introduzida pelo utilizador
     * @return True se o código introduzido estiver correto, False se o código estiver errado
     */
    public static boolean validateTOTPCode(String configkey, String insertedTOTP) throws UnsupportedEncodingException {



        try {
            if (insertedTOTP.equals(TOTPCode(configkey))){
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
