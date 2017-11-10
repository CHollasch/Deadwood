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

import edu.wwu.cs.deadwood.assets.Card;
import edu.wwu.cs.deadwood.assets.Player;
import edu.wwu.cs.deadwood.assets.Role;
import edu.wwu.cs.deadwood.assets.Room;
import edu.wwu.cs.deadwood.board.Board;
import lombok.Getter;

import java.util.Collection;
import java.util.HashSet;

/**
 * @author Connor Hollasch
 * @since October 31, 1:40 PM
 * Edited: Yichuan Yin, November 10, 12:59 AM
 */
public class Game
{
    @Getter
    private Deadwood deadwood;

    @Getter
    private Collection<Player> players;

    @Getter
    private Player currentPlayer;

    @Getter
    private Board gameBoard;

    @Getter
    private int dayNumber;

    private boolean playerUsedMove;
    private boolean playerUsedTurn;

    private edu.wwu.cs.deadwood.Rank rank;
    private edu.wwu.cs.deadwood.Dice dice;
    private Collection<Room> rooms;
    private Room trailer;

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
        if (playerCount == 5)
        {
            for (Player p: players)
            {
                p.setCreditCount(2);
            }
        }
        else if (playerCount == 6)
        {
            for (Player p: players)
            {
                p.setCreditCount(4);
            }
        }
        if (playerCount >= 7)
        {
            for (Player p: players)
            {
                p.setRank(2);
            }
        }

        wrapDay();
    }

    public void currentPlayerAct ()
    {
        if (!currentPlayer.isActing())
        {
            System.out.println("You can't act with no role!");
        }
        else
        {
            int diceRoll = dice.roll(2);
            Card card = currentPlayer.getActiveCard();
            int sceneBudget = card.getCardBudget();

            if ((diceRoll + currentPlayer.getPracticeChips()) >= sceneBudget)
            {
                System.out.println("Acting success!");

                if(currentPlayer.getActiveCard() == card)
                {
                    currentPlayer.onCardReward();
                }
                else
                {
                    currentPlayer.offCardReward();
                }

                currentPlayer.getRoom().takeOneShotOff();

                if (currentPlayer.getRoom().getShotCount() == 0)
                {
                    wrapScene(currentPlayer.getRoom());
                }
            }

            else
            {
                System.out.println("Acting Failed. Better luck next time!");
            }

            playerUsedTurn = true;
            currentPlayerEndTurn();
        }
    }

    public void currentPlayerEndTurn ()
    {

    }

    public void currentPlayerMove (final Room targetRoom)
    {
        if (currentPlayer.isActing())
        {
            System.out.println("Move invalid: acting in progress.");
        }
        else if (currentPlayer.getRoom().isAdjacentTo(targetRoom))
        {
            currentPlayer.setRoom(targetRoom);
            System.out.println("Move Success!");
            playerUsedMove = true;
        }
        else
        {
            System.out.println("Move invalid: room is not adjacent.");
        }
    }

    public void currentPlayerRehearse ()
    {
        if (currentPlayer.isActing())
        {
            if (currentPlayer.getPracticeChips() >= currentPlayer.getActiveCard().getCardBudget())
            {
                System.out.println("Enough practice, time to act!");
            }
            else
            {
                currentPlayer.addOnePracticeChip();
                System.out.println("You rehearsed successfully");
                playerUsedTurn = true;
                currentPlayerEndTurn();
            }
        }
        else
        {
            System.out.println("Cannot rehearse: you ain't acting.");
        }
    }

    public void currentPlayerTakeRole (final Card card, final Role role)
    {
        if (currentPlayer.isActing())
        {
            System.out.println("You have a role already, work on that!");
        }
        else if (!currentPlayer.hasEnoughRank(role))
        {
            System.out.println("You don't have enough rank to take this role.");
        }
        else if (currentPlayer.getRoom().getRoomType() != Room.Type.STAGE)
        {
            System.out.println("Not on a stage, no roles to take.");
        }
        else if (currentPlayer.getRoom().getShotCount() == 0)
        {
            System.out.println("Scene is done, no more roles to take.");
        }
        else if (role.isTaken())
        {
            System.out.println("Role is taken by someone else.");
        }
        else
        {
            currentPlayer.setActiveRole(role);
            currentPlayer.setActiveCard(card);
            role.setPlayer(currentPlayer);
            role.setTaken();
            System.out.println("You have taken this role successfully.");
            playerUsedTurn = true;
            currentPlayerEndTurn();
        }
    }

    public void currentPlayerUpgrade (final boolean useCredits, final int desiredRank)
    {
        if (!currentPlayer.isAtCastingOffice())
        {
            System.out.println("You need to be at the Casting Office to upgrade!");
        }
        else if (desiredRank >= currentPlayer.getRank())
        {
            System.out.println("You can only upgrade to a higher rank!");
        }
        else if (useCredits && currentPlayer.getCreditCount() < rank.creditsRequired(desiredRank))
        {
            System.out.println("Not enough credits!");
        }
        else if (!useCredits && currentPlayer.getDollarCount() < rank.moneyRequried(desiredRank))
        {
            System.out.println("Not enough money!");
        }
        else
        {
            currentPlayer.setRank(desiredRank);
            if (useCredits)
            {
                currentPlayer.takeCreditsAway(rank.creditsRequired(desiredRank));
            }
            else
            {
                currentPlayer.takeMoneyAway(rank.moneyRequried(desiredRank));
            }
            System.out.println("Upgrade Success!");
            playerUsedTurn = true;
            currentPlayerEndTurn();
        }
    }

    public void wrapScene (final Room room)
    {
        if(room.getCard().hasPlayers())
        {
            int[] diceResult = dice.detailedRoll(room.getCard().getCardBudget());

            for (int i = 0; i < diceResult.length; i++)
            {
                Role[] roles = room.getCard().getRoles();
                if (roles[i % roles.length].isTaken())
                {
                    roles[i % roles.length].getPlayer().receiveMoney(diceResult[i]);
                }
            }
        }

        room.closeScene();
    }

    public void wrapDay ()
    {
        for (Player p: players)
        {
            p.setRoom(trailer);
        }


        for (Room r: rooms)
        {
            r.clearShotMarker();
        }

        for (Room r: rooms)
        {
            if (r.getRoomType() == Room.Type.STAGE)
            {
                r.getNewCard();
            }
        }

    }

    public void endGame ()
    {
        for (Player p: players)
        {
            System.out.println(p.getScore());
        }
    }

}
