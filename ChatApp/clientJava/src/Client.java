import java.awt.*;
import java.net.Socket;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.swing.*;

public class Client {

    public User user;
    public Socket socket;
    public BufferedReader reader;
    public PrintWriter writer;
    public List<Chat> chatList;
    public Chat currentChat;
    public ChatUI chatUI;


    public Client() throws IOException {
        socket = new Socket("localhost", 12000);

        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream(), true);
        chatList = new ArrayList<>();
        chatUI = new ChatUI(this);
    }

    public DataPacket nextDataPacket() throws IOException {
        DataPacket dataPacket = null;
        Scanner scanner = new Scanner(System.in);
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(DataPacket.class, new DataTypeAdapter());
        gsonBuilder.serializeNulls();
        Gson gson = gsonBuilder.create();

        while (true){

            String jsonMessage = reader.readLine();
            dataPacket = gson.fromJson(jsonMessage, DataPacket.class);

            if (dataPacket.dataType == DataType.MESSAGE || dataPacket.dataType == DataType.NEWCHAT){
                switch (dataPacket.dataType){
                    case MESSAGE -> {
                        Message message = (Message) dataPacket.content;
                        for(Chat chat : chatList) {
                            if(chat.chatID == message.chatID){

                                if(chat.chatID == currentChat.chatID) {
                                    System.out.println("OR HERE");
                                    JLabel l = new JLabel(" " + message.authorName + ": " + message.text);
                                    l.setFont(new Font("Dialog", Font.PLAIN, 20));
                                    chatUI.chatMenuMessages.add(l);
                                    chatUI.verticalScrollBar.setValue(chatUI.verticalScrollBar.getMaximum());
                                    chatUI.revalidate();
                                    chatUI.repaint();
                                }

                                chat.messageHistory.add(message);

                                break;
                            }
                        }
                    }
                    case NEWCHAT -> {
                        Chat chat = ((NewChatData) dataPacket.content).newChat;
                        chatList.add(chat);
                        chatUI.renderMainMenu();
                    }


                }

            }
            else {
                break;
            }
        }
        return dataPacket;
    }
}