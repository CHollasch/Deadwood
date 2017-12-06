package edu.wwu.cs.deadwood.assets;

import edu.wwu.cs.deadwood.util.Location;

/**
 * @author Connor Hollasch
 * @since October 31, 1:44 PM
 */
public class Role
{
    //==================================================================================================================
    // Local variables.
    //==================================================================================================================

    private final String name;
    private final String line;

    private final int minimumRank;
    private final boolean extraRole;

    private Location location;

    //==================================================================================================================
    // Constructors.
    //==================================================================================================================

    public Role (
            final String name,
            final String line,
            final int minimumRank,
            final boolean extraRole,
            final Location location)
    {
        this.name = name;
        this.line = line;
        this.minimumRank = minimumRank;
        this.extraRole = extraRole;

        this.location = location;
    }

    //==================================================================================================================
    // Getters
    //==================================================================================================================

    public String getName ()
    {
        return this.name;
    }

    public String getLine ()
    {
        return this.line;
    }

    public int getMinimumRank ()
    {
        return this.minimumRank;
    }

    public boolean isExtraRole ()
    {
        return this.extraRole;
    }

    public Location getLocation ()
    {
        return this.location;
    }

    //==================================================================================================================
    // To string.
    //==================================================================================================================

    @Override
    public String toString ()
    {
        return "(" + this.name + "," + this.line + "," + this.minimumRank + "," + (this.extraRole ? "extra" : "main") + ")";
    }
}
