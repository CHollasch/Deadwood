/*
 * Copyright (C) 2017 Deadwood - All Rights Reserved
 *
 * Unauthorized copying of this file, via any median is strictly prohibited
 * proprietary and confidential. For more information, please contact me at
 * connor@hollasch.net
 *
 * Written by Connor Hollasch <connor@hollasch.net>, December 2017
 */

package edu.wwu.cs.deadwood.board.gui;


import edu.wwu.cs.deadwood.Game;
import edu.wwu.cs.deadwood.assets.Room;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

/**
 * @author Connor Hollasch
 * @since December 06, 1:08 PM
 */
public class ActionPanel extends JPanel
{
    private JButton bMove;
    private JButton bTakeRole;
    private JButton bRehearse;
    private JButton bAct;
    private JButton bUpgrade;
    private JButton bEndTurn;
    private JButton bEndDaye;
    private JButton bEndGame;

    private Game game;
    private GUIBoard board;

    private JPopupMenu menuPopup;

    private Collection<Room> adjacentRooms;


    public ActionPanel (final Game game, final GUIBoard board)
    {
        this.game = game;
        this.board = board;

        this.bMove = new JButton("Move");
        this.bMove.addActionListener(e -> {
            menuPopup = new JPopupMenu();

            adjacentRooms = game.getCurrentPlayer().getPlayer().getCurrentRoom().getAdjacentRooms();
            for (Room room: adjacentRooms) {
                menuPopup.add(new JMenuItem(room.getName()));
                //menuPopup.addPopupMenuListener();
            }

            menuPopup.setVisible(true);




            System.out.println("Move Clicked");

        });


        add(this.bMove);



    }



}
