/*
 * Copyright (C) 2017 Deadwood - All Rights Reserved
 *
 * Unauthorized copying of this file, via any median is strictly prohibited
 * proprietary and confidential. For more information, please contact me at
 * connor@hollasch.net
 *
 * Written by Connor Hollasch <connor@hollasch.net>, December 2017
 */

package edu.wwu.cs.deadwood.util;

/**
 * @author Connor Hollasch
 * @since December 06, 2:03 PM
 */
public class Location
{
    private int x;
    private int y;

    private int width;
    private int height;

    public Location (final int x, final int y, final int width, final int height)
    {
        this.x = x;
        this.y = y;

        this.width = width;
        this.height = height;
    }

    public Location add (final Location other)
    {
        final int nX = this.x + other.x;
        final int nY = this.y + other.y;

        return new Location(nX, nY, getWidth(), getHeight());
    }

    public int getX ()
    {
        return this.x;
    }

    public int getY ()
    {
        return this.y;
    }

    public int getWidth ()
    {
        return this.width;
    }

    public int getHeight ()
    {
        return this.height;
    }
}
