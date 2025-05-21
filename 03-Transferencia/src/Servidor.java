import java.io.*;
import java.net.*;

public class Servidor {
    private static final int PORT = 9999;
    private static final String HOST = "localhost";
    private ServerSocket serverSocket;
    private Socket socket;

    public void conectar() throws IOException {
        // El servidor acepta conexiones
        System.out.println("Acceptant connexions en -> " + HOST + ":" + PORT);
        serverSocket = new ServerSocket(PORT);
        System.out.println("Esperant connexio...");
        socket = serverSocket.accept();
        System.out.println("Connexio acceptada: " + socket.getInetAddress());
    }

    public void enviarFitxers(String nomFitxer) throws IOException {
        File file = new File(nomFitxer);

        // Verificación del archivo
        if (!file.exists() || file.isDirectory()) {
            System.out.println("Error llegint el fitxer del client: null");
            return;
        }

        // Información sobre el archivo
        System.out.println("Esperant el nom del fitxer del client...");
        System.out.println("Nomfitxer rebut: " + file.getAbsolutePath());
        System.out.println("Contingut del fitxer a enviar: " + file.length() + " bytes");

        // Enviar el archivo al cliente
        byte[] buffer = new byte[(int) file.length()];
        FileInputStream fileInputStream = new FileInputStream(file);
        fileInputStream.read(buffer);
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

        // Enviar el nombre del archivo primero
        out.writeObject(file.getAbsolutePath());
        out.flush(); // Asegurarse de que el nombre del archivo se envíe de inmediato

        // Luego enviar el contenido del archivo
        out.writeObject(buffer);
        out.flush(); // Asegurarse de que el archivo se envíe de inmediato
        System.out.println("Fitxer enviat al client: " + file.getAbsolutePath());
    }

    public void tancarConnexio() throws IOException {
        System.out.println("Tancant connexio amb el client:" + socket.getInetAddress());
        socket.close();
        serverSocket.close();
        System.out.println("Connexio tancada.");
    }

    public static void main(String[] args) throws IOException {
        Servidor servidor = new Servidor();
        servidor.conectar();
        servidor.enviarFitxers("/home/dani/M09-UF3/03-Transferencia/tmp/rebutFitxer.txt"); // Ruta del archivo
        servidor.tancarConnexio();
    }
}
