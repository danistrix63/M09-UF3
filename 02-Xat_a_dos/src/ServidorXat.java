import java.io.*;
import java.net.*;

public class ServidorXat {
    private static final int PORT = 9999;
    private static final String HOST = "localhost";
    public static final String MSG_SORTIR = "sortir";

    private ServerSocket serverSocket;

    public void iniciarServidor() throws IOException {
        serverSocket = new ServerSocket(PORT);
        System.out.println("Servidor iniciat a " + HOST + ":" + PORT);
    }

    public void pararServidor() throws IOException {
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
            System.out.println("Servidor aturat.");
        }
    }

    public String getNom(ObjectInputStream in) throws IOException, ClassNotFoundException {
        return (String) in.readObject();
    }

    public static void main(String[] args) {
        try {
            ServidorXat servidor = new ServidorXat();
            servidor.iniciarServidor();

            Socket clientSocket = servidor.serverSocket.accept();
            System.out.println("Client connectat: " + clientSocket.getInetAddress());

            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());

            String nom = servidor.getNom(in);
            System.out.println("Nom rebut: " + nom);
            System.out.println("Fil de lectura iniciat");

            FilServidorXat fil = new FilServidorXat(in, nom);
            fil.start();

            System.out.println("Fil de " + nom + " iniciat");
            BufferedReader teclat = new BufferedReader(new InputStreamReader(System.in));
            String missatge;
            do {
                System.out.print("Missatge ('sortir' per tancar): ");
                missatge = teclat.readLine();
                out.writeObject(missatge);
                out.flush();
            } while (!missatge.equals(MSG_SORTIR));

            fil.join();
            in.close();
            out.close();
            clientSocket.close();
            System.out.println("sortir");
            servidor.pararServidor();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
