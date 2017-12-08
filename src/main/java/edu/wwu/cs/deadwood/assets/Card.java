package edu.wwu.cs.deadwood.assets;

import java.util.Collection;

/**
 * @author Connor Hollasch
 * @since October 31, 1:53 PM
 */
public class Card
{
    //==================================================================================================================
    // Local variables.
    //==================================================================================================================

    private final String name;
    private final String description;
    private final int sceneNumber;

    private final int cardBudget;
    private final Collection<Role> roles;

    private boolean visible;

    //==================================================================================================================
    // Constructors.
    //==================================================================================================================

    Card (
            final String name,
            final String description,
            final int sceneNumber,
            final int cardBudget,
            final Collection<Role> roles)
    {
        this.name = name;
        this.description = description;
        this.sceneNumber = sceneNumber;

        this.cardBudget = cardBudget;
        this.roles = roles;

        this.visible = false;
    }

    //==================================================================================================================
    // Getters.
    //==================================================================================================================

    public String getName ()
    {
        return this.name;
    }

    @SuppressWarnings("unused")
    public String getDescription ()
    {
        return this.description;
    }

    public int getSceneNumber ()
    {
        return this.sceneNumber;
    }

    public int getCardBudget ()
    {
        return this.cardBudget;
    }

    public Collection<Role> getRoles ()
    {
        return this.roles;
    }

    public boolean isVisible ()
    {
        return this.visible;
    }

    public void setVisible (final boolean visible)
    {
        this.visible = visible;
    }

    //==================================================================================================================
    // To string.
    //==================================================================================================================

    @Override
    public String toString ()
    {
        return "[" + this.name + "_" + this.description + "_" + this.cardBudget + "_" + this.roles + "]";
    }
}
