import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;

public class ChatUI extends JFrame implements ActionListener {
    //Main
    private JPanel mainMenu;
    private List<JButton> mainMenuButtons;
    //Chat
    private JPanel chatMenu;
    private TextField chatMenuTextField;
    public JPanel chatMenuMessages;
    private JButton chatMenuSend;
    private JButton chatMenuBack;
    public JScrollBar verticalScrollBar;
    //New Chat
    public JPanel newChat;
    private JButton newChatButton;
    private TextField newChatTextField;
    //Login
    private JPanel loginMenu;
    private TextField loginMenuTextField;
    private JButton loginMenuButton;

    private Client client;
    private String userProvidedName;
    private int menuIndex;
    private String newName;
    private String chatMessage;

    public ChatUI(Client client) {
        setBounds(50, 50, 800, 700);
        setLayout(new FlowLayout());
        setTitle("ChatApp");
        setResizable(false);

        loginMenu = new JPanel();
        add(loginMenu);
        mainMenu = new JPanel();
        mainMenu.setVisible(false);
        add(mainMenu);
        chatMenu = new JPanel();
        chatMenu.setVisible(false);
        add(chatMenu);
        newChat = new JPanel();
        newChat.setLayout(new GridLayout(4,1));
        newChat.setVisible(false);
        add(newChat);

        this.client = client;
        userProvidedName = null;
        menuIndex = -1;
        newName = null;
        chatMessage = null;

        loginMenu.setLayout(new GridLayout(3,1));
        loginMenu.add(new JLabel("Name:"));
        loginMenuTextField = new TextField(20);
        loginMenu.add(loginMenuTextField);
        loginMenuButton = new JButton("Login");
        loginMenuButton.addActionListener(this);
        loginMenu.add(loginMenuButton);

        mainMenuButtons = new ArrayList<>();





        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == loginMenuButton){
            userProvidedName = loginMenuTextField.getText();

            synchronized (this) {
                notify();
            }

            loginMenu.setVisible(false);
        }
        if(mainMenuButtons.contains(((JButton) e.getSource()))){
            menuIndex = mainMenuButtons.indexOf(e.getSource());

            synchronized (this) {
                notify();
            }
        }
        if (e.getSource() == newChatButton) {
            newName = newChatTextField.getText();
            synchronized (this) {
                notify();
            }
        }
        if (e.getSource() == chatMenuSend) {
            if(!chatMenuTextField.getText().equals("")) {
                chatMessage = chatMenuTextField.getText();
                chatMenuTextField.setText("");
                synchronized (this) {
                    notify();
                }
            }

        }
        if (e.getSource() == chatMenuBack) {
            client.currentChat = null;
            chatMessage = "{[(_<EXIT>_)]}";
            synchronized (this) {
                notify();
            }
        }
    }

    public void toChatMenu() {
        mainMenu.setVisible(false);
        newChat.setVisible(false);
        chatMenu.setVisible(true);
    }

    public void toMainMenu() {
        mainMenu.setVisible(true);
        chatMenu.setVisible(false);
        newChat.setVisible(false);
    }

    public void toNewChat() {
        mainMenu.setVisible(false);
        chatMenu.setVisible(false);
        newChat.setVisible(true);
    }

    public void resetNewChat() {
        newName = null;
        newChat.removeAll();
        newChat.add(new JLabel("Give full name:"));
        newChatTextField = new TextField(20);
        newChat.add(newChatTextField);
        newChatButton = new JButton("Add");
        newChatButton.addActionListener(this);
        newChat.add(newChatButton);
        validate();
    }

    public void setUpChat() {
        chatMenu.removeAll();
        chatMenu.setLayout(new BorderLayout());
        chatMenu.setPreferredSize(new Dimension((int) (getSize().width * 0.95), (int) (getSize().height * 0.90)));
        chatMenuMessages = new JPanel();
        chatMenuMessages.setLayout(new BoxLayout(chatMenuMessages, BoxLayout.Y_AXIS));

        chatMenuTextField = new TextField();
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(chatMenuTextField, BorderLayout.CENTER);
        chatMenuSend = new JButton("Send");
        chatMenuSend.addActionListener(this);
        panel.add(chatMenuSend, BorderLayout.EAST);
        chatMenuBack = new JButton("Back");
        chatMenuBack.addActionListener(this);
        panel.add(chatMenuBack, BorderLayout.WEST);
        chatMenu.add(panel, BorderLayout.SOUTH);

        JScrollPane scrollPane = new JScrollPane(chatMenuMessages);
        scrollPane.setPreferredSize(new Dimension(400, 400));
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        chatMenu.add(scrollPane, BorderLayout.CENTER);

        verticalScrollBar = scrollPane.getVerticalScrollBar();

        for(Message message : client.currentChat.messageHistory) {
            JLabel l = new JLabel(" " + message.authorName + ": " + message.text);
            l.setFont(new Font("Dialog", Font.PLAIN, 20));
            chatMenuMessages.add(l);
        }

        revalidate();
        repaint();

        verticalScrollBar.setValue(verticalScrollBar.getMaximum());
    }

    public synchronized String getName() {

        while (userProvidedName == null) {
            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }

        return userProvidedName;
    }

    public synchronized int getMenuIndex() {
        while (menuIndex == -1) {
            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }
        int localMenuIndex = menuIndex;
        menuIndex = -1;

        return localMenuIndex;
    }

    public synchronized String getNewName() {
        while (newName == null) {
            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }

        return newName;
    }

    public synchronized String getChatMessage() {
        while (chatMessage == null) {
            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }
        if(chatMessage.equals("{[(_<EXIT>_)]}")){
            chatMessage = null;
            return "{[(_<EXIT>_)]}";
        }

        String localChatMessage = chatMessage;
        chatMessage = null;

        return localChatMessage;
    }


    public void renderMainMenu() {
        mainMenu.removeAll();
        mainMenu.setLayout(new GridLayout(client.chatList.size() + 2, 1));
        mainMenu.add(new Label("Choose a chat or create a new one:"));
        mainMenuButtons = new ArrayList<>();
        JButton button = null;
        for (Chat chat : client.chatList) {
            for(User user : chat.userList) {
                if(user.userID != client.user.userID){
                    button = new JButton(user.name);
                    button.addActionListener(this);
                }
            }
            mainMenuButtons.add(button);
            mainMenu.add(button);
        }
        button = new JButton("Create new chat");
        button.addActionListener(this);
        mainMenuButtons.add(button);
        mainMenu.add(button);

        validate();
    }
}
