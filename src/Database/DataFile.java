package Database;

import Model.Credential;
import Model.CredentialsList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;

public class DataFile {
    private String filename;

    public DataFile(String filename){
        this.filename = filename;
    }

    public void saveJSON(CredentialsList list){
        try {
            Writer writer = new FileWriter(filename);
            Gson gson = new GsonBuilder().create();
            gson.toJson(list, writer);
            writer.close();
        }
        catch (Exception e){
            System.out.println("SaveJSON: " + e.getMessage());
        }
    }

    public CredentialsList loadJSON(){
        CredentialsList credentialsList = new CredentialsList();

        try {
            Gson gson = new Gson();
            Reader fileReader = new FileReader(filename);
            JsonReader reader = new JsonReader(fileReader);
            credentialsList = gson.fromJson(reader, credentialsList.getClass());
            fileReader.close();
        }
        catch (Exception e){
            System.out.println("LoadJSON: " + e.getMessage());
        }

        if (credentialsList == null)
            credentialsList = new CredentialsList();

        return credentialsList;
    }
}
