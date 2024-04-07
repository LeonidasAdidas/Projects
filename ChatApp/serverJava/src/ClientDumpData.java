import java.util.List;

public class ClientDumpData extends Data {
    public User user;
    public List<Chat> chatList;

    public ClientDumpData(User user, List<Chat> chatList) {
        this.user = user;
        this.chatList = chatList;
    }
}
