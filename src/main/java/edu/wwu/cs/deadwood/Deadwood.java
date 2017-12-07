package edu.wwu.cs.deadwood;

import edu.wwu.cs.deadwood.assets.AssetManager;
import edu.wwu.cs.deadwood.board.CommandLineBoard;
import edu.wwu.cs.deadwood.board.gui.GUIBoard;

import javax.swing.*;
import java.io.File;

/**
 * @author Connor Hollasch
 * @since October 31, 1:14 PM
 */
public class Deadwood
{
    //==================================================================================================================
    // Local variables.
    //==================================================================================================================

    private int playerCount;
    private Game game;

    //==================================================================================================================
    // Constructors.
    //==================================================================================================================

    private Deadwood (final String... args) throws Exception
    {
        try {
            this.playerCount = JOptionPane.showOptionDialog(
                    null,
                    "Pick a number of players",
                    "Welcome to Deadwood",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    new Integer[] {2, 3, 4, 5, 6, 7, 8},
                    2);

            if (this.playerCount == -1) {
                System.exit(0);
            }

            this.playerCount += 2;
        } catch (final Exception e) {
            System.out.println("First argument must be the player count integer!");
            System.exit(-1);
            return;
        }

        // Create game instance and start... or whatever
        AssetManager.setupAssetManager(new File("assets"));
        startGame();
    }

    //==================================================================================================================
    // Public API.
    //==================================================================================================================

    public void startGame ()
    {
        this.game = new Game(this);
        startGUIGame();
    }

    private void startCLIGame ()
    {

        final CommandLineBoard cliBoard = new CommandLineBoard(this.game);
        this.game.initializeGame(cliBoard, this.playerCount);

        cliBoard.setupConsoleListener(); // Event loop.
    }

    private void startGUIGame ()
    {
        final GUIBoard guiBoard = new GUIBoard(this.game);
        this.game.initializeGame(guiBoard, this.playerCount);

        guiBoard.displayWindow();
    }

    public void onGameEnd ()
    {
        // Nothing to do here yet.
    }

    //==================================================================================================================
    // Main method.
    //==================================================================================================================

    public static void main (final String... args) throws Exception
    {
        new Deadwood(args);
    }
}
