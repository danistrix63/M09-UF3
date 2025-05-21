import java.io.*;
import java.net.*;

public class Client {
    private static final String DIR_ARRIBADA = "/tmp"; // O equivalente en Windows
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public void conectar() throws IOException {
        // El cliente se conecta al servidor
        System.out.println("Connectant a -> localhost:9999");
        socket = new Socket("localhost", 9999);
        in = new ObjectInputStream(socket.getInputStream());
        out = new ObjectOutputStream(socket.getOutputStream());
        System.out.println("Connexio acceptada: " + socket.getInetAddress());
    }

    public void rebreFitxers() throws IOException, ClassNotFoundException {
        // Recibir el nombre del archivo del servidor
        String nomFitxer = (String) in.readObject();
        System.out.println("Nom del fitxer a rebre ('sortir' per sortir): " + nomFitxer);

        // Si el archivo no es nulo, se procede a recibir los datos del archivo
        if (nomFitxer != null && !nomFitxer.isEmpty()) {
            byte[] buffer = (byte[]) in.readObject(); // Leer el archivo en bytes
            System.out.println("Nom del fitxer a guardar: " + DIR_ARRIBADA + "/rebutFitxer");

            // Guardar el archivo recibido
            FileOutputStream fileOutputStream = new FileOutputStream(DIR_ARRIBADA + "/rebutFitxer");
            fileOutputStream.write(buffer);
            fileOutputStream.close();
            System.out.println("Fitxer rebut i guardat com: " + DIR_ARRIBADA + "/rebutFitxer");
        }
    }

    public void tancarConnexio() throws IOException {
        // Cerrar la conexión
        socket.close();
        System.out.println("Connexio tancada.");
    }

    public static void main(String[] args) {
        try {
            Client client = new Client();
            client.conectar();
            client.rebreFitxers();
            client.tancarConnexio();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
