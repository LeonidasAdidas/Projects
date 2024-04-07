import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.swing.*;
import java.awt.*;
import java.util.Scanner;

public class ReceiverThread implements Runnable{
    private Client client;
    private boolean needed;

    public ReceiverThread(Client client){
        this.client = client;
        needed = false;
    }

    @Override
    public void run() { // check if it is a new message or new chat
        try {
            Scanner scanner = new Scanner(System.in);
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(DataPacket.class, new DataTypeAdapter());
            Gson gson = gsonBuilder.create();
            DataPacket dataPacket = null;

            while (true) {
                while(!client.reader.ready() || !needed) {}

                String jsonMessage = client.reader.readLine();
                dataPacket = gson.fromJson(jsonMessage, DataPacket.class);
                if (dataPacket.dataType == DataType.MESSAGE || dataPacket.dataType == DataType.NEWCHAT || dataPacket.dataType == DataType.CHATREQUEST){
                    switch (dataPacket.dataType){
                        case MESSAGE -> {
                            Message message = (Message) dataPacket.content;
                            for(Chat chat : client.chatList) {
                                if(chat.chatID == message.chatID){

                                    if(client.currentChat != null && chat.chatID == client.currentChat.chatID) {
                                        JLabel l = new JLabel(" " + message.authorName + ": " + message.text);
                                        l.setFont(new Font("Dialog", Font.PLAIN, 20));
                                        client.chatUI.chatMenuMessages.add(l);
                                        client.chatUI.revalidate();
                                        client.chatUI.repaint();
                                        client.chatUI.verticalScrollBar.setValue(client.chatUI.verticalScrollBar.getMaximum());
                                    }

                                    chat.messageHistory.add(message);

                                    break;
                                }
                            }
                        }
                        case NEWCHAT -> {
                            Chat chat = ((NewChatData) dataPacket.content).newChat;
                            client.chatList.add(chat);
                            client.chatUI.renderMainMenu();
                        }
                        case CHATREQUEST -> {
                            if(!((ChatRequestData)dataPacket.content).name.equals("OK")) {
                                client.chatUI.newChat.add(new JLabel(((ChatRequestData)dataPacket.content).name));
                                client.chatUI.validate();
                            }
                            Thread.sleep(2000);
                            client.chatUI.toMainMenu();
                        }


                    }

                }
                else {
                    System.out.println("SOMETHING WENT WRONG THE RECEIVER THREAD READ SOMETHING ELSE THAN JUST MESSAGES, NEW CHATS OR CHAT REQUESTS");
                    System.out.println(dataPacket.content);
                }
            }
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }

    public void turnOn(){
        needed = true;
    }
    public void turnOff(){
        needed = false;
    }


}
