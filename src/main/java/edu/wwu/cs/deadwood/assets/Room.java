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

import edu.wwu.cs.deadwood.Player;

import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * @author Connor Hollasch
 * @since October 31, 1:53 PM
 */
public class Room
{
    private final Type roomType;
    private final String name;

    private final int totalShotMarkers;
    private int currentShotCounter;

    private final Collection<Room> adjacentRooms;
    private final Collection<Role> extraRoles;

    private Card card;
    private boolean sceneFinished;

    private final Collection<Player> players;

    public Room (final Type roomType, final String name, final int totalShotMarkers)
    {
        this.roomType = roomType;
        this.name = name;
        this.totalShotMarkers = totalShotMarkers;

        this.currentShotCounter = 0;
        this.adjacentRooms = new HashSet<>();
        this.extraRoles = new HashSet<>();
        this.card = null;
        this.sceneFinished = false;
        this.players = new HashSet<>();
    }

    public boolean isAdjacentTo (final Room other)
    {
        return other != null && this.adjacentRooms.contains(other);
    }

    public Type getRoomType ()
    {
        return this.roomType;
    }

    public String getName ()
    {
        return this.name;
    }

    public int getTotalShotMarkers ()
    {
        return this.totalShotMarkers;
    }

    public int getCurrentShotCounter ()
    {
        return this.currentShotCounter;
    }

    public void setCurrentShotCounter (final int currentShotCounter)
    {
        this.currentShotCounter = currentShotCounter;
    }

    public Collection<Room> getAdjacentRooms ()
    {
        return this.adjacentRooms;
    }

    public Collection<Role> getExtraRoles ()
    {
        return this.extraRoles;
    }

    public Card getCard ()
    {
        return this.card;
    }

    public void setCard (final Card card)
    {
        this.card = card;
    }

    public boolean isSceneFinished ()
    {
        return this.sceneFinished;
    }

    public void setSceneFinished (final boolean sceneFinished)
    {
        this.sceneFinished = sceneFinished;
    }

    public enum Type
    {
        TRAILER,
        CASTING_OFFICE,
        STAGE
    }

    @Override
    public String toString ()
    {
        return "[" + this.roomType.toString()
                + "_" + this.name
                + "_" + this.currentShotCounter + "/" + this.totalShotMarkers
                + "_" + this.extraRoles.toString()
                + "_" + this.adjacentRooms.stream().map(Room::getName).collect(Collectors.toList())
                + "]";
    }
}
