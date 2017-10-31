/*
 * Copyright (C) 2017 Deadwood - All Rights Reserved
 *
 * Unauthorized copying of this file, via any median is strictly prohibited
 * proprietary and confidential. For more information, please contact me at
 * connor@hollasch.net
 *
 * Written by Connor Hollasch <connor@hollasch.net>, October 2017
 */

package edu.wwu.cs.deadwood.board;

import edu.wwu.cs.deadwood.Player;
import edu.wwu.cs.deadwood.assets.Role;
import edu.wwu.cs.deadwood.assets.Room;

import javax.swing.*;

/**
 * @author Connor Hollasch
 * @since October 31, 1:54 PM
 */
public class GUIBoard implements Board
{
    private JFrame frame;

    public void displayWindow ()
    {
        // Display the frame & initialize.
    }

    public void destroyWindow ()
    {
        // Destroy the frame.
    }

    @Override
    public void refreshBoard ()
    {

    }

    @Override
    public void playerActed (Player player, boolean successful, int diceRoll)
    {

    }

    @Override
    public void playerEndedTurn (Player player)
    {

    }

    @Override
    public void playerMoved (Player player, Room newRoom)
    {

    }

    @Override
    public void playerRehearsed (Player player)
    {

    }

    @Override
    public void playerTookRole (Player player, Role role)
    {

    }

    @Override
    public void playerUpgraded (Player player, boolean usedCredits, int rankUpgradingTo)
    {

    }
}
