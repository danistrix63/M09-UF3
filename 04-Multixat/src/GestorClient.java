
import java.io.*;
import java.net.*;

public class GestorClient implements Runnable {
    private Socket clientSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private ServidorXat servidorXat;
    private String nom;
    private boolean sortir = false;

    // Constructor
    public GestorClient(Socket socket, ServidorXat servidorXat) throws IOException {
        this.clientSocket = socket;
        this.servidorXat = servidorXat;
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
    }

    public String getNom() {
        return nom;
    }

    // Método de ejecución
    public void run() {
        try {
            while (!sortir) {
                String missatge = (String) in.readObject();
                processaMissatge(missatge);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Procesar mensaje
    private void processaMissatge(String missatge) {
        String codi = Missatge.getCodiMissatge(missatge);
        switch (codi) {
            case Missatge.CODI_CONECTAR:
                String[] parts = Missatge.getPartsMissatge(missatge);
                nom = parts[1];
                servidorXat.afegirClient(this);
                break;
            case Missatge.CODI_SORTIR_CLIENT:
                servidorXat.eliminarClient(nom);
                break;
            case Missatge.CODI_SORTIR_TOTS:
                sortir = true;
                servidorXat.finalitzarXat();
                break;
            case Missatge.CODI_MSG_PERSONAL:
                parts = Missatge.getPartsMissatge(missatge);
                servidorXat.enviarMissatgePersonal(parts[1], nom, parts[2]);
                break;
            case Missatge.CODI_MSG_GRUP:
                servidorXat.enviarMissatgeGrup(missatge);
                break;
            default:
                System.out.println("Codi desconegut: " + codi);
        }
    }

    // Enviar mensaje
    public void enviarMissatge(String remitent, String missatge) {
        try {
            out.writeObject(missatge);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
