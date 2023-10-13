import javax.swing.*;
import java.awt.*;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import Player.Player;
import Tile.Tile;
import Dice.Dice;
import Tile.TileType;

public class Game extends JFrame {

    Dice dice = new Dice();
    private final java.util.List<Tile> gameBoard;
    private final int diceRoll = dice.roll();
    private int currentPlayer = 1;
    private int seconds = 60;
    private boolean turnEnded = true;
    private final JFrame frame = new JFrame();
    private final JButton startGameButton = new JButton();
    private final JTextArea textfield = new JTextArea();
    private final JButton[] playerButtons = new JButton[4];
    private final Player[] players = new Player[4];
    private final JButton actionsButton = new JButton();
    private final JButton dice_button = new JButton();
    private final JLabel seconds_left = new JLabel();
    private final HashSet<String> usedNames = new HashSet<>();
    private final JButton endTurnButton = new JButton();

    // Custom OutputStream that appends text to the textfield
    private static class TextAreaOutputStream extends OutputStream {
        private final JTextArea textArea;
        private final int maxTextLength;

        public TextAreaOutputStream(JTextArea textArea, int maxTextLength) {
            this.textArea = textArea;
            this.maxTextLength = maxTextLength;
        }

        public void write(int b) {
            textArea.append(String.valueOf((char) b));
            if (textArea.getText().length() > maxTextLength) {
                textArea.setText(""); // Clear text if it exceeds the limit
            }
            textArea.setCaretPosition(textArea.getDocument().getLength()); // Scroll to the bottom
        }
    }

