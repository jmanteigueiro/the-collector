package CryptoPackage;

import java.io.Serializable;

public class Notes implements Serializable {
    private byte[] data;
    private String iv;

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getIv() {
        return iv;
    }

    public void setIv(String iv) {
        this.iv = iv;
    }
}