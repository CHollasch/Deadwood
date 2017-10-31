package edu.wwu.cs.deadwood;

/**
 * @author Connor Hollasch
 * @since October 31, 1:14 PM
 */
public class Deadwood
{
    private Game game;

    private Deadwood (final String... args)
    {
        // Create game instance and start... or whatever
    }

    public void startGame () {}

    public void onGameEnd () {}

    public void shutdown () {}

    public static void main (final String... args)
    {
        new Deadwood(args);
    }
}
