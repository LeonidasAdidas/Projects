import java.io.IOException;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        try {
            Server server = new Server();

            List<Thread> clientThreads = new ArrayList<>();
            List<Client> clients = new ArrayList<>();
            List<Chat> chatList = new ArrayList<>();
            List<User> userList = new ArrayList<>();

            readDataBase(chatList, userList);

            Thread stopperThread = new Thread(new StopperRunnable(clients, server));
            stopperThread.start();

            while(true) {
                Socket clientSocket = server.acceptClient();
                Client client = new Client(clientSocket, chatList, clients, server);
                ClientThread receiverThread = new ClientThread(client, chatList, userList, clients);
                Thread receiveThread = new Thread(receiverThread);
                receiveThread.start();

                clients.add(client);
                clientThreads.add(receiveThread);

            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void readDataBase(List<Chat> chatList, List<User> userList) throws ClassNotFoundException, SQLException {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

        String jdbcUrl = "jdbc:sqlserver://localhost:1433;integratedSecurity=true;databaseName=ChatAppDB;encrypt=true;trustServerCertificate=true";

        Connection connection = DriverManager.getConnection(jdbcUrl);

        String query = "SELECT * FROM Users";
        Statement statement = connection.createStatement();
        java.sql.ResultSet resultSet = statement.executeQuery(query);

        while(resultSet.next()) {
            userList.add(new User(resultSet.getInt("UserID"), resultSet.getString("Name")));
        }

        query = "SELECT * FROM Chats";
        statement = connection.createStatement();
        resultSet = statement.executeQuery(query);

        while(resultSet.next()) {
            chatList.add(new Chat(resultSet.getInt("ChatID")));
        }

        query = "SELECT * FROM UsersInChats";
        statement = connection.createStatement();
        resultSet = statement.executeQuery(query);

        while(resultSet.next()) {
            int userID = resultSet.getInt("UserID");
            int chatID = resultSet.getInt("ChatID");
            for(Chat chat : chatList) {
                if(chat.chatID == chatID) {
                    for(User user : userList) {
                        if(user.userID == userID) {
                            chat.userList.add(user);
                            break;
                        }
                    }
                    break;
                }
            }
        }

        query = "SELECT * FROM Messages M JOIN Users U ON U.UserID = M.AuthorID";
        statement = connection.createStatement();
        resultSet = statement.executeQuery(query);

        while(resultSet.next()) {
            for (Chat chat : chatList) {
                if(chat.chatID == resultSet.getInt("ChatID")) {
                    chat.messageHistory.add(new Message(resultSet.getInt("AuthorID"), resultSet.getString("Name"),
                            resultSet.getString("Date"), resultSet.getString("Text"), resultSet.getInt("ChatID")));
                    break;
                }
            }
        }










    }

}