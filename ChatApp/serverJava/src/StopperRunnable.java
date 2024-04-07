import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class StopperRunnable implements Runnable{
    private List<Client> clients;
    private Server server;
    private List<Chat> chatList;
    private List<User> userList;

    public StopperRunnable(List<Client> clients, Server server){
        this.clients = clients;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            Scanner scanner = new Scanner(System.in);
            while(true) {
                String input = scanner.nextLine();
                if(input.equals("EXIT")) {
                    for(Client client : clients) {
                        client.closeSocket();
                    }
                    server.closeServer();

                    List<Message> newMessages = server.getNewMessages();
                    List<User> newUsers = server.getNewUsers();
                    List<Chat> newChats = server.getNewChats();

                    try {
                        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

                        String jdbcUrl = "jdbc:sqlserver://localhost:1433;integratedSecurity=true;databaseName=ChatAppDB;encrypt=true;trustServerCertificate=true";

                        Connection connection = DriverManager.getConnection(jdbcUrl);

                        //Users
                        String query = "INSERT INTO Users(UserID, Name) VALUES ";
                        for (User user : newUsers) {
                            query += "(" + user.userID + ", '" + user.name + "'),";
                        }
                        query = query.substring(0, query.length() - 1);
                        Statement statement = connection.createStatement();
                        statement.executeUpdate(query);

                        //Chats
                        query = "INSERT INTO Chats(ChatID) VALUES ";
                        for (Chat chat : newChats) {
                            query += "(" + chat.chatID + "),";
                        }
                        query = query.substring(0, query.length() - 1);
                        statement = connection.createStatement();
                        statement.executeUpdate(query);

                        query = "INSERT INTO Messages(ChatID, AuthorID, Date, Text) VALUES ";
                        for(Message message : newMessages) {
                            query += "(" + message.chatID + ", " + message.authorID + ", '" + message.date + "', '" + message.text + "'),";
                        }
                        query = query.substring(0, query.length() - 1);
                        statement = connection.createStatement();
                        statement.executeUpdate(query);

                        query = "INSERT INTO UsersInChats(UserID, ChatID) VALUES ";
                        for (Chat chat : newChats) {
                            for (User user : chat.userList) {
                                query += "(" + user.userID + ", " + chat.chatID + "),";
                            }
                        }
                        query = query.substring(0, query.length() - 1);
                        statement = connection.createStatement();
                        statement.executeUpdate(query);

                        connection.close();
                    }
                    catch (Exception e) {
                        System.out.println(e);
                    }
                    break;

                }
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
