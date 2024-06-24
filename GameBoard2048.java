package org.cis120.twofoureight;

/**
 * CIS 120 HW09 - TicTacToe Demo
 * (c) University of Pennsylvania
 * Created by Bayley Tuch, Sabrina Green, and Nicolas Corona in Fall 2020.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

/**
 * This class instantiates a TicTacToe object, which is the model for the game.
 * As the user clicks the game board, the model is updated. Whenever the model
 * is updated, the game board repaints itself and updates its status JLabel to
 * reflect the current state of the model.
 * 
 * This game adheres to a Model-View-Controller design framework. This
 * framework is very effective for turn-based games. We STRONGLY
 * recommend you review these lecture slides, starting at slide 8,
 * for more details on Model-View-Controller:
 * https://www.seas.upenn.edu/~cis120/current/files/slides/lec37.pdf
 * 
 * In a Model-View-Controller framework, GameBoard stores the model as a field
 * and acts as both the controller (with a MouseListener) and the view (with
 * its paintComponent method and the status JLabel).
 */
@SuppressWarnings("serial")
public class GameBoard2048 extends JPanel {

    private TwentyFortyEight tfe; // model for the game
    private JLabel status;

    // Game constants
    public static final int BOARD_WIDTH = 400;
    public static final int BOARD_HEIGHT = 400;

    /**
     * Initializes the game board.
     */
    public GameBoard2048(JLabel gameOverInit) {
        // creates border around the court area, JComponent method
        setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // Enable keyboard focus on the court area. When this component has the
        // keyboard focus, key events are handled by its key listener.
        setFocusable(true);

        tfe = new TwentyFortyEight(); // initializes model for the game
        status = gameOverInit;

        /*
         * Listens for mouseclicks. Updates the model, then updates the game
         * board based off of the updated model.
         */
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!tfe.getStatus()) {
                    if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                        tfe.playLeft();
                    } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                        tfe.playRight();
                    } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                        tfe.playDown();
                    } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                        tfe.playUp();
                    }
                    updateGameStatus();
                    repaint();
                }
            }
        });
    }

    /**
     * (Re-)sets the game to its initial state.
     */
    public void reset() {
        tfe.reset();
        status.setText("Running");
        repaint();

        // Makes sure this component has keyboard/mouse focus
        requestFocusInWindow();
    }

    // Undos a turn and repaints the board.
    public void undo() {
        tfe.undo();
        updateGameStatus();
        repaint();

        requestFocusInWindow();
    }

    // Saves the game.
    public void save() {
        try {
            tfe.save();
            status.setText("Game Saved!");
        } catch (IOException e) {
            status.setText("Unable to Save");
        }
        requestFocusInWindow();
    }

    /*
    Try to load the game. If it loads, update the game status,
    repaint it, and focus.
     */
    public void load() {
        try {
            tfe.reload();
        } catch (IOException e) {
            status.setText("Unable to Load");
        }
        updateGameStatus();
        repaint();
        requestFocusInWindow();
    }

    /*
    This method is used so that the info pop-up does not
    break the focusInWindow.
     */
    public void focus() {
        requestFocusInWindow();
    }

    public void updateGameStatus() {
        if (tfe.getStatus()) {
            status.setText("Game Over!");
        } else {
            status.setText("Running");
        }
    }

    /**
     * Draws the game board.
     * 
     * There are many ways to draw a game board. This approach
     * will not be sufficient for most games, because it is not
     * modular. All of the logic for drawing the game board is
     * in this method, and it does not take advantage of helper
     * methods. Consider breaking up your paintComponent logic
     * into multiple methods or classes, like Mushroom of Doom.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Font font = new Font("Serif", Font.PLAIN, 20);
        g.setFont(font);
        // Draws board grid

        g.drawLine(100, 0, 100, 400);
        g.drawLine(200, 0, 200, 400);
        g.drawLine(300, 0, 300, 400);
        g.drawLine(0, 100, 400, 100);
        g.drawLine(0, 200, 400, 200);
        g.drawLine(0, 300, 400, 300);

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                int state = tfe.getCell(i, j);
                if (state != 0) {
                    g.drawString(String.valueOf(state), 45 + 100 * i, 45 + 100 * j);
                }
            }
        }
    }

    /**
     * Returns the size of the game board.
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    }
}
