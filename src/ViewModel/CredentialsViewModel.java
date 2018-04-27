package ViewModel;

import Database.DataFile;
import Model.CredentialsList;

/**
 * Ligação entre a janela principal do programa (lista de credenciais) e os objetos.
 */
public class CredentialsViewModel {
    private String fileData = "data.dat";

    private CredentialsList credentials;

    public void loadCredentials(){
        DataFile file = new DataFile(fileData);

        credentials = file.loadJSON();

        file = null;
    }

    public void saveCredentials(){
        DataFile file = new DataFile(fileData);

        file.saveJSON(credentials);

        file = null;
        disposeCredentials();
    }

    private void disposeCredentials(){
        credentials.dispose();
        credentials = null;
    }
}