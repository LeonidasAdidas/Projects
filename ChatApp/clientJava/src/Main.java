import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.io.*;

import com.google.gson.Gson;

//Scanner scanner = new Scanner(System.in);
//String message = scanner.nextLine();A

public class Main {

    public static void main(String[] args) throws IOException {

        Client client = new Client();

        MainThread mainThread = new MainThread(client);
        Thread sendThread = new Thread(mainThread);
        sendThread.start();

//        new ChatUI(new Client());


    }
}