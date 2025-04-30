import java.io.*;

public class FilLectorCX extends Thread {
    private ObjectInputStream in;

    public FilLectorCX(ObjectInputStream in) {
        this.in = in;
    }

    @Override
    public void run() {
        try {
            String missatge;
            do {
                missatge = (String) in.readObject();
                System.out.println("Rebut: " + missatge);
            } while (!missatge.equals(ServidorXat.MSG_SORTIR));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
