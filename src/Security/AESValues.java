package Security;

public class AESValues {
    private byte[] key;
    private byte[] iv;
    private byte[] ciphertext;

    public AESValues(byte[] key, byte[] iv, byte[] ciphertext) {
        this.key = key;
        this.iv = iv;
        this.ciphertext = ciphertext;
    }

    public byte[] getKey() {
        return key;
    }

    public byte[] getIv() {
        return iv;
    }

    public byte[] getCiphertext() {
        return ciphertext;
    }
}
