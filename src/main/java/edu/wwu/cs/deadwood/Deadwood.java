package edu.wwu.cs.deadwood;

import edu.wwu.cs.deadwood.assets.AssetManager;

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
    }

    public void startGame ()
    {
        this.game = new Game(this);
        this.game.initializeGame(2);
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
