import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class MainMenu extends JFrame {
    private JPanel titlePanel;
    private JPanel recordPanel;
    private JPanel choosePanel;
    private int record;

    public MainMenu() {
        setTitle("Minesweeper Menu");
        setSize(700, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());
        titlePanel = new JPanel();
        recordPanel = new JPanel();
        recordPanel.setLayout(new BorderLayout());
        JLabel titleLabel = new JLabel("Minesweeper");
        titleLabel.setLayout(new BorderLayout());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        getRecord();
        JLabel recordLabel = new JLabel("Current highscore: " + new TimeKeeper(record).timeToString());
        recordLabel.setFont(new Font("Arial", Font.BOLD, 25));
        recordLabel.setHorizontalAlignment(JLabel.CENTER);
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        recordPanel.add(recordLabel, BorderLayout.NORTH);
        add(titlePanel, BorderLayout.NORTH);
        add(recordPanel, BorderLayout.CENTER);

        JLabel optionLabel = new JLabel("Select difficulty:");
        optionLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        optionLabel.setHorizontalAlignment(JLabel.CENTER);

        choosePanel = new JPanel();
        choosePanel.setLayout(new BorderLayout());
        choosePanel.add(optionLabel, BorderLayout.NORTH);

        JPanel checkBoxPanel = new JPanel();

        checkBoxPanel.setLayout(new GridLayout(1,3));
        JCheckBox easy = new JCheckBox("Easy");
        easy.setHorizontalAlignment(JLabel.CENTER);
        easy.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                setVisible(false);
                easy.setSelected(false);
                new Minesweeper(8, 8, 10, this);
            }
        });
        JCheckBox medium = new JCheckBox("Medium");
        medium.setHorizontalAlignment(JLabel.CENTER);
        medium.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                setVisible(false);
                medium.setSelected(false);
                new Minesweeper(12, 12, 20, this);
            }
        });
        JCheckBox hard = new JCheckBox("Hard");
        hard.setHorizontalAlignment(JLabel.CENTER);
        hard.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                setVisible(false);
                hard.setSelected(false);
                new Minesweeper(16, 16, 42, this);
            }
        });
        checkBoxPanel.add(easy);
        checkBoxPanel.add(medium);
        checkBoxPanel.add(hard);

        choosePanel.add(checkBoxPanel, BorderLayout.SOUTH);
        add(choosePanel, BorderLayout.SOUTH);


        setVisible(true);
    }

    private void getRecord() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("./highscore.txt"));
            String line = reader.readLine();
            record = Integer.parseInt(line);
        } catch (FileNotFoundException e) {
            record = Integer.MAX_VALUE;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        new MainMenu();
    }
}
