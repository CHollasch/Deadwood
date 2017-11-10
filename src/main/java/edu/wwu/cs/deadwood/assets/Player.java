/*
 * Copyright (C) 2017 Deadwood - All Rights Reserved
 *
 * Unauthorized copying of this file, via any median is strictly prohibited
 * proprietary and confidential. For more information, please contact me at
 * connor@hollasch.net
 *
 * Written by Connor Hollasch <connor@hollasch.net>, October 2017
 */

package edu.wwu.cs.deadwood.assets;

import edu.wwu.cs.deadwood.assets.Card;
import edu.wwu.cs.deadwood.assets.Role;
import edu.wwu.cs.deadwood.assets.Room;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Connor Hollasch
 * @since October 31, 1:42 PM
 */

@Getter
@Setter
public class Player
{
    private final Color color;

    private int creditCount;
    private int dollarCount;
    private int rank;

    private int practiceChips;
    private Role activeRole;
    private Card activeCard;
    private Room currentRoom;

    public Player (final Color color)
    {
        this.color = color;

        this.creditCount = creditCount;
        this.dollarCount = 0;
        this.rank = 1;
        this.practiceChips = 0;
        this.activeRole = null;
        this.activeCard = null;
    }

    public enum Color
    {
        BLUE,
        CYAN,
        GREEN,
        ORANGE,
        PINK,
        RED,
        VIOLET,
    }

    public boolean isActing()
    {
        return activeRole != null;
    }

    public void addOnePracticeChip()
    {
        practiceChips++;
    }

    public void onCardReward()
    {
        creditCount += 2;
    }

    public void offCardReward()
    {
        creditCount++;
        dollarCount++;
    }

    public boolean hasEnoughRank(Role role)
    {
        return rank >= role.getMinimumRank();
    }

    public Room getRoom()
    {
        return currentRoom;
    }

    public void setActiveRole(Role role)
    {
        activeRole = role;
    }

    public Role getActiveRole()
    {
        return activeRole;
    }

    public int getPracticeChips()
    {
        return practiceChips;
    }

    public Card getActiveCard()
    {
        return activeCard;
    }

    public void setRoom(Room room)
    {
        currentRoom = room;
    }

    public boolean isAtCastingOffice()
    {
        return currentRoom.getRoomType() == Room.Type.CASTING_OFFICE;
    }

    public int getRank()
    {
        return rank;
    }

    public int getCreditCount()
    {
        return creditCount;
    }

    public int getDollarCount()
    {
        return dollarCount;
    }

    public void setRank(int newRank)
    {
        rank = newRank;
    }

    public void takeMoneyAway(int moneyOut)
    {
        dollarCount -= moneyOut;
    }

    public void takeCreditsAway(int creditsOut)
    {
        dollarCount -= creditsOut;
    }

    public void receiveMoney(int moneyReceived)
    {
        dollarCount += moneyReceived;
    }

    public void setActiveCard(Card card)
    {
        activeCard = card;
    }

    public int getScore()
    {
        return dollarCount + creditCount + rank * 5;
    }

    public void setCreditCount(int count)
    {
        creditCount = count;
    }
}
