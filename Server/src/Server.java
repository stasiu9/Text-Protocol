import java.io.IOException;
import java.net.ServerSocket;
import java.util.Random;

public class Server {

    private ServerSocket serverSocket;
    private int sessionID;

    // w konstruktorze tworzymy serwer socket
    Server(int port){
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // funkcja ktora generuje identyfikator sesji (sessionID_1) w serwerze (zakres od 1 do 7)
    private void generate_sessionID(){

        Random random = new Random();
        sessionID = random.nextInt(7) + 1;
    }


    void start(){

        generate_sessionID();

        Client client1 = new Client(serverSocket, sessionID);

        Thread t1 = new Thread(client1);

        t1.start();

        t1.interrupt();

        try {
            serverSocket.close(); // zamkniecie serwer socketa
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        System.setProperty("line.separator","");
        Server server = new Server(1234);
        server.start();
    }
}
