package edu.wwu.cs.deadwood.board;

import edu.wwu.cs.deadwood.Player;
import edu.wwu.cs.deadwood.assets.Role;
import edu.wwu.cs.deadwood.assets.Room;

import javax.swing.*;
import java.util.Collection;
import java.util.Map;

/**
 * @author Connor Hollasch
 * @since October 31, 1:54 PM
 */
public class GUIBoard implements Board
{
    private JFrame frame;

    public void displayWindow ()
    {
        // Display the frame & initialize.
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
