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

    public Player (final Color color)
    {
        this.color = color;

        this.creditCount = 0;
        this.dollarCount = 0;
        this.rank = 1;
        this.practiceChips = 0;
        this.activeRole = null;
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
        YELLOW
    }
}
