import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Hashtable;

public class Client implements Runnable{

    private Socket clientsocket;
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;
    private boolean condition = true;

    Client(ServerSocket serverSocket, int sessionID){
        try {
            clientsocket = serverSocket.accept();

            bufferedReader = new BufferedReader(new InputStreamReader(clientsocket.getInputStream())); // obiekt sluzacy do zczytywania danych
            printWriter = new PrintWriter(clientsocket.getOutputStream(), true); // obiekt sluzacy do wpisywania danych

            System.out.println("Polaczono z klientem o numerze ID: " + sessionID);

            sendPacket("Y","R", sessionID,0.0,0.0); // pakiet inicjalizujacy
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String generatePacket(String operation, String status, int sessionID, double number_1, double number_2)
    {
        // metoda sluzaca do wygenerowania odpowiedniego formatu (zdefiniowego w wymaganiach do zadania) pakietu tekstowego

        int number = 1;

        String packet = "";
        packet += "#" + number + ":O=" + operation + "+"; // pole operacji
        number++;
        packet += "#" + number + ":S=" + status + "+"; // pole statusu
        number++;
        packet += "#" + number + ":I=" + sessionID + "+"; // id sesji
        number++;
        packet += "#" + number + ":T=" + (System.currentTimeMillis() / 1000) + "+"; // znacznik czasu
        number++;
        packet += "#" + number + ":K=" + number_1 + "+"; // pierwsza liczba
        number++;
        packet += "#" + number + ":L=" + number_2 + "+"; // druga liczba

        return packet;
    }


    public void sendPacket(String operation, String status, int sessionID, double number_1, double number_2) {

        // metoda sluzaca do wysylania wygenerowanego pakietu do klienta

        printWriter.println(generatePacket(operation, status, sessionID, number_1, number_2));
    }

    private int factorial(int n) { // metoda obliczajaca silnie
        if (n == 0) return 1;
        else return n * factorial(n - 1);
    }

    private void decodePacket(String packet){

        // metoda sluzaca do dekodowania otrzymanego pakietu, dzieki niej wyluskujemy wszystkie potrzebne nam dane
        // czyli: pole operacji, statusu, id sesji oraz liczby

        double number_1;
        double number_2;
        int sessionID;

        ArrayList<String> arrayList = new ArrayList<>();

        Hashtable<String,String> hashtable = new Hashtable<>();

        String[] hashArray = packet.split("#");

        for(String w : hashArray){

            String[] equalsignArray = w.split("=");

            String[] colonArray = equalsignArray[0].split(":");

            if(colonArray.length == 2){
                String argument = equalsignArray[1].replace("+","");

                hashtable.put(colonArray[1],argument);

                arrayList.add(colonArray[1]);
                arrayList.add(argument);
            }
        }

        number_1 = Double.parseDouble(hashtable.get("K"));
        number_2 = Double.parseDouble(hashtable.get("L"));
        sessionID = Integer.parseInt(hashtable.get("I"));

        double result;

        // dodawanie
        if(arrayList.get(1).equals("A") && arrayList.get(3).equals("C")){
            result = number_1 + number_2;
            if(result>179769e+303)
            {
                sendPacket("W","W",sessionID,0,0);
            }

            sendPacket("A","R", sessionID, result,0);
        }
        // odejmowanie
        else if(arrayList.get(1).equals("S") && arrayList.get(3).equals("C")){
            result = number_1 - number_2;
            sendPacket("S","R", sessionID, result,0);
        }
        // mnozenie
        else if(arrayList.get(1).equals("M") && arrayList.get(3).equals("C")){
            result = number_1 * number_2;
            sendPacket("M","R", sessionID, result,0);
        }
        // dzielenie
        else if(arrayList.get(1).equals("D") && arrayList.get(3).equals("C")){
            result = number_1 / number_2;
            sendPacket("D","R", sessionID, result,0);
        }
        // potegowanie
        else if(arrayList.get(1).equals("P") && arrayList.get(3).equals("C")){
            result = Math.pow(number_1, number_2);
            sendPacket("P","R", sessionID, result,0);
        }
        // modulo
        else if(arrayList.get(1).equals("R") && arrayList.get(3).equals("C")){
            result = number_1 % number_2;
            sendPacket("R","R", sessionID, result,0);
        }
        // silnia
        else if(arrayList.get(1).equals("F") && arrayList.get(3).equals("C")){
            result = factorial((int)number_1);
            sendPacket("F","R", sessionID, result,0);
        }
        // logarytm
        else if(arrayList.get(1).equals("Z")&&arrayList.get(3).equals("C")) {
            result = Math.log(number_1);
            sendPacket("Z","R",sessionID,result,0);
        }
        // porownanie
        else if(arrayList.get(1).equals("X")&&arrayList.get(3).equals("C")) {
            if(number_1 > number_2) {
                result = number_1;
            }
            else {
                result = number_2;
            }
            sendPacket("G","R",sessionID,result,0);
        }
        else if(arrayList.get(1).equals("E")&&arrayList.get(3).equals("E")) {
            condition = false;
        }
    }

    private void readPacket() throws IOException { // metoda sluzaca do zczytywania pakietu przyslanego ze strony klienta

        char []packet = new char[1024];
        int length = bufferedReader.read(packet);

        String packet1 = new String(packet);
        String vpacket = packet1.substring(0,length);

        decodePacket(vpacket);
    }

    // metoda run, ktora jest wymagana do zaimplementowania, gdy uzywamy watkow;
    // znajuduje sie w niej petla ktora "nasluchuje" czy nadszedl jakis pakiet ze strony klienta
    // gdy nadejdzie jest on odczytywany i dekodowany
    public void run() {
        try{
            while(condition) {
                readPacket();
            }
        }catch (IOException e) {
            e.getMessage();
        }
    }
}

