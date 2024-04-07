public class User{
    public int userID;
    public String name;
    public User(int userID, String name) {
        this.userID = userID;
        this.name = name;
    }
    public User() {
        this.userID = -1;
        this.name = null;
    }
}
