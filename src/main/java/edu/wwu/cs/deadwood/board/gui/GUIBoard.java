package edu.wwu.cs.deadwood.board.gui;

import edu.wwu.cs.deadwood.Game;
import edu.wwu.cs.deadwood.Player;
import edu.wwu.cs.deadwood.assets.Role;
import edu.wwu.cs.deadwood.assets.Room;
import edu.wwu.cs.deadwood.board.Board;

import javax.swing.*;
import java.util.Collection;
import java.util.Map;

/**
 * @author Connor Hollasch
 * @since October 31, 1:54 PM
 */
public class GUIBoard implements Board
{
    private final Game game;
    private JFrame frame;

    public GUIBoard (final Game game)
    {
        this.game = game;
    }

    public void displayWindow ()
    {
        this.frame = new JFrame("Deadwood");
        this.frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        this.frame.add(new BoardPanel());
        this.frame.pack();

        this.frame.setVisible(true);
    }

    public void destroyWindow ()
    {
        // Destroy the frame.
    }

    @Override
    public void refreshBoard ()
    {

    }

    @Override
    public void sceneWrapped (Room room, Map<Player, Collection<Integer>> onCardPayouts, final Map<Player, Integer> offCardPayouts)
    {

    }

    @Override
    public void dayWrapped ()
    {

    }

    @Override
    public void endGame ()
    {

    }

    @Override
    public void playerActed (final Player player, final boolean successful, final int diceRoll)
    {

    }

    @Override
    public void playerEndedTurn (final Player old, final Player newPlayer)
    {

    }

    @Override
    public void playerMoved (final Player player, final Room newRoom)
    {

    }

    @Override
    public void playerRehearsed (final Player player)
    {

    }

    @Override
    public void playerTookRole (final Player player, final Role role)
    {

    }

    @Override
    public void playerUpgraded (final Player player, final boolean usedCredits, final int rankUpgradingTo)
    {

    }
}
