package Model;

import Security.Security;

public class Config {
    private String fileCredentials;
    private byte[] authenticationPublicKey;
    private byte[] simmetricKey;
    private byte[] initVector;

    public Config(byte[] authenticationPublicKey, byte[] simmetricKey, byte[] initVector) {
        this.authenticationPublicKey = authenticationPublicKey;
        this.simmetricKey = simmetricKey;
        this.initVector = initVector;
    }

    public Config (String fileCredentials, byte[] authenticationPublicKey, byte[] simmetricKey, byte[] initVector){
        this.fileCredentials = fileCredentials;
        this.authenticationPublicKey = authenticationPublicKey;
        this.simmetricKey = simmetricKey;
        this.initVector = initVector;
    }

    public Config(){
        this.fileCredentials = "data.dat";
        this.simmetricKey = Security.generateAESKey();
        this.initVector = Security.generateRandomBytes(16);
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

    public byte[] getSimmetricKey() {
        return simmetricKey;
    }

    public void setSimmetricKey(byte[] simmetricKey) {
        this.simmetricKey = simmetricKey;
    }

    public byte[] getInitVector() {
        return initVector;
    }

    public void setInitVector(byte[] initVector) {
        this.initVector = initVector;
    }
}
