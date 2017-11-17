package edu.wwu.cs.deadwood.assets;

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

    //==================================================================================================================
    // Constructors.
    //==================================================================================================================

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

    //==================================================================================================================
    // To string.
    //==================================================================================================================

    @Override
    public String toString ()
    {
        return "(" + this.name + "," + this.line + "," + this.minimumRank + "," + (this.extraRole ? "extra" : "main") + ")";
    }
}
