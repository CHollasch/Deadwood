/*
 * Copyright (C) 2017 Deadwood - All Rights Reserved
 *
 * Unauthorized copying of this file, via any median is strictly prohibited
 * proprietary and confidential. For more information, please contact me at
 * connor@hollasch.net
 *
 * Written by Yichuan Yin <yiny@wwu.edu>, November 2017
 */

package edu.wwu.cs.deadwood;

import edu.wwu.cs.deadwood.assets.Card;
import edu.wwu.cs.deadwood.assets.Role;
import edu.wwu.cs.deadwood.assets.Room;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Yichuan Yin
 * @since November 9, 10:14 PM
 */

public class Rank
{
    private int[] rankByMoney;
    private int[] rankByCredits;

    public Rank ()
    {
        rankByMoney = new int[6];
        rankByCredits = new int[6];
    }

    public int creditsRequired (int desiredRank)
    {
        return rankByCredits[desiredRank];
    }

    public int moneyRequried (int desiredRank)
    {
        return rankByMoney[desiredRank];
    }
}
