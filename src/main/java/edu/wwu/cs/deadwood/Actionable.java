package edu.wwu.cs.deadwood;

/**
 * @author Connor Hollasch
 * @since Oct 23, 5:55 PM
 */
public enum Actionable
{
    ACT("act"),
    END_TURN("end"),
    MOVE("move"),
    REHEARSE("rehearse"),
    TAKE_ROLE("work"),
    UPGRADE("upgrade");

    //==================================================================================================================
    // Local variables.
    //==================================================================================================================

    private String actionName;

    //==================================================================================================================
    // Constructors.
    //==================================================================================================================

    Actionable (final String actionName)
    {
        this.actionName = actionName;
    }

    //==================================================================================================================
    // Public API.
    //==================================================================================================================

    public String getActionName ()
    {
        return this.actionName;
    }

    @Override
    public String toString ()
    {
        return getActionName();
    }
}
