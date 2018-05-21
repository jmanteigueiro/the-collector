package Security;

import pt.gov.cartaodecidadao.*;

import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

    /*
        TODO: Generate nonce;
        TODO: nonce+counter (data? ou simples ctr)
        TODO: Guardar nas notas do CC
        --> Autenticação passa a necessitar do CC permanentemente
    */

public class PortugueseEID {

    private PTEID_EIDCard card;
    private PTEID_ReaderContext context;
    private PTEID_ReaderSet readerSet;
    private X509Certificate sign_certif;

    static {
        try {
            System.loadLibrary("pteidlibj");
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Native code library failed to load.\n" + e);
            System.exit(1);
        }
    }

    public PortugueseEID(){

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



    public boolean signNonce(String[] nonce, PublicKey pk) {

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
            nonces = hash.digest(nonce[0].getBytes("UTF-8"));
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

        // Testing --> should return 256
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
            sha256withRSA.update(nonce[0].getBytes("UTF-8"));
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
    private void writePublicKeyToFile(){

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
     * Get the Public Key from the PortugueseEID card
     * @return Public Key object
     */
    public PublicKey getPublicKey() {

        PTEID_Certificate signature = null;
        try {
            // Get the certificate from the card
            signature = card.getSignature();

            // Get the certificate data from the card
            byte[] cert = signature.getCertData().GetBytes();
        } catch (PTEID_Exception e) {
            e.printStackTrace();
        }

        try {
            // Create a CertificateFactory object
            CertificateFactory cf;

            // Instance it with X.509 certificate
            cf = CertificateFactory.getInstance("X.509");

            // Create a input stream with the certificate data
            InputStream fin = new ByteArrayInputStream(cert);

            // Get the certificate in the X.509 format
            sign_certif = (X509Certificate)cf.generateCertificate(fin);
        } catch (CertificateException e) {
            e.printStackTrace();
        }

        // Obtain the Public Key from the certificate
        return sign_certif.getPublicKey();
    }

    /**
     * Close the connection to the card.
     * Needs to be called in the end (when the connection is open)
     */
    public void closeConnection(){
        try {
            PTEID_ReaderSet.releaseSDK();
        } catch (PTEID_Exception e) {
            e.printStackTrace();
        }
    }

    /* Exemplo de uso das funções
    public static void main(String[] args) {
        PortugueseEID pid = new PortugueseEID();
        try {
            PublicKey pk2 = pid.getPublicKey();
            System.out.println(pk2);
            pid.signNonce(new String[]{"NONCE_GOES_HERE|COUNTER"}, pk2);
        } catch (PTEID_Exception e) {
            e.printStackTrace();
            System.out.println("Error: " + e);
        }
        pid.closeConnection();
    }
    */
}