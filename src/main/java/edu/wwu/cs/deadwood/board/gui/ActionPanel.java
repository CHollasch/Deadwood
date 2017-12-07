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
import edu.wwu.cs.deadwood.assets.Role;
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
    private Collection<Role> roles;


    public ActionPanel (final Game game, final GUIBoard board)
    {
        this.game = game;
        this.board = board;

        this.bMove = new JButton("Move");
        this.bMove.addActionListener(e -> {
            menuPopup = new JPopupMenu();

            adjacentRooms = game.getCurrentPlayer().getPlayer().getCurrentRoom().getAdjacentRooms();
            for (Room room: adjacentRooms) {
                JMenuItem roomItem = new JMenuItem(room.getName());
                roomItem.addActionListener(d -> {
                    game.currentPlayerMove(room);
                    System.out.println("Room clicked");
                });
                menuPopup.add(roomItem);

            }
            System.out.println("Move Clicked");
            menuPopup.setVisible(true);
        });
        add(this.bMove);

        this.bTakeRole = new JButton("Take Role");
        this.bTakeRole.addActionListener(e -> {

            this.roles = game.getCurrentPlayer().getPlayer().getCurrentRoom().getCard().getRoles();

            // String choice = (String)JOptionPane.showInputDialog(this, "Which role would you like to take?", "Taking a Role", JOptionPane.PLAIN_MESSAGE, null, roleOptions, roleOptions[0]);

//            for (Role role: roles) {
//                if (role.getName().equals(choice)) {
//                    game.currentPlayerTakeRole(role);
//                }
//            }


            //          menuPopup = new JPopupMenu();
//
//            roles = game.getCurrentPlayer().getPlayer().getCurrentRoom().getCard().getRoles();
//            for (Role role: roles) {
//                JMenuItem roleItem = new JMenuItem(role.getName());
//                roleItem.addActionListener(d -> {
//                    game.currentPlayerTakeRole(role);
//                    System.out.println("Role clicked");
//                });
//                menuPopup.add(roleItem);
//
//            }
//            System.out.println("Take Role Clicked");
//            menuPopup.setVisible(true);
        });
        add(this.bTakeRole);

        this.bRehearse = new JButton("Rehearse");
        this.bRehearse.addActionListener(e -> {

        });


    }



}
