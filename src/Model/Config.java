package Model;

import Security.Security;

import java.io.Serializable;

/**
 * Classe que contém os parâmetros de configuração da aplicação.
 */
public class Config implements Serializable {
    /*** Path do ficheiro que contém as credenciais. */
    private String fileCredentials;

    /*** Chave pública RSA de autenticação, que servirá para cifrar a chave simétrica e a chave de integridade */
    private byte[] authenticationPublicKey;

    /*** Chave simétrica AES de 256 bits */
    private byte[] symmetricKey;

    /*** Vetor de inicialização para cifra AES */
    private byte[] initVector;

    /*** Chave de integridade para calculo de HMAC-SHA256 */
    private byte[] integrityKey;

    /*** Último HMAC-SHA256 das credenciais calculado */
    private byte[] hmac;

    public Config(byte[] authenticationPublicKey, byte[] symmetricKey, byte[] initVector, byte[] integrityKey, byte[] hmac) {
        this.authenticationPublicKey = authenticationPublicKey;
        this.symmetricKey = symmetricKey;
        this.initVector = initVector;
        this.integrityKey = integrityKey;
        this.hmac = hmac;
    }

    public Config (String fileCredentials, byte[] authenticationPublicKey, byte[] symmetricKey, byte[] initVector, byte[] integrityKey, byte[] hmac){
        this.fileCredentials = fileCredentials;
        this.authenticationPublicKey = authenticationPublicKey;
        this.symmetricKey = symmetricKey;
        this.initVector = initVector;
        this.integrityKey = integrityKey;
        this.hmac = hmac;
    }

    public Config(){
        this.fileCredentials = "data.dat";
        this.symmetricKey = Security.generateAESKey();
        this.initVector = Security.generateRandomBytes(16);
        this.integrityKey = Security.generateAESKey();
    }

    public String getFileCredentials() {
        return fileCredentials;
    }

    public void setFileCredentials(String fileCredentials) {
        this.fileCredentials = fileCredentials;
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

    public byte[] getHmac() {
        return hmac;
    }

    public void setHmac(byte[] hmac) {
        this.hmac = hmac;
    }
}
