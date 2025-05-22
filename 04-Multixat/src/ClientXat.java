import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientXat {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean sortir = false;

    public void connecta(String host, int port) throws IOException {
        socket = new Socket(host, port);
        out = new ObjectOutputStream(socket.getOutputStream());
        System.out.println("Client connectat a " + host + ":" + port);
        System.out.println("Flux d'entrada i sortida creat.");
    }

    public void enviarMissatge(String missatge) {
        try {
            out.writeObject(missatge);
        } catch (IOException e) {
            System.out.println("Error enviant missatge.");
        }
    }

    public void tancarClient() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
            System.out.println("Tancant client...");
        } catch (IOException e) {
            System.out.println("Error tancant el client.");
        }
    }

    public void ajuda() {
        System.out.println("---------------------");
        System.out.println("Comandes disponibles:");
        System.out.println("1.- Conectar al servidor (primer pass obligatori)");
        System.out.println("2.- Enviar missatge personal");
        System.out.println("3.- Enviar missatge al grup");
        System.out.println("4.- (o línia en blanc)-> Sortir del client");
        System.out.println("5.- Finalitzar tothom");
        System.out.println("---------------------");
    }

    public String getLinea(Scanner sc, String missatge, boolean obligatori) {
        String linea;
        do {
            System.out.print(missatge);
            linea = sc.nextLine();
        } while (obligatori && linea.trim().isEmpty());
        return linea;
    }

    public void iniciarLectura() {
        Thread lector = new Thread(() -> {
            try {
                in = new ObjectInputStream(socket.getInputStream());
                System.out.println("DEBUG: Iniciant rebuda de missatges...");
                while (!sortir) {
                    String missatgeCru = (String) in.readObject();
                    String codi = Missatge.getCodiMissatge(missatgeCru);
                    String[] parts = Missatge.getPartsMissatge(missatgeCru);
                    switch (codi) {
                        case Missatge.CODI_SORTIR_TOTS:
                            System.out.println("Tancant tots els clients.");
                            sortir = true;
                            break;
                        case Missatge.CODI_MSG_PERSONAL:
                             System.out.println(parts[1]);
                            break;
                        case Missatge.CODI_MSG_GRUP:
                            System.out.println("Missatge grupal: " + parts[1]);
                            break;
                        default:
                            System.out.println("Codi desconegut: " + codi);
                    }
                }
            } catch (Exception e) {
                System.out.println("Error rebent missatge. Sortint...");
            } finally {
                tancarClient();
            }
        });
        lector.start();
    }

    public static void main(String[] args) {
        ClientXat client = new ClientXat();
        Scanner sc = new Scanner(System.in);

        try {
            client.connecta("localhost", 9999);
            client.iniciarLectura();
            client.ajuda();

            while (!client.sortir) {
                String opcio = sc.nextLine().trim();
                if (opcio.isEmpty() || opcio.equals("4")) {
                    client.enviarMissatge("1003#Adéu");
                    client.sortir = true;
                } else if (opcio.equals("1")) {
                    String nom = client.getLinea(sc, "Introdueix el nom: ", true);
                    client.enviarMissatge("1000#" + nom);
                } else if (opcio.equals("2")) {
                    String desti = client.getLinea(sc, "Destinatari:: ", true);
                    String msg = client.getLinea(sc, "Missatge a enviar: ", true);
                    client.enviarMissatge("1001#" + desti + "#" + msg);
                } else if (opcio.equals("3")) {
                    String msg = client.getLinea(sc, "Missatge al grup: ", true);
                    client.enviarMissatge("1002#" + msg);
                } else if (opcio.equals("5")) {
                    client.enviarMissatge("0000#Adéu");
                    client.sortir = true;
                } else {
                    client.ajuda();
                }
            }
        } catch (IOException e) {
            System.out.println("Error de connexió.");
        } finally {
            client.tancarClient();
        }
    }
}
