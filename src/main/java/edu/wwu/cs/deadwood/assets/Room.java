package edu.wwu.cs.deadwood.assets;

import edu.wwu.cs.deadwood.Player;
import edu.wwu.cs.deadwood.util.Location;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Connor Hollasch
 * @since October 31, 1:53 PM
 */
public class Room
{
    //==================================================================================================================
    // Local variables.
    //==================================================================================================================

    private final Type roomType;
    private final String name;

    private final int totalShotMarkers;
    private int currentShotCounter;

    private final Collection<Room> adjacentRooms;
    private final Collection<Role> extraRoles;

    private final Location cardLocation;
    private final Location hoverLocation;

    private final Map<Integer, Location> shotMarkerLocations;
    private final Map<Player.Color, Location> playerLocations;

    private Card card;
    private boolean sceneFinished;

    //==================================================================================================================
    // Constructors.
    //==================================================================================================================

    public Room (
            final Type roomType,
            final String name,
            final int totalShotMarkers,
            final Location cardLocation,
            final Location hoverLocation)
    {
        this.roomType = roomType;
        this.name = name;
        this.totalShotMarkers = totalShotMarkers;
        this.cardLocation = cardLocation;
        this.hoverLocation = hoverLocation;

        this.currentShotCounter = 0;
        this.adjacentRooms = new HashSet<>();
        this.extraRoles = new HashSet<>();
        this.shotMarkerLocations = new HashMap<>();
        this.playerLocations = new HashMap<>();
        this.card = null;
        this.sceneFinished = false;
    }

    //==================================================================================================================
    // Public API.
    //==================================================================================================================

    public boolean isAdjacentTo (final Room other)
    {
        return other != null && this.adjacentRooms.contains(other);
    }

    //==================================================================================================================
    // Getters and Setters.
    //==================================================================================================================

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

    public Map<Integer, Location> getShotMarkerLocations ()
    {
        return this.shotMarkerLocations;
    }

    public Location getCardLocation ()
    {
        return this.cardLocation;
    }

    public Location getHoverLocation ()
    {
        return this.hoverLocation;
    }

    public Map<Player.Color, Location> getPlayerLocations ()
    {
        return this.playerLocations;
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

    //==================================================================================================================
    // To string.
    //==================================================================================================================

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

    //==================================================================================================================
    // Room type.
    //==================================================================================================================

    public enum Type
    {
        TRAILER,
        CASTING_OFFICE,
        STAGE
    }
}
