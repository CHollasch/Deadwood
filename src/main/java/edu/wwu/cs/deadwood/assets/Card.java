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

import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

/**
 * @author Connor Hollasch
 * @since October 31, 1:53 PM
 */
@Getter
public class Card
{
    private final String name;
    private final String description;

    private final int cardBudget;
    private final Role[] roles;
    private final Collection<Player> players;

    @Setter
    private boolean isVisible;

    public Card (final String name, final String description, final int cardBudget, final Role[] roles)
    {
        this.name = name;
        this.description = description;
        this.cardBudget = cardBudget;
        this.roles = roles;
        this.players = null;

        this.isVisible = false;
    }

    public int getCardBudget()
    {
        return cardBudget;
    }

    public boolean hasPlayers()
    {
        return players != null;
    }

    public void discard()
    {
        isVisible = false;
        // better to remove it from board
    }

    public Role[] getRoles()
    {
        return roles;
    }
}
