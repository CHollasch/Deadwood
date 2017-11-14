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

import java.util.Collection;
import java.util.HashSet;

/**
 * @author Connor Hollasch
 * @since October 31, 1:40 PM
 */
public class Game
{
    private Deadwood deadwood;
    private Collection<Player> players;
    private Player currentPlayer;
    private Board gameBoard;
    private int dayNumber;

    private boolean playerUsedMove;
    private boolean playerUsedTurn;

    public Game (final Deadwood deadwood)
    {
        this.deadwood = deadwood;

        this.players = new HashSet<>();
        this.currentPlayer = null;
        this.gameBoard = null;
        this.dayNumber = 1;

        this.playerUsedMove = false;
        this.playerUsedTurn = false;
    }

    public void initializeGame (final int playerCount)
    {

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

    public Player getCurrentPlayer ()
    {
        return this.currentPlayer;
    }

    public Board getGameBoard ()
    {
        return this.gameBoard;
    }

    public int getDayNumber ()
    {
        return this.dayNumber;
    }
}
