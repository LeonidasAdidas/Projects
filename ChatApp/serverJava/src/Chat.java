import java.util.ArrayList;
import java.util.List;

public class Chat extends Data{
    public int chatID;
    public List<User> userList;
    public List<Message> messageHistory;
    public List<Client> clients;

    public Chat(List<Client> clients, int chatID) {
        userList = new ArrayList<>();
        messageHistory = new ArrayList<>();
        this.clients = clients;
        this.chatID = chatID;
        for (Client client : clients) {
            userList.add(client.user);
        }
    }
    public Chat(int chatID) {
        userList = new ArrayList<>();
        messageHistory = new ArrayList<>();
        clients = new ArrayList<>();
        this.chatID = chatID;
    }

    public Chat() {

    }
}
