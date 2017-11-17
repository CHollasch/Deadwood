package edu.wwu.cs.deadwood.board;

import edu.wwu.cs.deadwood.Player;
import edu.wwu.cs.deadwood.assets.Role;
import edu.wwu.cs.deadwood.assets.Room;

import java.util.Collection;
import java.util.Map;

/**
 * Models a view for any Deadwood game.
 *
 * @author Connor Hollasch
 * @since October 31, 1:51 PM
 */
public interface Board
{
    void refreshBoard ();

    void sceneWrapped (
            final Room room,
            final Map<Player, Collection<Integer>> onCardPayouts,
            final Map<Player, Integer> offCardPayouts);

    void dayWrapped ();

    void endGame ();

    void playerActed (final Player player, final boolean successful, final int diceRoll);

    void playerEndedTurn (final Player old, final Player newPlayer);

    void playerMoved (final Player player, final Room newRoom);

    void playerRehearsed (final Player player);

    void playerTookRole (final Player player, final Role role);

    void playerUpgraded (final Player player, final boolean usedCredits, final int rankUpgradingTo);
}
