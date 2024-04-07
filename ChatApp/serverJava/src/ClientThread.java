import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

public class ClientThread implements Runnable{
    private Client client;
    private List<Chat> chatList;
    private List<User> userList;
    private List<Client> clients;

    public ClientThread(Client client, List<Chat> chatList, List<User> userList, List<Client> clients){
        this.client = client;
        this.chatList = chatList;
        this.userList = userList;
        this.clients = clients;
    }

    @Override
    public void run() {
        try {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(DataPacket.class, new DataTypeAdapter());
            gsonBuilder.registerTypeAdapter(Chat.class, new ChatTypeAdapter());
            gsonBuilder.registerTypeAdapter(User.class, new UserTypeAdapter());
            gsonBuilder.serializeNulls();
            Gson gson = gsonBuilder.create();

//            System.out.println("HERE");

            String jsonMessage = "";
            DataPacket dataPacket = client.nextDataPacket();
            if(dataPacket.dataType != DataType.LOGIN) {
                throw new Exception("Didn't log in");
            }

            boolean userFound = false;
            NameData nameData = (NameData)dataPacket.content;
            for(User user : userList) {
                if(user.name.equals(nameData.name)) {
                    userFound = true;

                    List<Chat> partialChatList = new ArrayList<>();
                    for(Chat chat : chatList) {
                        if(chat.userList.contains(user)) {
                            partialChatList.add(chat);
                        }
                    }

                    DataPacket dataPacket1 = new DataPacket(0, DataType.CLIENTDATA, new ClientDumpData(user, partialChatList));
                    jsonMessage = gson.toJson(dataPacket1);

                    client.user = user;
                    userList.add(user);

                    break;
                }
            }

            if (!userFound) {
                User user = new User();
                user.name = nameData.name;
                user.userID = userList.size() + 1;

                DataPacket dataPacket1 = new DataPacket(0, DataType.CLIENTDATA, new ClientDumpData(user, new ArrayList<>()));
                jsonMessage = gson.toJson(dataPacket1);

                client.user = user;
                userList.add(user);

                client.server.addNewUser(user);
            }

            client.writer.println(jsonMessage);

//            SenderThread senderThread = new SenderThread(client, chatList, userList);
//            Thread sendThread = new Thread(senderThread);
//            sendThread.start();

            while (true) {

                DataPacket dataPacket1 = client.nextDataPacket();

                switch (dataPacket1.dataType) {
                    case CHATREQUEST -> {
                        User user1;
                        ChatRequestData chatRequestData = (ChatRequestData)dataPacket1.content;
                        if(chatRequestData.name.equals(client.user.name)) {
                            throw new Exception("User tried to start chat with themselves");
                        }
                        DataPacket dataPacket2 = null;

                        userFound = false;
                        for(Client client1 : clients) {
                            if(client1.user != null && client1.user.name.equals(chatRequestData.name)) {
                                userFound = true;

                                List<Client> newChatClientList = new ArrayList<>();
                                newChatClientList.add(client1);
                                newChatClientList.add(client);
                                Chat newChat = new Chat(newChatClientList, chatList.size() + 1);

                                DataPacket dataPacket3 = new DataPacket(0, DataType.NEWCHAT, new NewChatData(newChat));
                                jsonMessage = gson.toJson(dataPacket3);

                                for (Client client2 : newChatClientList) {
                                    client2.writer.println(jsonMessage);
                                }

                                chatList.add(newChat);

                                client.server.addNewChat(newChat);

                                dataPacket2 = new DataPacket(0, DataType.CHATREQUEST, new ChatRequestData("OK"));


                                break;
                            }
                        }
                        if(!userFound) {
                            dataPacket2 = new DataPacket(0, DataType.CHATREQUEST, new ChatRequestData("User not found"));

                        }
                        jsonMessage = gson.toJson(dataPacket2);
                        client.writer.println(jsonMessage);

                    }
                    case MESSAGE -> {
                        Message message = (Message) dataPacket.content;
                        for(Chat chat : chatList) {
                            if(chat.chatID == message.chatID){

                                chat.messageHistory.add(message);

                                for (Client client1 : clients){
                                    if(client1.user.userID != message.authorID){
                                        client1.writer.println(jsonMessage);
                                    }
                                }

                                break;
                            }
                        }

                        client.server.addNewMessage(message);

                    }

                }











//                jsonMessage = client.reader.readLine();
//                System.out.println(jsonMessage);
//                System.out.println("MESSAGE RECEIVED");
//                Message message = gson.fromJson(jsonMessage, Message.class);
//                chat.messageHistory.add(message);
//
//                for(Client client:chat.clients) {
//                    client.writer.println(jsonMessage);
//                }








            }
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }
}
