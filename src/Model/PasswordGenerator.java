package Model;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class PasswordGenerator {

    private int passSize;

    public PasswordGenerator(int size){
        this.passSize = size;
    }

    public String generator() throws NoSuchAlgorithmException {

        char[] elements = new char[93];
        // get ascii values
        for (int i = 0; i < 93 ; i++) {
            int n = i + 33;
            char c = (char)n;
            elements[i] = c;
        }

        StringBuilder pass = new StringBuilder(this.passSize);
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        for (int i = 0; i < this.passSize/4; i++) {
            int nextInt = secureRandom.nextInt(elements.length);
            pass.append(elements[nextInt]);

        }


        return new String(pass);
    }
}
