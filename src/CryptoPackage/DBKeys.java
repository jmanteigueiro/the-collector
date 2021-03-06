package CryptoPackage;

import java.io.Serializable;

public class DBKeys implements Serializable{
    private String symmetricKey;
    private String integrityKey;

    public String getSymmetricKey() {
        return symmetricKey;
    }

    public void setSymmetricKey(String symmetricKey) {
        this.symmetricKey = symmetricKey;
    }

    public String getIntegrityKey() {
        return integrityKey;
    }

    public void setIntegrityKey(String integrityKey) {
        this.integrityKey = integrityKey;
    }
}
