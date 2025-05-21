import java.io.*;

public class Fitxer {
    private String nom;
    private byte[] contingut;

    public Fitxer(String nom) {
        this.nom = nom;
        this.contingut = getContingut(nom);
    }

    public byte[] getContingut(String nomFitxer) {
        try {
            File file = new File(nomFitxer);
            byte[] buffer = new byte[(int) file.length()];
            FileInputStream fileInputStream = new FileInputStream(file);
            fileInputStream.read(buffer);
            return buffer;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getNom() {
        return nom;
    }

    public byte[] getContingut() {
        return contingut;
    }
}
