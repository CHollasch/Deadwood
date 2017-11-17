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
    private Game game;

    private Deadwood (final String... args) throws Exception
    {
        // Create game instance and start... or whatever
        AssetManager.setupAssetManager(new File("assets"));
        startGame();
    }

    public void startGame ()
    {
        this.game = new Game(this);

        final CommandLineBoard cliBoard = new CommandLineBoard(this.game);
        this.game.initializeGame(cliBoard, 2);

        cliBoard.setupConsoleListener(); // Event loop.
    }

    public void onGameEnd ()
    {
        // Nothing to do here yet.
    }

    public static void main (final String... args) throws Exception
    {
        new Deadwood(args);
    }
}
