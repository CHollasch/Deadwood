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


import edu.wwu.cs.deadwood.Actionable;
import edu.wwu.cs.deadwood.Game;
import edu.wwu.cs.deadwood.Player;
import edu.wwu.cs.deadwood.assets.AssetManager;
import edu.wwu.cs.deadwood.assets.Role;
import edu.wwu.cs.deadwood.assets.Room;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.LinkedHashMap;

/**
 * @author Connor Hollasch
 * @since December 06, 1:08 PM
 */
public class ActionPanel extends JPanel
{
    private LinkedHashMap<Actionable, JButton> actionButtonMap;

    private Game game;
    private GUIBoard board;

    private JPopupMenu menuPopup;

    private Collection<Room> adjacentRooms;
    private Collection<Role> roles;

    public ActionPanel (final Game game, final GUIBoard board)
    {
        this.game = game;
        this.board = board;

        setPreferredSize(new Dimension(150, 0));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        this.actionButtonMap = new LinkedHashMap<>();

        createActionButton("Act", Actionable.ACT, actionEvent -> {});

        createActionButton("End Turn", Actionable.END_TURN, actionEvent -> {});

        createActionButton("Move", Actionable.MOVE, actionEvent -> {
            final Player currentPlayer = this.game.getCurrentPlayer().getPlayer();
            final Room currentRoom = currentPlayer.getCurrentRoom();
            final Collection<Room> adjacentRooms = currentRoom.getAdjacentRooms();

            final Room[] adjacent = adjacentRooms.toArray(new Room[0]);
            final String[] adjacentChoices = new String[adjacent.length];

            for (int i = 0; i < adjacent.length; ++i) {
                adjacentChoices[i] = adjacent[i].getName();
            }

            final String choice = (String) JOptionPane.showInputDialog(
                    null,
                    "Pick a location to move to...",
                    "Move",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    adjacentChoices,
                    adjacentChoices[0]);

            final Room movingTo = AssetManager.getInstance().getRoomMap().get(choice.toLowerCase());
            this.game.currentPlayerMove(movingTo);
        });

        createActionButton("Rehearse", Actionable.REHEARSE, actionEvent -> {});

        createActionButton("Take Role", Actionable.TAKE_ROLE, actionEvent -> {});

        createActionButton("Upgrade", Actionable.UPGRADE, actionEvent -> {});

        for (final JButton button : this.actionButtonMap.values()) {
            add(button);
        }

        update();
    }

    public void update ()
    {
        final Collection<Actionable> actions = this.game.getCurrentPlayerPossibleActions();
        for (final Actionable action : this.actionButtonMap.keySet()) {
            final JButton button = this.actionButtonMap.get(action);

            if (actions.contains(action)) {
                button.setEnabled(true);
            } else {
                button.setEnabled(false);
            }
        }
    }

    private void createActionButton (final String action, final Actionable actionable, final ActionListener listener)
    {
        final JButton button = new JButton(action);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.addActionListener(listener);

        this.actionButtonMap.put(actionable, button);
    }
}
