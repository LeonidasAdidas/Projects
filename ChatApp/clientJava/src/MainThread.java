import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.swing.*;

public class MainThread implements Runnable{
    private Client client;
    private Thread receiveThread;
    private ReceiverThread receiverThread;

    public MainThread(Client client){
        this.client = client;

        receiverThread = new ReceiverThread(client);
        receiveThread = new Thread(receiverThread);

    }

    @Override
    public void run() { /// newly created chats sending messages fucks up login, and it can try to append it to nonexistent message histories
        try {
            Scanner scanner = new Scanner(System.in);
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(DataPacket.class, new DataTypeAdapter());
            Gson gson = gsonBuilder.create();

            String name = client.chatUI.getName();

            DataPacket dataPacket = new DataPacket(DataType.LOGIN, new NameData(name));
            String jsonMessage = gson.toJson(dataPacket);
            client.writer.println(jsonMessage);


            dataPacket = client.nextDataPacket();

            ClientDumpData clientDumpData = (ClientDumpData)dataPacket.content;

            client.user = clientDumpData.user;
            client.chatList = clientDumpData.chatList;

            receiveThread.start();
            receiverThread.turnOn();

            while (true) {

                if(client.currentChat == null) {

                    client.chatUI.renderMainMenu();
                    client.chatUI.toMainMenu();


                    int inputInd = client.chatUI.getMenuIndex();

                    int ind = client.chatList.size();

                    DataPacket dataPacket1;
                    if(inputInd != ind) {

                        client.currentChat = client.chatList.get(inputInd);
                        client.chatUI.setUpChat();
                        client.chatUI.toChatMenu();

                    }
                    else if(inputInd == ind) {
                        client.chatUI.resetNewChat();
                        client.chatUI.toNewChat();

                        name = client.chatUI.getNewName();

                        dataPacket1 = new DataPacket(client.user.userID, DataType.CHATREQUEST, new ChatRequestData(name));
                        jsonMessage = gson.toJson(dataPacket1);
                        client.writer.println(jsonMessage);
                        Thread.sleep(2000);

                    }
                }
                else {
                    String chatMessage = client.chatUI.getChatMessage();
                    if(!chatMessage.equals("{[(_<EXIT>_)]}")){
                        JLabel l = new JLabel(" " + client.user.name + ": " + chatMessage);
                        l.setFont(new Font("Dialog", Font.PLAIN, 20));
                        client.chatUI.chatMenuMessages.add(l);
                        client.chatUI.revalidate();
                        client.chatUI.repaint();
                        client.chatUI.verticalScrollBar.setValue(client.chatUI.verticalScrollBar.getMaximum());

                        DataPacket dataPacket1 = new DataPacket(client.user.userID, DataType.MESSAGE, new Message(
                                client.user.userID, client.user.name, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), chatMessage, client.currentChat.chatID
                        ));
                        client.currentChat.messageHistory.add((Message) dataPacket1.content);
                        jsonMessage = gson.toJson(dataPacket1);
                        client.writer.println(jsonMessage);
                    }
                }
            }
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }
}
