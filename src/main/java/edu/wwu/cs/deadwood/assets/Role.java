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

/**
 * @author Connor Hollasch
 * @since October 31, 1:44 PM
 */
public class Role
{
    private final String name;
    private final String line;

    private final int minimumRank;
    private final boolean extraRole;

    public Role (
            final String name,
            final String line,
            final int minimumRank,
            final boolean extraRole)
    {
        this.name = name;
        this.line = line;
        this.minimumRank = minimumRank;
        this.extraRole = extraRole;
    }
}
