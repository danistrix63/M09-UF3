import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClientXat {
    private static final String HOST = "localhost";
    private static final int PORT = 9999;

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public void connecta() throws IOException {
        socket = new Socket(HOST, PORT);
        System.out.println("Client connectat a " + HOST + ":" + PORT);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        System.out.println("Flux d'entrada i sortida creat.");
    }

    public void enviarMissatge(String missatge) throws IOException {
        out.writeObject(missatge);
        out.flush();
        System.out.println("Enviant missatge: " + missatge);
    }

    public void tancarClient() throws IOException {
        if (socket != null && !socket.isClosed()) {
            socket.close();
            System.out.println("Client tancat.");
        }
    }

    public static void main(String[] args) {
        try {
            ClientXat client = new ClientXat();
            client.connecta();

            Scanner scanner = new Scanner(System.in);
            System.out.println("Missatge ('sortir' per tancar): Fil de lectura iniciat");
            System.out.print("Rebut: Escriu el teu nom: ");
            String nom = scanner.nextLine();
            client.enviarMissatge(nom);

            FilLectorCX fil = new FilLectorCX(client.in);
            fil.start();

            String missatge;
            do {
                System.out.print("Missatge ('sortir' per tancar): ");
                missatge = scanner.nextLine();
                client.enviarMissatge(missatge);
            } while (!missatge.equals(ServidorXat.MSG_SORTIR));

            fil.join();
            scanner.close();
            System.out.println("Tancant client...");
            client.tancarClient();
            System.out.println("El servidor ha tancat la connexió.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
