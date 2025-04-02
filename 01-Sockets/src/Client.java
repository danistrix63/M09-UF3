import java.io.*;
import java.net.*;

public class Client {
    private static final int PORT = 7777;
    private static final String HOST = "localhost";
    private Socket socket;
    private PrintWriter out;

    public void connecta() throws IOException {
        socket = new Socket(HOST, PORT);
        System.out.println("Connectat a servidor en " + HOST + ":" + PORT);
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    public void enviaMissatges() throws IOException {
        BufferedReader teclat = new BufferedReader(new InputStreamReader(System.in));
        String missatge;

        while (true) {
            System.out.print("Escriu un missatge (enter buit per sortir): ");
            missatge = teclat.readLine();
            if (missatge == null || missatge.isEmpty()) break;
            out.println(missatge);
        }
    }

    public void tanca() throws IOException {
        if (out != null) out.close();
        if (socket != null) socket.close();
        System.out.println("Client tancat");
    }

    public static void main(String[] args) {
        Client client = new Client();
        try {
            client.connecta();
            client.enviaMissatges();
            client.tanca();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
