import java.io.*;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.ArrayList;

public class Client implements Runnable{

    private Socket clientSocket;
    public BufferedReader bufferedReader;
    private PrintWriter print_writer;
    static private int sessionID;
    private static boolean condition = true;

    Client(String IP, int port){ // w konstruktorze tworzymy socket o danym ip i porcie

        try {
            System.out.println("Waiting for connection...");

            clientSocket = new Socket(IP, port);

            bufferedReader=new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); // obiekt sluzacy do zczytywania danych
            print_writer = new PrintWriter(clientSocket.getOutputStream(),true); // obiekt sluzacy do wpisywania danych
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String generatePacket(String opeartion, String status, int sessionID, double number_1, double number_2)
    {
        // metoda sluzaca do wygenerowania odpowiedniego formatu (zdefiniowego w wymaganiach do zadania) pakietu tekstowego

        int number = 1;

        String packet ="";
        packet += "#" + number + ":O=" + opeartion + "+"; // pole operacji
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

    private void readPacket() { // metoda sluzaca do zczytywania pakietu przyslanego ze strony serwera
        try {
            char[] packet =new char[1024];

            int length= bufferedReader.read(packet);
            String packet1=new String(packet);

            String vpacket = packet1.substring(0,length);

            decode(vpacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendPacket(String opeartion, String status, int sessionID, double number_1, double number_2) {

        // metoda sluzaca do wysylania wygenerowanego pakietu do serwera

        print_writer.println(generatePacket(opeartion, status, sessionID, number_1, number_2));
    }

    private void decode(String packet){

        // metoda sluzaca do dekodowania otrzymanego pakietu, dzieki niej wyluskujemy wszystkie potrzebne nam dane
        // czyli: pole operacji, statusu, id sesji oraz liczby

        double number_1;

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
        sessionID = Integer.parseInt(hashtable.get("I"));

        if((arrayList.get(1).equals("A") | arrayList.get(1).equals("S") | arrayList.get(1).equals("M") |
                arrayList.get(1).equals("D") | arrayList.get(1).equals("P") | arrayList.get(1).equals("R")|arrayList.get(1).equals("F")|arrayList.get(1).equals("Z")|arrayList.get(1).equals("G"))
                && arrayList.get(3).equals("R") ) {

            System.out.println("Wynik = " + number_1);
        }
        if(arrayList.get(1).equals("W") &&arrayList.get(3).equals("W"))
        {
            System.out.println("Wynik jest za duzy");
        }
    }

    public static void main(String[] args){

        boolean statement = false;
        System.setProperty("line.separator", "");

        Client client = new Client("127.0.0.1", 1234);

        if(client.clientSocket != null){

            System.out.println("Polaczono z serwerem");
            new Thread(client).start();

            while(!statement)
            {
                System.out.println("Wpisz operacje,ktora chcesz wykonac");
                String message;
                Scanner scanner = new Scanner(System.in);
                message = scanner.nextLine();

                double number_1, number_2;

                // poniezej w switch - case znajduja sie instrukcje ktore maja sie wykonac po
                // podaniu odpowiedniej komendy
                switch(message)
                {
                    case "dodawanie":
                    {
                        System.out.println("Podaj pierwsza liczbe: ");
                        number_1 = scanner.nextDouble();
                        while(number_1>179769e+303)
                        {
                            System.out.println("liczba jest za duza");
                            number_1=scanner.nextDouble();
                        }
                        System.out.println("Podaj druga liczbe: ");
                        number_2=scanner.nextDouble();
                        while(number_2>179769e+303)
                        {
                            System.out.println("liczba jest za duza");
                            number_2=scanner.nextDouble();
                        }

                        client.sendPacket("A","C", sessionID, number_1, number_2);
                        break;
                    }
                    case "odejmowanie":
                    {
                        System.out.println("Podaj pierwsza liczbe: ");
                        number_1 = scanner.nextDouble();
                        while(number_1>179769e+303)
                        {
                            System.out.println("liczba jest za duza");
                            number_1=scanner.nextDouble();
                        }
                        System.out.println("Podaj druga liczbe: ");
                        number_2=scanner.nextDouble();
                        while(number_2>179769e+303)
                        {
                            System.out.println("liczba jest za duza");
                            number_2=scanner.nextDouble();
                        }
                        client.sendPacket("S","C", sessionID, number_1, number_2);
                        break;
                    }
                    case "mnozenie":
                    {
                        System.out.println("Podaj pierwsza liczbe: ");
                        number_1 = scanner.nextDouble();
                        while(number_1>179769e+303)
                        {
                            System.out.println("liczba jest za duza");
                            number_1=scanner.nextDouble();
                        }
                        System.out.println("Podaj druga liczbe: ");
                        number_2=scanner.nextDouble();
                        while(number_2>179769e+303)
                        {
                            System.out.println("liczba jest za duza");
                            number_2=scanner.nextDouble();
                        }
                        client.sendPacket("M","C", sessionID, number_1, number_2);
                        break;
                    }
                    case "dzielenie":
                    {
                        System.out.println("Podaj pierwsza liczbe: ");
                        number_1 = scanner.nextDouble();
                        System.out.println("Podaj druga liczbe: ");
                        number_2=scanner.nextDouble();
                        if(number_2==0)
                        {
                            System.out.println("Nie mozna dzielic przez zero, podaj inna liczbe");
                            number_2=scanner.nextDouble();
                            while(number_2==0)
                            {
                                System.out.println("Nie ma cwaniakowania");
                                number_2=scanner.nextDouble();
                            }
                        }
                        client.sendPacket("D","C", sessionID, number_1, number_2);
                        break;
                    }
                    case "potegowanie":
                    {
                        System.out.println("Podaj pierwsza liczbe: ");
                        number_1 = scanner.nextDouble();
                        while(number_1>179769e+303)
                        {
                            System.out.println("liczba jest za duza");
                            number_1=scanner.nextDouble();
                        }
                        System.out.println("Podaj druga liczbe: ");
                        number_2=scanner.nextDouble();
                        while(number_2>179769e+303)
                        {
                            System.out.println("liczba jest za duza");
                            number_2=scanner.nextDouble();
                        }
                        client.sendPacket("P","C", sessionID, number_1, number_2);
                        break;
                    }
                    case "modulo":
                    {
                        System.out.println("Podaj pierwsza liczbe: ");
                        number_1 = scanner.nextDouble();
                        while(number_1>179769e+303)
                        {
                            System.out.println("liczba jest za duza");
                            number_1=scanner.nextDouble();
                        }
                        System.out.println("Podaj druga liczbe: ");
                        number_2=scanner.nextDouble();
                        while(number_2>179769e+303)
                        {
                            System.out.println("liczba jest za duza");
                            number_2=scanner.nextDouble();
                        }
                        client.sendPacket("R","C", sessionID, number_1, number_2);
                        break;
                    }
                    case "logarytm":
                    {

                        System.out.println("Podaj pierwsza liczbe: ");
                        number_1 = scanner.nextDouble();
                        while(number_1>179769e+303)
                        {
                            System.out.println("liczba jest za duza");
                            number_1=scanner.nextDouble();
                        }
                        client.sendPacket("Z","C",sessionID,number_1,0);
                        break;

                    }
                    case "porownanie":
                    {
                        System.out.println("Podaj pierwsza liczbe: ");
                        number_1 = scanner.nextDouble();
                        while(number_1 > 179769e+303)
                        {
                            System.out.println("liczba jest za duza.");
                            number_1=scanner.nextDouble();
                        }
                        System.out.println("Podaj druga liczbe: ");
                        number_2=scanner.nextDouble();
                        while(number_2 > 179769e+303)
                        {
                            System.out.println("Liczba jest za duza.");
                            number_2 = scanner.nextDouble();
                        }
                        client.sendPacket("X","C", sessionID, number_1, number_2);
                        break;
                    }
                    case "silnia":
                    {
                        System.out.println("Podaj jedna liczbe: ");
                        number_1=scanner.nextDouble();

                        while(number_1 > 179769e+303)
                        {
                            System.out.println("Liczba jest za duza.");
                            number_1 = scanner.nextDouble();
                        }
                        client.sendPacket("F","C", sessionID, number_1,0);
                        break;
                    }
                    case "koniec":
                    {
                        client.sendPacket("E","E", sessionID,0,0);
                        condition = false;
                        statement = true;
                        try {
                            client.clientSocket.close();
                        }catch (IOException e) {
                            e.getMessage();
                        }
                        break;
                    }
                    default:
                    {
                        System.out.println("Podales zla komende. Sproboj jeszcze raz:");
                        break;
                    }
                }
            }
        } else{
            System.out.println("Nie mozna bylo polaczyc sie z serwerem.");
            condition = false;
        }
    }

    // metoda run, ktora jest wymagana do zaimplementowania, gdy uzywamy watkow;
    // znajuduje sie w niej petla ktora "nasluchuje" czy nadszedl jakis pakiet ze strony serwera
    // gdy nadejdzie jest on odczytywany i dekodowany
    public void run() {
        while(condition) {
            readPacket();
        }
    }
}