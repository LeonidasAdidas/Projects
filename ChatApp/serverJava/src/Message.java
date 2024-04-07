public class Message extends Data{
    public int authorID;
    public String authorName;
    public String date;
    public String text;
    public int chatID;

    public Message(int authorID, String authorName, String date, String text, int chatID){
        this.authorID = authorID;
        this.authorName = authorName;
        this.date = date;
        this.text = text;
        this.chatID = chatID;
    }
    public Message(){
        this.authorID = -1;
        this.authorName = null;
        this.date = null;
        this.text = null;
        this.chatID = -1;
    }
}
