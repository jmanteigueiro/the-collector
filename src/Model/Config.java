package Model;

import CryptoPackage.Security;
import Data.Helpers.ArrayHelpers;
import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Classe que contém os parâmetros de configuração da aplicação.
 */
public class Config implements Serializable {
    /*** Credenciais em bytes */
    private byte[] credentialsBytes;

    /*** Chave pública RSA de autenticação, que servirá para cifrar a chave simétrica e a chave de integridade */
    private byte[] authenticationPublicKey;

    /*** Chave simétrica AES de 256 bits */
    private transient byte[] symmetricKey;

    /*** Vetor de inicialização para cifra AES */
    private byte[] initVector;

    /*** Chave de integridade para calculo de HMAC-SHA256 */
    private transient byte[] integrityKey;

    /** Google Key*/
    private byte[] gkey;

    /*** Última assinatura digital das credenciais calculada, para efeitos de integridade */
    private byte[] digitalSignature;

    public Config(){
        this.symmetricKey = Security.generate256BitKey();
        this.initVector = Security.generateRandomBytes(16);
        this.integrityKey = Security.generate256BitKey();
    }

    public byte[] getAuthenticationPublicKey() {
        return authenticationPublicKey;
    }

    public void setAuthenticationPublicKey(byte[] authenticationPublicKey) {
        this.authenticationPublicKey = authenticationPublicKey;
    }

    public byte[] getSymmetricKey() {
        return symmetricKey;
    }

    public void setSymmetricKey(byte[] symmetricKey) {
        this.symmetricKey = symmetricKey;
    }

    public byte[] getInitVector() {
        return initVector;
    }

    public void setInitVector(byte[] initVector) {
        this.initVector = initVector;
    }

    public byte[] getIntegrityKey() {
        return integrityKey;
    }

    public void setIntegrityKey(byte[] integrityKey) {
        this.integrityKey = integrityKey;
    }

    public byte[] getDigitalSignature() {
        return digitalSignature;
    }

    public void setDigitalSignature(byte[] digitalSignature) {
        this.digitalSignature = digitalSignature;
    }

    public byte[] getCredentialsBytes() {
        return credentialsBytes;
    }

    public void setCredentialsBytes(byte[] credentialsBytes) {
        this.credentialsBytes = credentialsBytes;
    }

    public CredentialsList getCredentialsList() {
        if (credentialsBytes == null)
            return new CredentialsList();

        Gson gson = new Gson();
        CredentialsList credentialsList = new CredentialsList();

        credentialsList = gson.fromJson(new String(credentialsBytes), credentialsList.getClass());

        gson = null;
        return credentialsList;
    }

    public void setCredentialsList(CredentialsList credentialsList) {
        Gson gson = new Gson();
        credentialsBytes = gson.toJson(credentialsList).getBytes();
        gson = null;
    }

    public byte[] getGkey() {
        return gkey;
    }

    public void setGkey(byte[] gkey) {
        this.gkey = gkey;
    }

    public byte[] getBytesForDigitalSignature(){
        byte[] bytesFinal = ArrayHelpers.concat(credentialsBytes, authenticationPublicKey);
        bytesFinal = ArrayHelpers.concat(bytesFinal, initVector);
        bytesFinal = ArrayHelpers.concat(bytesFinal, gkey);
        return bytesFinal;
    }

    public Config clone(){
        Config newConfig = new Config();
        newConfig.setCredentialsList(this.getCredentialsList());
        newConfig.setAuthenticationPublicKey(this.getAuthenticationPublicKey());
        newConfig.setSymmetricKey(this.getSymmetricKey());
        newConfig.setIntegrityKey(this.getIntegrityKey());
        newConfig.setInitVector(this.getInitVector());
        newConfig.setDigitalSignature(this.getDigitalSignature());
        newConfig.setGkey(this.getGkey());
        return newConfig;
    }

}
