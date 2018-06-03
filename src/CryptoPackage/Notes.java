package CryptoPackage;

import java.io.Serializable;

public class Notes implements Serializable {
    private String data;
    private String iv;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getIv() {
        return iv;
    }

    public void setIv(String iv) {
        this.iv = iv;
    }
}