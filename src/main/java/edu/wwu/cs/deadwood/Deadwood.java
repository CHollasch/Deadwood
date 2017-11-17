package edu.wwu.cs.deadwood;

import edu.wwu.cs.deadwood.assets.AssetManager;
import edu.wwu.cs.deadwood.board.CommandLineBoard;

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
            this.playerCount = Integer.parseInt(args[0]);
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

        final CommandLineBoard cliBoard = new CommandLineBoard(this.game);
        this.game.initializeGame(cliBoard, this.playerCount);

        cliBoard.setupConsoleListener(); // Event loop.
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
