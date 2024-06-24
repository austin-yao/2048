package org.cis120.twofoureight;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


/**
 * CIS 120 HW09 - 2048
 * (c) University of Pennsylvania
 * Created by Austin Yao
 */

/**
 * This class is a model for 2048.
 */
public class TwentyFortyEight {

    private int[][] board;
    private boolean gameOver;
    private LinkedList<int[][]> prevBoards;
    private final String filepath = "files/saved_game.txt";

    /**
     * Constructor sets up game state.
     */
    public TwentyFortyEight() {
        reset();
    }

    // Constructor for testing with a pre-set board.
    public TwentyFortyEight(int[][] board) {
        reset();
        this.board = board;
    }

    /*
    Mechanism for playing down (user presses down arrow).
     */
    public void playDown() {
        /*
        Adds the current board to the list of previous turns as a copy
        to prevent aliasing errors.
         */
        prevBoards.addFirst(arrayCopy());

        moveDown();

        /*
        Checks to see whether we can combine in all possible ways.
         */
        for (int col = 0; col < 4; col++) {
            if (board[3][col] == board[2][col]) {
                board[3][col] *= 2;
                board[2][col] = 0;
                if (board[1][col] == board[0][col]) {
                    board[1][col] *= 2;
                    board[0][col] = 0;
                }
            } else if (board[2][col] == board[1][col]) {
                board[2][col] *= 2;
                board[1][col] = 0;
            } else if (board[1][col] == board[0][col]) {
                board[1][col] *= 2;
                board[0][col] = 0;
            }
        }

        // Translates blocks again in case blocks were combined.
        moveDown();

        /*
        Checks to see if it was a legal move or not by comapring the board
        after translations and combining to the previous board. If it is the
        same, the move does not count so we do not count it as a turn. That is,
        we do not generate a random number.
         */
        boolean flag = false;
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                int[][] arr = prevBoards.getFirst();
                if (arr[row][col] != board[row][col]) {
                    genRandomNumber();
                    flag = true;
                    break;
                }
            }
            if (flag) {
                break;
            }
        }

        /*
        If it was not a legal move, do not add it to the previous turns so that
        undo does not become inconsistent.
         */
        if (!prevBoards.isEmpty() && Arrays.deepEquals(board, prevBoards.getFirst())) {
            prevBoards.remove();
        }

        // Checks to see if game is over or not.
        checkGameStatus();
    }

    // Helper function for translating blocks down.
    private void moveDown() {
        // Makes it easy to remove all zeroes later.
        ArrayList<Integer> zero = new ArrayList<>();
        zero.add(0);

        /*
        For each column, get all the squares that are not zero and push
        them down.
         */
        for (int col = 0; col < 4; col++) {
            ArrayList<Integer> states = new ArrayList<>();
            for (int row = 0; row < 4; row++) {
                states.add(board[row][col]);
            }
            states.removeAll(zero);
            for (int row = 3; row >= 0; row--) {
                if (states.isEmpty()) {
                    board[row][col] = 0;
                } else {
                    board[row][col] = states.remove(states.size() - 1);
                }
            }
        }
    }

    // Mechanism if user moves blocks up.
    public void playUp() {
        prevBoards.addFirst(arrayCopy());
        moveUp();

        for (int col = 0; col < 4; col++) {
            if (board[0][col] == board[1][col]) {
                board[0][col] *= 2;
                board[1][col] = 0;
                if (board[2][col] == board[3][col]) {
                    board[2][col] *= 2;
                    board[3][col] = 0;
                }
            } else if (board[1][col] == board[2][col]) {
                board[1][col] *= 2;
                board[2][col] = 0;
            } else if (board[2][col] == board[3][col]) {
                board[2][col] *= 2;
                board[3][col] = 0;
            }
        }

        moveUp();

        boolean flag = false;
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                int[][] arr = prevBoards.getFirst();
                if (arr[row][col] != board[row][col]) {
                    genRandomNumber();
                    flag = true;
                    break;
                }
            }
            if (flag) {
                break;
            }
        }
        if (!prevBoards.isEmpty() && Arrays.deepEquals(board, prevBoards.getFirst())) {
            prevBoards.remove();
        }
        checkGameStatus();
    }

    // Helper function for translating blocks up.
    private void moveUp() {
        ArrayList<Integer> zero = new ArrayList<>();
        zero.add(0);
        for (int col = 0; col < 4; col++) {
            ArrayList<Integer> states = new ArrayList<>();
            for (int row = 3; row >= 0; row--) {
                states.add(board[row][col]);
            }
            states.removeAll(zero);
            for (int row = 0; row < 4; row++) {
                if (states.isEmpty()) {
                    board[row][col] = 0;
                } else {
                    board[row][col] = states.remove(states.size() - 1);
                }
            }
        }
    }

    // Mechanism for user playing blocks right.
    public void playRight() {
        prevBoards.addFirst(arrayCopy());
        moveRight();

        for (int row = 0; row < 4; row++) {
            if (board[row][3] == board[row][2]) {
                board[row][3] *= 2;
                board[row][2] = 0;
                if (board[row][1] == board[row][0]) {
                    board[row][1] *= 2;
                    board[row][0] = 0;
                }
            } else if (board[row][2] == board[row][1]) {
                board[row][2] *= 2;
                board[row][1] = 0;
            } else if (board[row][1] == board[row][0]) {
                board[row][1] *= 2;
                board[row][0] = 0;
            }
        }

        moveRight();
        boolean flag = false;

        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                int[][] arr = prevBoards.getFirst();
                if (arr[row][col] != board[row][col]) {
                    genRandomNumber();
                    flag = true;
                    break;
                }
            }
            if (flag) {
                break;
            }
        }

        // If was not a legal move, remove from list of prevBoards so that undo isn't jacked up.
        if (!prevBoards.isEmpty() && Arrays.deepEquals(board, prevBoards.getFirst())) {
            prevBoards.remove();
        }
        checkGameStatus();
    }

    // Helper function for translating blocks to the right.
    private void moveRight() {
        ArrayList<Integer> zero = new ArrayList<>();
        zero.add(0);

        for (int row = 0; row < 4; row++) {
            ArrayList<Integer> states = new ArrayList<>();
            for (int col = 0; col < 4; col++) {
                states.add(board[row][col]);
            }
            states.removeAll(zero);
            for (int col = 3; col >= 0; col--) {
                if (states.isEmpty()) {
                    board[row][col] = 0;
                } else {
                    board[row][col] = states.remove(states.size() - 1);
                }
            }
        }
    }

    // Mechanism for if the user plays blocks to the left.
    public void playLeft() {
        prevBoards.addFirst(arrayCopy());
        moveLeft();

        for (int row = 0; row < 4; row++) {
            if (board[row][0] == board[row][1]) {
                board[row][0] *= 2;
                board[row][1] = 0;
                if (board[row][2] == board[row][3]) {
                    board[row][2] *= 2;
                    board[row][3] = 0;
                }
            } else if (board[row][1] == board[row][2]) {
                board[row][1] *= 2;
                board[row][2] = 0;
            } else if (board[row][2] == board[row][3]) {
                board[row][2] *= 2;
                board[row][3] = 0;
            }
        }

        moveLeft();
        boolean flag = false;
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                int[][] arr = prevBoards.getFirst();
                if (arr[row][col] != board[row][col]) {
                    genRandomNumber();
                    flag = true;
                    break;
                }
            }
            if (flag) {
                break;
            }
        }
        if (!prevBoards.isEmpty() && Arrays.deepEquals(board, prevBoards.getFirst())) {
            prevBoards.remove();
        }
        checkGameStatus();
    }

    // Helper function for translating blocks to the left
    private void moveLeft() {
        ArrayList<Integer> zero = new ArrayList<>();
        zero.add(0);

        for (int row = 0; row < 4; row++) {
            ArrayList<Integer> states = new ArrayList<>();
            for (int col = 3; col >= 0; col--) {
                states.add(board[row][col]);
            }
            states.removeAll(zero);
            for (int col = 0; col < 4; col++) {
                if (states.isEmpty()) {
                    board[row][col] = 0;
                } else {
                    board[row][col] = states.remove(states.size() - 1);
                }
            }
        }
    }

    /*
    Checks if the game is over or not by checking if there is space left and/or
    legal moves left. If no space and no legal moves, then game is over.
     */
    public void checkGameStatus() {
        if (checkSpaceLeft()) {
            return;
        }
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                int curr = board[row][col];
                if (curr == 0) {
                    continue;
                }
                ArrayList<Integer> neighbors = new ArrayList<>();
                neighbors.add(getNeighbors(row - 1, col));
                neighbors.add(getNeighbors(row + 1, col));
                neighbors.add(getNeighbors(row, col - 1));
                neighbors.add(getNeighbors(row, col + 1));
                if (neighbors.contains(curr)) {
                    return;
                }
            }
        }
        gameOver = true;
    }

    /*
    Helper function for checkGameStatus() that retrieves the neighbors
    directly above, below, to the left and right of the block in question.
     */
    private int getNeighbors(int r, int c) {
        try {
            return board[r][c];
        } catch (ArrayIndexOutOfBoundsException e) {
            return 0;
        }
    }

    /**
     * printGameState prints the current game state
     * for debugging.
     */
    public void printGameState() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                System.out.print(board[i][j]);
                if (j < 2) {
                    System.out.print(" | ");
                }
            }
            if (i < 2) {
                System.out.println("\n---------");
            }
        }
    }

    /**
     * reset (re-)sets the game state to start a new game.
     */
    public void reset() {
        board = new int[4][4];
        genRandomNumber();
        genRandomNumber();
        gameOver = false;
        prevBoards = new LinkedList<>();
    }

    /*
    Undos a move by popping the previous game board off the list of previous
    moves and setting that board to the current board.
     */
    public void undo() {
        if (!prevBoards.isEmpty()) {
            board = prevBoards.getFirst();
            prevBoards.remove();
            if (gameOver) {
                gameOver = false;
            }
        }
    }

    /*
    Saves the game by writing the current board and all previous turns to a
    txt file.
     */
    public void save() throws IOException {
        prevBoards.addFirst(board);
        Writer out = new FileWriter(filepath, false);


        for (int[][] currBoard: prevBoards) {
            for (int row = 0; row < 4; row++) {
                for (int col = 0; col < 4; col++) {
                    out.write(currBoard[row][col] + " ");
                }
            }
        }
        out.close();
    }

    /*
    Reloads a game by reading off the txt file and modifying the
    game state and past turns.
     */
    public void reload() throws IOException {
        reset();
        Reader in = new FileReader(filepath);
        boolean flag = true;

        /*
        Loops through the file, setting up each concurrent 2D array until
        we run out of characters in the txt file.
         */
        while (flag) {
            int[][] currBoard = new int[4][4];
            for (int row = 0; row < 4; row++) {
                for (int col = 0; col < 4; col++) {
                    int ch = in.read();
                    StringBuilder str = new StringBuilder();
                    if (ch == -1 || (char) ch == ' ') {
                        flag = false;
                        in.close();
                        break;
                    } else {
                        while (!((char) ch == ' ')) {
                            str.append((char) ch);
                            ch = in.read();
                        }
                        currBoard[row][col] = Integer.parseInt(String.valueOf(str));
                    }
                }
                if (!flag) {
                    break;
                }
            }
            if (flag) {
                prevBoards.addLast(currBoard);
            }
        }
        if (!prevBoards.isEmpty()) {
            board = prevBoards.getFirst();
            prevBoards.remove();
        }
        checkGameStatus();
    }

    /**
     * Helper function that randomly generates a 2 or 4.
     * @return a randomly generated 2 or 4.
     */
    private int randomNumber() {
        int num = (int) (Math.random() + 0.5);
        if (num == 0) {
            return 2;
        }
        return 4;
    }

    // Generates a random number for after each legal move.
    private void genRandomNumber() {
        if (checkSpaceLeft()) {
            int loc = (int) (Math.random() * getZeroSpaceLocations().size());
            int[] arr = getZeroSpaceLocations().get(loc);
            board[arr[0]][arr[1]] = randomNumber();
        }
    }

    // Helper method for checkGameOver() that checks if there are zeroes.
    private boolean checkSpaceLeft() {
        return !getZeroSpaceLocations().isEmpty();
    }

    // Helper method for checkGameOver that checks if there are zeroes.
    private List<int[]> getZeroSpaceLocations() {
        ArrayList<int[]> zeroes = new ArrayList<>();
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                if (board[row][col] == 0) {
                    int[] arr = {row, col};
                    zeroes.add(arr);
                }
            }
        }

        return zeroes;
    }

    /*
    Helper function that copies the board to a new array such that we can easily
    compare to previous turns.
     */
    private int[][] arrayCopy() {
        int[][] arr = new int[4][4];
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                arr[row][col] = board[row][col];
            }
        }
        return arr;
    }

    /**
     * getCell is a getter for the contents of the cell specified by the method
     * arguments.
     *
     * @param c column to retrieve
     * @param r row to retrieve
     * @return an integer denoting the contents of the corresponding cell on the
     *         game board. 0 = empty, 1 = Player 1, 2 = Player 2
     */
    public int getCell(int c, int r) {
        return board[r][c];
    }

    /**
     * getStatus() is a getter for whether or not the game is over.
     * @return boolean value gameOver
     */
    public boolean getStatus() {
        return gameOver;
    }

    // To test Undo and File I/O -- removes random component

    public void playDownWithoutRandomGenNumber() {
        prevBoards.addFirst(arrayCopy());

        moveDown();
        for (int col = 0; col < 4; col++) {
            if (board[3][col] == board[2][col]) {
                board[3][col] *= 2;
                board[2][col] = 0;
                if (board[1][col] == board[0][col]) {
                    board[1][col] *= 2;
                    board[0][col] = 0;
                }
            } else if (board[2][col] == board[1][col]) {
                board[2][col] *= 2;
                board[1][col] = 0;
            } else if (board[1][col] == board[0][col]) {
                board[1][col] *= 2;
                board[0][col] = 0;
            }
        }
        moveDown();
        if (!prevBoards.isEmpty() && Arrays.deepEquals(board, prevBoards.getFirst())) {
            prevBoards.remove();
        }
        checkGameStatus();
    }

    public void playUpWithoutRandomGenNumber() {
        prevBoards.addFirst(arrayCopy());
        moveUp();

        for (int col = 0; col < 4; col++) {
            if (board[0][col] == board[1][col]) {
                board[0][col] *= 2;
                board[1][col] = 0;
                if (board[2][col] == board[3][col]) {
                    board[2][col] *= 2;
                    board[3][col] = 0;
                }
            } else if (board[1][col] == board[2][col]) {
                board[1][col] *= 2;
                board[2][col] = 0;
            } else if (board[2][col] == board[3][col]) {
                board[2][col] *= 2;
                board[3][col] = 0;
            }
        }

        moveUp();

        if (!prevBoards.isEmpty() && Arrays.deepEquals(board, prevBoards.getFirst())) {
            prevBoards.remove();
        }
        checkGameStatus();
    }

    public void playRightWithoutRandomGenNumber() {
        prevBoards.addFirst(arrayCopy());
        moveRight();

        for (int row = 0; row < 4; row++) {
            if (board[row][3] == board[row][2]) {
                board[row][3] *= 2;
                board[row][2] = 0;
                if (board[row][1] == board[row][0]) {
                    board[row][1] *= 2;
                    board[row][0] = 0;
                }
            } else if (board[row][2] == board[row][1]) {
                board[row][2] *= 2;
                board[row][1] = 0;
            } else if (board[row][1] == board[row][0]) {
                board[row][1] *= 2;
                board[row][0] = 0;
            }
        }

        moveRight();

        if (!prevBoards.isEmpty() && Arrays.deepEquals(board, prevBoards.getFirst())) {
            prevBoards.remove();
        }
        checkGameStatus();
    }

    public void playLeftWithoutGenRandomNumber() {
        prevBoards.addFirst(arrayCopy());
        moveLeft();

        for (int row = 0; row < 4; row++) {
            if (board[row][0] == board[row][1]) {
                board[row][0] *= 2;
                board[row][1] = 0;
                if (board[row][2] == board[row][3]) {
                    board[row][2] *= 2;
                    board[row][3] = 0;
                }
            } else if (board[row][1] == board[row][2]) {
                board[row][1] *= 2;
                board[row][2] = 0;
            } else if (board[row][2] == board[row][3]) {
                board[row][2] *= 2;
                board[row][3] = 0;
            }
        }

        moveLeft();
        if (!prevBoards.isEmpty() && Arrays.deepEquals(board, prevBoards.getFirst())) {
            prevBoards.remove();
        }
        checkGameStatus();
    }
}
