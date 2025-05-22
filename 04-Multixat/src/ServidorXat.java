import java.io.IOException;
import java.net.*;
import java.util.Hashtable;

public class ServidorXat {
    private static final int PORT = 9999;
    private static final String HOST = "localhost";
    private static final String MSG_SORTIR = "sortir";
    private Hashtable<String, GestorClient> clients = new Hashtable<>();
    private boolean sortir = false;
    private ServerSocket serverSocket;

    public void servidorAEscoltar() throws IOException {
        serverSocket = new ServerSocket(PORT);
        System.out.println("Servidor iniciat a " + HOST + ":" + PORT);
        while (!sortir) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connectat: " + clientSocket.getInetAddress());
            GestorClient gestor = new GestorClient(clientSocket, this);
            new Thread(gestor).start();
        }
    }

    public void pararServidor() throws IOException {
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }
    }

    public void finalitzarXat() {
        enviarMissatgeGrup(MSG_SORTIR);
        System.out.println("DEBUG: multicast sortir");
        clients.clear();
        sortir = true;
        try {
            pararServidor();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    public synchronized void afegirClient(GestorClient client) {
        clients.put(client.getNom(), client);
        System.out.println("DEBUG: multicast Entra: " + client.getNom());
        enviarMissatgeGrup(client.getNom() + " s'ha connectat.");
    }

    public synchronized void eliminarClient(String nom) {
        if (clients.containsKey(nom)) {
            clients.remove(nom);
        }
    }

    // Enviar missatge de grup (ServidorXat.java)
    public synchronized void enviarMissatgeGrup(String missatge) {
        String codificat = Missatge.getMissatgeGrup(missatge);
        for (GestorClient client : clients.values()) {
            client.enviarMissatge("Servidor", codificat);
        }
    }

    // Enviar missatge personal (ServidorXat.java)
        public void enviarMissatgePersonal(String destinatari, String remitent, String missatge) {
        GestorClient client = clients.get(destinatari);
        if (client != null) {
            String missatgeFinal = "Missatge personal per (" + destinatari + ") de (" + remitent + "): " + missatge;
            String codificat = Missatge.getMissatgePersonal(destinatari, missatgeFinal);
            client.enviarMissatge(remitent, codificat);
        }
    }
    public static void main(String[] args) {
        try {
            ServidorXat servidor = new ServidorXat();
            servidor.servidorAEscoltar();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
