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

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Connor Hollasch
 * @since October 31, 1:44 PM
 */
@Getter
@AllArgsConstructor
public class Role
{
    private final String name;
    private final String line;
    private Player player;

    private final int minimumRank;
    private final boolean extraRole;

    private boolean isTaken;
    private boolean isOnCard;

    public boolean isTaken()
    {
        return isTaken;
    }

    public boolean isOnCard(Card card)
    {
        return isOnCard;
    }

    public int getMinimumRank()
    {
        return minimumRank;
    }

    public void setTaken()
    {
        isTaken = true;
    }

    public void setPlayer(Player player)
    {
        this.player = player;
    }

    public Player getPlayer()
    {
        return player;
    }

}
