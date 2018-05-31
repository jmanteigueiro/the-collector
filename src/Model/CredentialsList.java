package Model;

import Data.Helpers.GsonHelpers;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Classe que guarda todas as credenciais em memória.
 */
public class CredentialsList extends ArrayList<Credential> {

    /**
     * Eliminar seguramente a lista que contém as credenciais do utilizador.
     * Deve ser chamado quando as credenciais não vão ser mais utilizadas.
     */
    public void dispose() {
        for (Credential credential : this){
            credential.setPassword( new char[]{} );
            credential = null;
        }
    }

    public void addCredential(String website, String username, String password){
        this.add(new Credential(website, username, password.toCharArray()));
    }

    public void addCredential(Credential c){
        this.add(c);
    }

    public void addCredential(Credential c, int index){
        this.add(index, c);
    }

    /**
     * Transforms the list into a byte array of a json
     * @return
     */
    public byte[] toByteArray(){
        Gson gson = GsonHelpers.buildCustomGson();
        String string = gson.toJson(this);
        return string.getBytes();
    }
}
