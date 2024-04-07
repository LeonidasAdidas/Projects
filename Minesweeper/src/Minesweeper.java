import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class Minesweeper extends JFrame{
    private final int tileSize = 55;
    private int rows;
    private int cols;
    private int minecount;
    private int boardw;
    private int boardh;
    private JLabel label;
    private JPanel panel;
    private JPanel board;
    private MineTile[][] mines;
    private ArrayList<MineTile> minelist;
    boolean finished = false;
    private int clicked = 0;

    private Thread t;
    private int record;
    private Clip clip;
    private TimeKeeper timelabel;
    private MainMenu mainmenu;

    Minesweeper(int r, int c, int count, MainMenu mainm) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("./highscore.txt"));
            String line = reader.readLine();
            record = Integer.parseInt(line);
        } catch (FileNotFoundException e) {
            record = Integer.MAX_VALUE;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        rows = r;
        cols = c;
        minecount = count;
        mainmenu = mainm;
        boardw = cols * tileSize;
        boardh = rows * tileSize;
        setTitle("Bosnia Simulator");
        setSize(boardw, boardh);
        setLocationRelativeTo(null);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                mainmenu.setVisible(true);
                clip.stop();
                dispose();
            }
        });
        setResizable(false);
        setLayout(new BorderLayout());

        label = new JLabel();
        label.setFont(new Font("Arial", Font.BOLD, 25));
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setText("Minesweeper");

        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(label);
        timelabel = new TimeKeeper();
        timelabel.setHorizontalAlignment(JLabel.CENTER);
        panel.add(timelabel, BorderLayout.SOUTH);
        t = new Thread(timelabel);
        t.start();
        add(panel, BorderLayout.NORTH);



        board = new JPanel();
        board.setLayout(new GridLayout(rows, cols));
        //board.setBackground(Color.GREEN);
        mines = new MineTile[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                mines[i][j] = new MineTile(i, j);
                mines[i][j].setFocusable(false);
                mines[i][j].setFont(new Font("Arial", Font.BOLD, 20));
                mines[i][j].setMargin(new Insets(0, 0, 0, 0));
                mines[i][j].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if(finished) {return;}
                        MineTile tile = (MineTile) e.getSource();
                        if (e.getButton() == MouseEvent.BUTTON1) {
                            if (tile.getText() == "") {
                                if (minelist.contains(tile)) {
                                    revealMines();
                                } else {
                                    checkMines(tile.getRow(), tile.getCol());
                                }
                            }
                        } else if (e.getButton() == MouseEvent.BUTTON3) {
                            if(tile.getText() == "" && tile.isEnabled()) {
                                tile.setText("|>");
                            }else if(tile.getText() == "|>"){
                                tile.setText("");
                            }
                        }
                    }
                });
                board.add(mines[i][j]);
            }
        }
        setMines();
        add(board);

        try {
            AudioInputStream audioInputStream  = AudioSystem.getAudioInputStream(new File("./src/bgmusic.wav"));
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
            clip.loop(Integer.MAX_VALUE);
        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            System.out.println("IO error");
        }
        setVisible(true);
    }

    void setMines() {
        minelist = new ArrayList<>();
        Random rand = new Random(System.currentTimeMillis());
        while(minecount > 0) {
            int r = rand.nextInt(rows);
            int c = rand.nextInt(cols);
            MineTile tile = mines[r][c];
            if(!minelist.contains(tile)) {
                minelist.add(tile);
                minecount--;
            }
        }
    }

    void revealMines() {
        for (int i = 0; i < minelist.size(); i++) {
            minelist.get(i).setText("●~*");
        }
        finished = true;
        label.setText("Game Over");
        t.interrupt();
    }

    void checkMines(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            return;
        }
        MineTile tile = mines[row][col];
        if(!tile.isEnabled()) {
            return;
        }
        tile.setEnabled(false);
        clicked++;
        int neighbours = 0;
        neighbours += hasMine(row - 1, col - 1);
        neighbours += hasMine(row - 1, col);
        neighbours += hasMine(row, col - 1);
        neighbours += hasMine(row + 1, col - 1);
        neighbours += hasMine(row - 1, col + 1);
        neighbours += hasMine(row + 1, col);
        neighbours += hasMine(row, col + 1);
        neighbours += hasMine(row + 1, col + 1);
        if (neighbours > 0) {
            tile.setText(Integer.toString(neighbours));
        } else {
            tile.setText("");
            checkMines(row - 1, col - 1);
            checkMines(row, col - 1);
            checkMines(row - 1, col);
            checkMines(row + 1, col - 1);
            checkMines(row - 1, col + 1);
            checkMines(row + 1, col);
            checkMines(row, col + 1);
            checkMines(row + 1, col + 1);
        }
        if(clicked == rows * cols - minelist.size()) {
            finished = true;
            label.setText("Victory");
            t.interrupt();
            try {
                AudioInputStream audioInputStream  = AudioSystem.getAudioInputStream(new File("./src/applause_y.wav"));
                Clip clip = AudioSystem.getClip();
                clip.open(audioInputStream);
                clip.start();
            } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
                System.out.println("IO error");
            }
            if(timelabel.getTime() < record) {
                timelabel.setText("New Record: " + timelabel.timeToString());
                try {
                    BufferedWriter writer = new BufferedWriter(new FileWriter("./highscore.txt"));
                    writer.write(Integer.toString(timelabel.getTime()));
                    writer.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    int hasMine(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            return 0;
        }
        if (minelist.contains(mines[row][col])) {
            return 1;
        }
        return 0;
    }
    /*void setColor(MineTile tile) {
        switch(tile.getText()) {
            case "1":
                tile.setForeground(Color.BLUE);
                break;
            case "2":
                tile.setForeground(Color.GREEN);
                break;
            case "3":
                tile.setForeground(Color.RED);
                break;
            case "4":
                tile.setForeground(Color.PINK);
                break;
            case "5":
                tile.setForeground(new Color(160,82,45));
                break;
            case "6":
                tile.setForeground(Color.CYAN);
                break;
            case "7":
                tile.setForeground(Color.BLACK);
                break;
            case "8":
                tile.setForeground(Color.LIGHT_GRAY);
                break;
        }
    }*/
}