    public Game() {
        gameBoard = new ArrayList<>();

        // Create and add tiles to the game board
        gameBoard.add(new Tile(0, TileType.START));
        gameBoard.add(new Tile(1, TileType.SPECIAL));
        gameBoard.add(new Tile(2, TileType.NORMAL));
        gameBoard.add(new Tile(3, TileType.NORMAL));
        gameBoard.add(new Tile(4, TileType.NORMAL));
        gameBoard.add(new Tile(5, TileType.NORMAL));
        gameBoard.add(new Tile(6, TileType.NORMAL));
        gameBoard.add(new Tile(7, TileType.NORMAL));
        gameBoard.add(new Tile(8, TileType.NORMAL));
        gameBoard.add(new Tile(9, TileType.NORMAL));
        gameBoard.add(new Tile(10, TileType.NORMAL));
        gameBoard.add(new Tile(11, TileType.SPECIAL));
        gameBoard.add(new Tile(12, TileType.SPECIAL));
        for (int i = 0; i < playerButtons.length; i++) {
            String playerName = "Player " + (i + 1);
            players[i] = new Player(playerName);
            players[i].setPosition(0); // Set the initial position to 0
        }

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(650, 650);
        frame.getContentPane().setBackground(new Color(50, 50, 50));
        frame.setLayout(null);
        frame.setResizable(false);

        JLabel titleLabel = new JLabel();
        titleLabel.setBounds(210, 10, 650, 50);
        titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 40));
        titleLabel.setForeground(new Color(255, 255, 255)); // Set color to white
        titleLabel.setText("Game Title");

        textfield.setBounds(100, 100, 620, 400);
        textfield.setBackground(new Color(25, 25, 25));
        textfield.setForeground(new Color(25, 255, 0));
        textfield.setFont(new Font("Times New Roman", Font.BOLD, 20));
        textfield.setBorder(BorderFactory.createBevelBorder(1));
        textfield.setEditable(false);
        textfield.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(textfield);
        scrollPane.setBounds(100, 100, 540, 400);

        PrintStream printStream = new PrintStream(new TextAreaOutputStream(textfield, 5000));
        System.setOut(printStream);
        System.setErr(printStream);

        for (int i = 0; i < 4; i++) {
            String playerName = "Player " + (i + 1);
            players[i] = new Player(playerName);
            playerButtons[i] = new JButton();
            playerButtons[i].setBounds(0, 100 + (i * 100), 98, 100);
            playerButtons[i].setFont(new Font("Times New Roman", Font.BOLD, 15));
            playerButtons[i].setFocusable(false);
            playerButtons[i].setText(playerName);
            int finalI = i;
            playerButtons[i].addActionListener(e -> {
                String newName = Uniquename(playerName);
                if (newName != null) {
                    players[finalI].setName(newName);
                    playerButtons[finalI].setText(newName);
                    System.out.print(playerName + " has changed their name to " + newName + "\n");
                }
            });
            frame.add(playerButtons[i]);
        }

        seconds_left.setBounds(535,510,100,100);
        seconds_left.setBackground(new Color(25,25,25));
        seconds_left.setForeground(new Color(255,0,0));
        seconds_left.setFont(new Font("Times New Roman",Font.BOLD,60));
        seconds_left.setBorder(BorderFactory.createBevelBorder(1));
        seconds_left.setOpaque(true);
        seconds_left.setHorizontalAlignment(JTextField.CENTER);
        seconds_left.setText(String.valueOf(seconds));

        JLabel time_label = new JLabel();
        time_label.setBounds(535,510,100,25);
        time_label.setBackground(new Color(50,50,50));
        time_label.setForeground(new Color(255,0,0));
        time_label.setFont(new Font("Times New Roman",Font.PLAIN,16));
        time_label.setHorizontalAlignment(JTextField.CENTER);
        time_label.setText("timer >:D");

        dice_button.setBounds(335,525,200,100);
        dice_button.setFont(new Font("Times New Roman",Font.BOLD,35));
        dice_button.setFocusable(false);
        dice_button.setEnabled(false);
        dice_button.setText("Roll Dice");
        dice_button.addActionListener(e -> {
        if (turnEnded) {
        performPlayerTurn();
        }
});
        actionsButton.setBounds(125, 525, 200, 100); // Adjust these values as needed
        actionsButton.setFont(new Font("Times New Roman", Font.BOLD, 35));
        actionsButton.setFocusable(false);
        actionsButton.setEnabled(false);
        actionsButton.setText("Actions");
        actionsButton.addActionListener(a -> {
            String[] options = {"Option 1", "Option 2", "Option 3", "Option 4", "Option 5"};
            String selectedOption = (String) JOptionPane.showInputDialog(
                    frame,
                    playerButtons[currentPlayer - 1].getText() + ", choose an action:",
                    "Action Selection - " + currentPlayer,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]
            );
            // Handle the selected option
            if (selectedOption != null) {
                System.out.println(playerButtons[currentPlayer - 1].getText() + " You selected: " + selectedOption);
            }
        });

        Timer timer = new Timer(1000, e -> {
            seconds--;
            seconds_left.setText(String.valueOf(seconds));
            if (seconds <= 0) {
                ((Timer)e.getSource()).stop();
                System.out.print("Game over\n");
                displayFinalState();
            }

        });

        startGameButton.setBounds(0,500,98,110);// Adjust these values as needed
        startGameButton.setFont(new Font("Times New Roman", Font.BOLD, 20));
        startGameButton.setFocusable(false);
        startGameButton.setText("Start");
        startGameButton.addActionListener(e -> {
            dice_button.setEnabled(true);
            actionsButton.setEnabled(true);
            seconds = 60; // Reset the seconds variable
            System.out.println("game has started");
            seconds_left.setText(String.valueOf(seconds)); // Update the seconds_left label
            timer.start(); // Start the timer
            frame.remove(startGameButton);
            frame.add(endTurnButton);
        });

        endTurnButton.setBounds(0, 500, 100, 110); // Adjust these values as needed
        endTurnButton.setFont(new Font("Times New Roman", Font.BOLD, 15));
        endTurnButton.setFocusable(false);
        endTurnButton.setText("<html>End<br>Turn</html>");
        endTurnButton.addActionListener(e -> {
            seconds = 60;
            seconds_left.setText(String.valueOf(seconds)); // Update the seconds_left label
            timer.start(); // Start the timer
            System.out.println(playerButtons[currentPlayer - 1].getText() + " has ended their turn");
            turnEnded = true; // Set turnEnded to true to indicate that the current player's turn has ended
            currentPlayer = (currentPlayer % 4) + 1; // Switch to the next player
            dice_button.setEnabled(true);
        });

        promptPlayerNames();
        frame.add(startGameButton);
        frame.add(titleLabel);
        frame.add(scrollPane);
        frame.add(time_label);
        frame.add(seconds_left);
        frame.add(dice_button);
        frame.add(actionsButton);
        frame.setVisible(true);
    }

    private String Uniquename(String playerLabel) {
        String name;
        do {
            name = JOptionPane.showInputDialog(frame, "Enter new name for " + playerLabel);
            if (name != null && name.trim().isEmpty()) {
                // Name is empty, show a message
                JOptionPane.showMessageDialog(frame, "Name cannot be empty. Please enter a different name.");
            } else if (name != null && name.length() > 8) {
                // Name is more than 8 characters long, show a message
                JOptionPane.showMessageDialog(frame, "Name must be up to 8 characters long. Please enter a different name.");
            } else if (usedNames.contains(name)) {
                // Name is already used, show a message
                JOptionPane.showMessageDialog(frame, "Name '" + name + "' is already in use. Please enter a different name.");
            }
        } while (name != null && (name.trim().isEmpty() || name.length() > 8 || usedNames.contains(name)));
        if (name != null) {
            usedNames.add(name);
        }
        return name;
    }
    private int rollDice() {
        // Generate random numbers between 1 and 6 for two dice
        int dice1 = (int) (Math.random() * 6) + 1;
        int dice2 = (int) (Math.random() * 6) + 1;
        // Calculate the sum of the two dice rolls
        int sum = dice1 + dice2;

        return sum;
    }
    private void promptPlayerNames() {
        for (int i = 0; i < playerButtons.length; i++) {
            String newName = Uniquename(playerButtons[i].getText());
            if (newName != null) {
                players[i].setName(newName);
                playerButtons[i].setText(newName);
                System.out.print(playerButtons[i].getText() + " has changed their name to " + newName + "\n");
            }
        }
    }
    private void displayFinalState() {
        System.out.println("Final State of Play:");
        for (Player player : players) {
            System.out.println(player.getName() + " - Position: " + player.getPosition());
        }
    }
    private void performPlayerTurn() {
    int diceRoll = rollDice();
    System.out.println(players[currentPlayer - 1].getName() + " rolled a " + diceRoll);

    int currentPosition = players[currentPlayer - 1].getPosition();
    int newPosition = currentPosition + diceRoll;

    if (newPosition > gameBoard.size() - 1) {
        newPosition = newPosition - gameBoard.size();
        if (newPosition < 0) {
            newPosition = gameBoard.size() + newPosition;
        }
    }

    players[currentPlayer - 1].setPosition(newPosition);

    Tile currentTile = gameBoard.get(newPosition);
    System.out.println(players[currentPlayer - 1].getName() + " landed on tile " + currentTile.getPosition() + " - " + currentTile.getType());

    if (currentTile.getType() == TileType.SPECIAL) {
        players[currentPlayer - 1].increaseScore(10);
        System.out.println(players[currentPlayer - 1].getName() + "'s score increased to " + players[currentPlayer - 1].getScore());
    }

    turnEnded = false;

    // Grey out the "Roll Dice" button
    dice_button.setEnabled(false);

}
}