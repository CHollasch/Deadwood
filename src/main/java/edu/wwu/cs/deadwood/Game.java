/*
 * Copyright (C) 2017 Deadwood - All Rights Reserved
 *
 * Unauthorized copying of this file, via any median is strictly prohibited
 * proprietary and confidential. For more information, please contact me at
 * connor@hollasch.net
 *
 * Written by Connor Hollasch <connor@hollasch.net>, October 2017
 */

package edu.wwu.cs.deadwood;

import edu.wwu.cs.deadwood.assets.Role;
import edu.wwu.cs.deadwood.assets.Room;
import edu.wwu.cs.deadwood.board.Board;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

/**
 * @author Connor Hollasch
 * @since October 31, 1:40 PM
 */
public class Game
{
    private Deadwood deadwood;
    private Collection<Player> players;
    private PlayerTurn currentPlayer;
    private Board gameBoard;

    private int daysLeft;

    private boolean playerUsedMove;
    private boolean playerUsedTurn;

    public Game (final Deadwood deadwood)
    {
        this.deadwood = deadwood;

        this.players = new HashSet<>();
        this.currentPlayer = null;
        this.gameBoard = null;

        this.playerUsedMove = false;
        this.playerUsedTurn = false;
    }

    public void initializeGame (final int playerCount)
    {
        this.daysLeft = 4;

        switch (playerCount) {
            case 2:
            case 3:
                this.daysLeft = 3;
                break;
            case 5:
                this.players.forEach(p -> p.setCreditCount(2));
                break;
            case 6:
                this.players.forEach(p -> p.setCreditCount(4));
                break;
            case 7:
            case 8:
                this.players.forEach(p -> p.setRank(2));
                break;
        }

        final ArrayList<Player> randomPlayerOrder = new ArrayList<>(this.players);
        Collections.shuffle(randomPlayerOrder);

        PlayerTurn previous = null;
        for (final Player player : randomPlayerOrder) {
            final PlayerTurn turn = new PlayerTurn(player);

            if (previous != null) {
                previous.setNext(turn);
            }

            if (getCurrentPlayer() == null) {
                setCurrentPlayer(turn);
            }

            previous = turn;
        }

        if (previous != null) {
            previous.setNext(getCurrentPlayer());
        }
    }

    public void currentPlayerAct ()
    {

    }

    public void currentPlayerEndTurn ()
    {

    }

    public void currentPlayerMove (final Room room)
    {

    }

    public void currentPlayerRehearse ()
    {

    }

    public void currentPlayerTakeRole (final Role role)
    {

    }

    public void currentPlayerUpgrade (final boolean useCredits, final int rank)
    {

    }

    public void wrapScene (final Room room)
    {

    }

    public void wrapDay ()
    {

    }

    public void endGame ()
    {

    }

    public Deadwood getDeadwood ()
    {
        return this.deadwood;
    }

    public Collection<Player> getPlayers ()
    {
        return this.players;
    }

    public PlayerTurn getCurrentPlayer ()
    {
        return this.currentPlayer;
    }

    public void setCurrentPlayer (final PlayerTurn currentPlayer)
    {
        this.currentPlayer = currentPlayer;
    }

    public Board getGameBoard ()
    {
        return this.gameBoard;
    }

    public int getDaysLeft ()
    {
        return this.daysLeft;
    }

    public static class PlayerTurn
    {
        private final Player    player;
        private PlayerTurn      next;

        public PlayerTurn (final Player player)
        {
            this.player = player;
        }

        public Player getPlayer ()
        {
            return this.player;
        }

        public PlayerTurn getNext ()
        {
            return this.next;
        }

        public void setNext (final PlayerTurn next)
        {
            this.next = next;
        }
    }
}
