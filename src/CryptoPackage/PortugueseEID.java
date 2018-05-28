package CryptoPackage;

import Data.Helpers.GsonHelpers;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import pt.gov.cartaodecidadao.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;


public class PortugueseEID {

    private PTEID_EIDCard card;
    private PTEID_ReaderContext context;
    private PTEID_ReaderSet readerSet;
    private X509Certificate sign_certif;

    // This is necessary otherwise it won't work.
    // Portuguese middleware should be installed, or atleast the pteidlibj present.
    static {
        //TODO: check what operating system is running this and change the location of the library

        // Linux
        try {
            System.loadLibrary("pteidlibj");
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Native code library failed to load.\n" + e);
        }
    }

    public PortugueseEID() {

        try {
            // Initiate the SDK
            // Needs to be called before doing any operation
            PTEID_ReaderSet.initSDK();
            card = null;

            // Get a ReaderSet instance
            readerSet = PTEID_ReaderSet.instance();

            for (int i = 0; i < readerSet.readerCount(); i++) {
                context = readerSet.getReaderByNum(i);
                if (context.isCardPresent()) {
                    // Get the card object
                    card = context.getEIDCard();
                }
            }

            if (card == null) {
                System.out.println("Wasn't able to obtain information from the card.");
                System.exit(1);
            }

            sign_certif = null;
        } catch (PTEID_Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Sign a nonce and verify it's signature with the public key
     *
     * @param nonce nonce to be signed, in the format of a string array
     * @param pk    public key used to verify the signature
     * @return boolean true if the signature is valid, false if otherwise
     */
    public boolean signNonceAndVerify(String nonce, PublicKey pk) {

        // Get SHA-256
        MessageDigest hash = null;
        try {
            hash = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        // Hash the nonce with SHA-256 before signing
        byte[] nonces = new byte[0];
        try {
            assert hash != null;
            nonces = hash.digest(nonce.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // Create a PTEID_ByteArray with the hashed nonce
        PTEID_ByteArray pBAhashedNonce = new PTEID_ByteArray(nonces, nonces.length);

        // Sign the PTEID_ByteArray
        PTEID_ByteArray pBAsignedNonce = null;
        try {
            pBAsignedNonce = card.Sign(pBAhashedNonce, true);
        } catch (PTEID_Exception e) {
            e.printStackTrace();
        }

        // TESTING
        // --> should return 256
        // System.out.println(pBAsignedNonce.Size());

        // Get a Signature object with SHA256withRSA (what the PortugueseEID card supports)
        Signature sha256withRSA = null;
        try {
            sha256withRSA = Signature.getInstance("SHA256withRSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        // Init the Signature with the public key
        try {
            assert sha256withRSA != null;
            sha256withRSA.initVerify(pk);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        // Pass the nonce to be verified
        try {
            sha256withRSA.update(nonce.getBytes("UTF-8"));
        } catch (SignatureException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // Obtain the result. True if the signature is valid, false if otherwise
        boolean result = false;
        try {
            assert pBAsignedNonce != null;
            result = sha256withRSA.verify(pBAsignedNonce.GetBytes());
        } catch (SignatureException e) {
            e.printStackTrace();
        }

        // Return the result
        return result;
    }

    /**
     * Write the Public Key to pk.pem
     * Called the first time someone authenticates, using a new card, and creates their password manager instance
     */
    public void writePublicKeyToFile() {

        FileOutputStream fileOutputStream = null;
        try {
            // Initiate a File Output Stream to "pk.pem"
            fileOutputStream = new FileOutputStream("pk.pem");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            assert fileOutputStream != null;

            // Write the public key to the file
            fileOutputStream.write(getPublicKey().getEncoded());

            // Close the output stream
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the public key from a file
     * @param pathToFile String with the path to the public key
     * @return PublicKey object
     */
    public PublicKey getPublicKeyFromFile(String pathToFile) {
        try {
            // Read the public key
            byte[] publicKeyBytes = Files.readAllBytes(Paths.get(pathToFile));

            // Create a X.509 key spec
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);

            // Get RSA instance
            KeyFactory kf = KeyFactory.getInstance("RSA");

            // Return the PublicKey object
            return kf.generatePublic(keySpec);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get the Public Key from the PortugueseEID card
     *
     * @return Public Key object
     */
    public PublicKey getPublicKey() {

        PTEID_Certificate signature = null;
        byte[] cert = null;
        try {
            // Get the certificate from the card
            signature = card.getSignature();

            // Get the certificate data from the card
            cert = signature.getCertData().GetBytes();
        } catch (PTEID_Exception e) {
            e.printStackTrace();
        }

        try {
            // Create a CertificateFactory object
            CertificateFactory cf;

            // Instance it with X.509 certificate
            cf = CertificateFactory.getInstance("X.509");

            // Create a input stream with the certificate data
            assert cert != null;
            InputStream fin = new ByteArrayInputStream(cert);

            // Get the certificate in the X.509 format
            sign_certif = (X509Certificate) cf.generateCertificate(fin);
        } catch (CertificateException e) {
            e.printStackTrace();
        }

        // Obtain the Public Key from the certificate
        return sign_certif.getPublicKey();
    }


    /**
     * Writes the keys to the CC personal notes field
     * @param symmetricKey symmetric Key in the format of String
     * @param integrityKey integrity key in the format of String
     * @return true or false depending if the data was written to the card or not
     */
    public boolean writeKeysToCC(String symmetricKey, String integrityKey){
        // Initiate a StringBuilder
        StringBuilder dataToWrite = new StringBuilder();

        // Append the data
        dataToWrite.append("SymmetricKey");
        dataToWrite.append(">>>>>>");
        dataToWrite.append(symmetricKey);
        dataToWrite.append(">>>>>>");
        dataToWrite.append("IntegrityKey");
        dataToWrite.append(">>>>>>");
        dataToWrite.append(integrityKey);

        // Encode it to Base64
        String encoded = Base64.getEncoder().encodeToString(dataToWrite.toString().getBytes());

        // Create a PTEID_ByteArray with the data
        PTEID_ByteArray pb = new PTEID_ByteArray(encoded.getBytes(), encoded.getBytes().length);

        // Flag to obtain the result
        boolean result = false;
        try {

            // Write the data to the card
            // If successful, result equals true
            // Else, result equals false
            result = card.writePersonalNotes(pb, card.getPins().getPinByPinRef(PTEID_Pin.AUTH_PIN));
        } catch (PTEID_Exception e) {
            e.printStackTrace();
        }

        // Return the result
        return result;
    }

    /**
     * Writes the keys to the CC personal notes field
     * @param symmetricKey symmetric Key in the format of String
     * @param integrityKey integrity key in the format of String
     * @return true or false depending if the data was written to the card or not
     */
    public boolean writeKeysToCC(byte[] symmetricKey, byte[] integrityKey){
        DBKeys dbKeys = new DBKeys();
            dbKeys.setIntegrityKey( Base64.getEncoder().encodeToString(integrityKey) );
            dbKeys.setSymmetricKey( Base64.getEncoder().encodeToString(symmetricKey) );

        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        String json = gson.toJson(dbKeys);

        // Encode it to Base64
        String encoded = Base64.getEncoder().encodeToString(json.getBytes());

        // Create a PTEID_ByteArray with the data
        PTEID_ByteArray pb = new PTEID_ByteArray(encoded.getBytes(), encoded.getBytes().length);

        // Flag to obtain the result
        boolean result = false;
        try {

            // Write the data to the card
            // If successful, result equals true
            // Else, result equals false
            result = card.writePersonalNotes(pb, card.getPins().getPinByPinRef(PTEID_Pin.AUTH_PIN));
        } catch (PTEID_Exception e) {
            e.printStackTrace();
        }

        // Return the result
        return result;
    }



    /**
     * Writes the keys to the CC personal notes field
     * @param keys keys object with both keys
     * @return true or false depending if the data was written to the card or not
     */
    public boolean writeKeysToCC(DBKeys keys) throws PTEID_Exception{
        // Initiate a StringBuilder
        StringBuilder dataToWrite = new StringBuilder();

        // Append the data
        dataToWrite.append("SymmetricKey");
        dataToWrite.append(">>>>>>");
        dataToWrite.append(keys.getSymmetricKey());
        dataToWrite.append(">>>>>>");
        dataToWrite.append("IntegrityKey");
        dataToWrite.append(">>>>>>");
        dataToWrite.append(keys.getIntegrityKey());

        // Encode it to Base64
        String encoded = Base64.getEncoder().encodeToString(dataToWrite.toString().getBytes());

        // Create a PTEID_ByteArray with the data
        PTEID_ByteArray pb = new PTEID_ByteArray(encoded.getBytes(), encoded.getBytes().length);

        // Flag to obtain the result
        boolean result = false;
        try {

            // Write the data to the card
            // If successful, result equals true
            // Else, result equals false
            result = card.writePersonalNotes(pb, card.getPins().getPinByPinRef(PTEID_Pin.AUTH_PIN));
        } catch (PTEID_Exception e) {
            e.printStackTrace();
        }

        // Return the result
        return result;
    }

    public DBKeys getKeysFromCC(){

        // Initiate a DBKeys to contain both keys
        DBKeys keys = new DBKeys();

        try {
            // Read the data from the card
            String dataRead = card.readPersonalNotes();

            String decoded = new String( Base64.getDecoder().decode(dataRead) );

            Gson gson = new GsonBuilder().disableHtmlEscaping().create();
            keys = gson.fromJson(decoded, keys.getClass());

        } catch (PTEID_Exception e) {
            e.printStackTrace();
        }

        return keys;
    }


    /**
     * Close the connection to the card.
     * Needs to be called in the end (when the connection is open)
     * @return true is connection is closed, false if otherwise
     */
    public boolean closeConnection() {
        try {
            PTEID_ReaderSet.releaseSDK();
        } catch (PTEID_Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

//    public static void main(String[] args) {
//        PortugueseEID pid = new PortugueseEID();
//         pid.writePublicKeyToFile();
//
//        pid.closeConnection();
//    }
//
//    public static void main(String[] args) {
//        PortugueseEID pid = new PortugueseEID();
//        // TESTING
//        PublicKey pk2 = pid.getPublicKey();
//        System.out.println(pk2);
//        pid.writePublicKeyToFile();
//        PublicKey pk1 = pid.getPublicKeyFromFile("pk.pem");
//        if(pk1.equals(pk2))
//            System.out.println("True");
//        else
//            System.out.println("False");
//
//
//        DBKeys keys = new DBKeys();
//        boolean writeResult;// = pid.writeKeysToCC("123", "345");
//        keys.setIntegrityKey("234");
//        keys.setSymmetricKey("567");
//        writeResult = pid.writeKeysToCC(keys);
//        System.out.println(writeResult);
//
//        pid.getKeysFromCC();
//        //System.out.println(keys.getIntegrityKey());
//        //System.out.println(keys.getSymmetricKey());
//        pid.closeConnection();
//    }

}