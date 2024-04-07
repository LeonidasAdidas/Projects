import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Client {

    private Socket clientSocket;
    private BufferedReader reader;
    public PrintWriter writer;
    public User user;
    public List<Chat> chatList;
    public List<Client> clients;
    public Server server;

    public Client(Socket clientSocket, List<Chat> chatList, List<Client> clients, Server server) throws IOException {

        this.clientSocket = clientSocket;
        this.chatList = chatList;
        this.clients = clients;
        this.server = server;
        reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        writer = new PrintWriter(clientSocket.getOutputStream(), true);
    }

    public void closeSocket() throws IOException {
        clientSocket.close();
    }

    public DataPacket nextDataPacket() throws Exception {
        DataPacket dataPacket = null;
        Scanner scanner = new Scanner(System.in);
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(DataPacket.class, new DataTypeAdapter());
        gsonBuilder.registerTypeAdapter(Chat.class, new ChatTypeAdapter());
        gsonBuilder.registerTypeAdapter(User.class, new UserTypeAdapter());
        gsonBuilder.serializeNulls();
        Gson gson = gsonBuilder.create();

        while (true){

            String jsonMessage = reader.readLine();
            dataPacket = gson.fromJson(jsonMessage, DataPacket.class);

            if (dataPacket.dataType == DataType.CHATREQUEST || dataPacket.dataType == DataType.MESSAGE){
                switch (dataPacket.dataType){
                    case CHATREQUEST -> {
                        ChatRequestData chatRequestData = (ChatRequestData)dataPacket.content;
                        if(chatRequestData.name.equals(user.name)) {
                            throw new Exception("User tried to start chat with themselves");
                        }
                        DataPacket dataPacket2 = null;

                        boolean userFound = false;

                        System.out.println("clientsclientsclients" + clients.size());

                        for(Client client1 : clients) {
                            if(client1.user != null && client1.user.name.equals(chatRequestData.name)) {
                                userFound = true;

                                for(Chat chat : chatList) {
                                    if(chat.userList.contains(user) && chat.userList.contains(client1.user)){
                                        dataPacket2 = new DataPacket(0, DataType.CHATREQUEST, new ChatRequestData("Chat already exists!"));
                                        break;
                                    }
                                }
                                if(dataPacket2 != null) {
                                    break;
                                }

                                List<Client> newChatClientList = new ArrayList<>();
                                newChatClientList.add(client1);
                                newChatClientList.add(this);
                                Chat newChat = new Chat(newChatClientList, chatList.size() + 1);

                                DataPacket dataPacket3 = new DataPacket(0, DataType.NEWCHAT, new NewChatData(newChat));
                                jsonMessage = gson.toJson(dataPacket3);

                                for (Client client2 : newChatClientList) {
                                    client2.writer.println(jsonMessage);
                                }

                                chatList.add(newChat);

                                server.addNewChat(newChat);

                                dataPacket2 = new DataPacket(0, DataType.CHATREQUEST, new ChatRequestData("OK"));

                                break;
                            }
                        }
                        if(!userFound) {
                            dataPacket2 = new DataPacket(0, DataType.CHATREQUEST, new ChatRequestData("User not found"));

                        }
                        jsonMessage = gson.toJson(dataPacket2);
                        writer.println(jsonMessage);

                    }
                    case MESSAGE -> {
                        Message message = (Message) dataPacket.content;
                        System.out.println(message.text);
                        for(Chat chat : chatList) {
                            if(chat.chatID == message.chatID){

                                chat.messageHistory.add(message);
                                System.out.println("clientsclientsclients" + clients.size());

                                for (Client client1 : chat.clients){
                                    if(client1.user.userID != message.authorID){
                                        client1.writer.println(jsonMessage);
                                    }
                                }

                                break;
                            }
                        }
                        server.addNewMessage(message);
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
