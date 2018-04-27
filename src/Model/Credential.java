package Model;

import java.io.Serializable;

public class Credential implements Serializable {
    private String username;
    private char[] password;

    public Credential(String username, char[] password){
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public char[] getPassword() {
        return password;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }
}
