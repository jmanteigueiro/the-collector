package Model;

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

    public void add(String username, String password){
        this.add(new Credential(username, password.toCharArray()));
    }
}
