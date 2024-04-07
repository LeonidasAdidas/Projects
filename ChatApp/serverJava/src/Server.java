import java.awt.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Server {

    private ServerSocket serverSocket;
    private int chatCount;
    private List<Message> newMessages;
    private List<User> newUsers;
    private List<Chat> newChats;


    public Server() throws IOException {
        serverSocket = new ServerSocket(12000);
        chatCount = 0;
        newMessages = new ArrayList<>();
        newUsers = new ArrayList<>();
        newChats = new ArrayList<>();



    }

    public Socket acceptClient() throws IOException {
        return serverSocket.accept();
    }

    public void closeServer() throws IOException {
        serverSocket.close();
    }

    public int giveUniqueChatID() {
        chatCount++;
        return chatCount;
    }

    public void addNewMessage(Message message) {
        newMessages.add(message);
    }
    public void addNewUser(User user) {
        newUsers.add(user);
    }
    public void addNewChat(Chat chat) {
        newChats.add(chat);
    }

    public List<Message> getNewMessages() {
        return newMessages;
    }

    public List<User> getNewUsers() {
        return newUsers;
    }

    public List<Chat> getNewChats() {
        return newChats;
    }
}
