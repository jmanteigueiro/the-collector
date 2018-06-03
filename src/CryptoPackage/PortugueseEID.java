package CryptoPackage;

import Data.Helpers.GsonHelpers;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.scene.control.Alert;
import javafx.scene.layout.Region;
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
            Alert alert = new Alert(Alert.AlertType.ERROR, "Wasn't possible to find the Citizen Card API.\n Install the Citizen Card official application and try again.");
            alert.setTitle("CC API not found");
            alert.setResizable(false);

            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.setHeaderText("Could not load necessary libraries");
            //alert.setContentText("Insert Citizen Card, or verify that it is correctly inserted, then open this application again.");
            alert.showAndWait();
            System.exit(1001);
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
                Alert alert = new Alert(Alert.AlertType.ERROR, "Wasn't possible to obtain information from the card.\nInsert a card and relaunch the app.");
                alert.setTitle("Card not found");
                alert.setResizable(false);

                alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                alert.setHeaderText("Citizen Card was not found");
                //alert.setContentText("Insert Citizen Card, or verify that it is correctly inserted, then open this application again.");
                alert.showAndWait();
                closeConnection();
                System.exit(1000);
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
            System.exit(5000);
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
            System.exit(5001);
        }

        // Init the Signature with the public key
        try {
            assert sha256withRSA != null;
            sha256withRSA.initVerify(pk);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            System.exit(5002);
        }

        // Pass the nonce to be verified
        try {
            sha256withRSA.update(nonce.getBytes("UTF-8"));
        } catch (SignatureException | UnsupportedEncodingException e) {
            e.printStackTrace();
            System.exit(5003);
        }

        // Obtain the result. True if the signature is valid, false if otherwise
        boolean result = false;
        try {
            assert pBAsignedNonce != null;
            result = sha256withRSA.verify(pBAsignedNonce.GetBytes());
        } catch (SignatureException e) {
            e.printStackTrace();
            System.exit(5004);
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

    public String getName() throws PTEID_Exception {
        return card.getID().getGivenName() + card.getID().getSurname();
    }

    /**
     * Retrieves the public key from a file
     *
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

    public String getOwnerName() {
        String capitalizedName = null;
        try {
            capitalizedName = card.getID().getGivenName();
        } catch (PTEID_Exception e) {
            e.printStackTrace();
        }
        String[] substrings = capitalizedName.split(" ");
        StringBuilder sb = new StringBuilder("");
        for (String substring : substrings) {
            sb.append(substring.substring(0, 1)).append(substring.substring(1, substring.length()).toLowerCase()).append("_");
        }
        sb.deleteCharAt(sb.length()-1);
        return sb.toString();
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
     *
     * @param symmetricKey symmetric Key in the format of String
     * @param integrityKey integrity key in the format of String
     * @return true or false depending if the data was written to the card or not
     */
    public boolean writeKeysToCC(byte[] symmetricKey, byte[] integrityKey, byte[] cipherKey, byte[] cipherSalt) {
        DBKeys dbKeys = new DBKeys();
        Notes notes = new Notes();
        dbKeys.setIntegrityKey(Base64.getEncoder().encodeToString(integrityKey));
        dbKeys.setSymmetricKey(Base64.getEncoder().encodeToString(symmetricKey));

        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        String keys = gson.toJson(dbKeys);

        byte[] cipherIv = Security.generateRandomBytes(16);

        byte[] ciphered = Security.encryptAES(keys.getBytes(), cipherKey, cipherIv);

        notes.setData(ciphered);
        notes.setSalt(cipherSalt);
        notes.setIv(cipherIv);

        String sNotes = gson.toJson(notes);
        sNotes = sNotes + "                         ";

        // Encode it to Base64
        //String encoded = Base64.getEncoder().encodeToString(notes_to_encode);
        //String encoded = Base64.getEncoder().encodeToString(notes_to_encode.getBytes());


        // Create a PTEID_ByteArray with the data
        PTEID_ByteArray pb = new PTEID_ByteArray(sNotes.getBytes(), sNotes.getBytes().length);

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

    public DBKeys getKeysFromCC(byte[] cipherKey) {
        // Initiate a DBKeys to contain both keys
        DBKeys keys = new DBKeys();

        Notes notes = new Notes();

        try {
            // Read the data from the card
            Gson gson = new GsonBuilder().disableHtmlEscaping().create();

            String dataRead = card.readPersonalNotes();

            byte[] decoded = Base64.getDecoder().decode(dataRead);

            notes = gson.fromJson(new String(decoded), notes.getClass());

            String deciphered = new String( Security.decryptAES(notes.getData(), cipherKey, notes.getIv()) );

            keys = gson.fromJson(deciphered, keys.getClass());

        } catch (PTEID_Exception e) {
            e.printStackTrace();
            keys = null;
        }

        return keys;
    }

    public Notes getNotesFromCC(){
        Notes notes = new Notes();

        try {
            // Read the data from the card
            Gson gson = new GsonBuilder().disableHtmlEscaping().create();

            String s = card.readPersonalNotes();

            //byte[] decoded = Base64.getDecoder().decode(dataRead);

            notes = gson.fromJson(s, notes.getClass());

        } catch (PTEID_Exception e) {
            e.printStackTrace();
            notes = null;
        }

        return notes;
    }

    public static DBKeys decryptKeysFromNotes(Notes notes, byte[] cipherKey){
        DBKeys keys = new DBKeys();

        String deciphered = new String( Security.decryptAES(notes.getData(), cipherKey, notes.getIv()) );

        Gson gson = new GsonBuilder().disableHtmlEscaping().create();

        keys = gson.fromJson(deciphered, keys.getClass());

        return keys;
    }


    /**
     * Close the connection to the card.
     * Needs to be called in the end (when the connection is open)
     *
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

    /**
     * Sign a string using CC
     * @param bytes value to be signed
     * @return digital signature
     */
    public byte[] signBytes(byte[] bytes) {

        // Get SHA-256
        MessageDigest hash = null;
        try {
            hash = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        // Hash the nonce with SHA-256 before signing
        byte[] hashBytes = new byte[0];
        assert hash != null;
        hashBytes = hash.digest(bytes);

        // Create a PTEID_ByteArray with the hashed nonce
        PTEID_ByteArray pBAhashedNonce = new PTEID_ByteArray(hashBytes, hashBytes.length);

        // Sign the PTEID_ByteArray
        PTEID_ByteArray pBAsignature = null;
        try {
            pBAsignature = card.Sign(pBAhashedNonce, true);
        } catch (PTEID_Exception e) {
            e.printStackTrace();
            System.exit(5009);
        }

        return pBAsignature.GetBytes();
    }

    /**
     * Verify a signature using CC
     * @param value value previously signed
     * @param signature signature bytes
     * @return true if signature is verified, false otherwise
     */
    public boolean verifySignature(byte[] value, byte[] signature) {
        // Get Public Key from the card
        PublicKey pk = getPublicKey();

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
            sha256withRSA.update(value);
        } catch (SignatureException e) {
            e.printStackTrace();
        }

        // Obtain the result. True if the signature is valid, false if otherwise
        boolean result = false;
        try {
            assert signature != null;
            result = sha256withRSA.verify(signature);
        } catch (SignatureException e) {
            e.printStackTrace();
        }

        // Return the result
        return result;
    }
}