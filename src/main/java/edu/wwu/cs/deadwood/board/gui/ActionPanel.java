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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Connor Hollasch
 * @since December 06, 1:08 PM
 */
public class ActionPanel extends JPanel
{
    private LinkedHashMap<Actionable, JButton> actionButtonMap;
    private LinkedHashMap<String, JLabel> statsMap;

    private Game game;
    private GUIBoard board;

    private JPanel buttonPanel;

    private JPanel statsPanel;
    private JLabel currentPlayerLabel;

    public ActionPanel (final Game game, final GUIBoard board)
    {
        this.game = game;
        this.board = board;

        setPreferredSize(new Dimension(250, 0));
        setLayout(new BorderLayout());

        this.buttonPanel = new JPanel();
        this.buttonPanel.setLayout(new GridLayout(3, 2));
        this.buttonPanel.setPreferredSize(new Dimension(250, 100));

        this.actionButtonMap = new LinkedHashMap<>();
        this.statsMap = new LinkedHashMap<>();

        createActionButton("Act", Actionable.ACT, actionEvent -> this.game.currentPlayerAct());
        createActionButton("End Turn", Actionable.END_TURN, actionEvent -> this.game.currentPlayerEndTurn());

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
                    ActionPanel.this.board.getBoardPanel(),
                    "Pick a location to move to...",
                    "Move",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    adjacentChoices,
                    adjacentChoices[0]);

            if (choice == null) {
                return;
            }

            final Room movingTo = AssetManager.getInstance().getRoomMap().get(choice.toLowerCase());
            this.game.currentPlayerMove(movingTo);
        });

        createActionButton("Rehearse", Actionable.REHEARSE, actionEvent -> this.game.currentPlayerRehearse());

        createActionButton("Take Role", Actionable.TAKE_ROLE, actionEvent -> {
            final Player currentPlayer = this.game.getCurrentPlayer().getPlayer();

            final Map<String, Role> roleMap = new HashMap<>();
            final Collection<Role> availableRoles = game.getActableRolesByPlayer(currentPlayer);

            final Role[] roles = availableRoles.toArray(new Role[0]);
            final String[] roleChoices = new String[roles.length];

            for (int i = 0; i < roles.length; ++i) {
                roleChoices[i] = roles[i].getName();
                roleMap.put(roles[i].getName(), roles[i]);
            }

            final String choice = (String) JOptionPane.showInputDialog(
                    ActionPanel.this.board.getBoardPanel(),
                    "Pick a role to take ...",
                    "Roles",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    roleChoices,
                    roleChoices[0]);

            if (choice == null) {
                return;
            }

            final Role chosenRole = roleMap.get(choice);
            this.game.currentPlayerTakeRole(chosenRole);
        });

        createActionButton("Upgrade", Actionable.UPGRADE, new ActionListener()
        {
            @Override
            public void actionPerformed (ActionEvent actionEvent)
            {
                final String choice = (String) JOptionPane.showInputDialog(
                        ActionPanel.this.board.getBoardPanel(),
                        "Pick a method of payment for upgrading...",
                        "Upgrade",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        new String[] {"Credits", "Dollars"},
                        "Credits");

                if (choice == null) {
                    return;
                }

                final Player player = ActionPanel.this.game.getCurrentPlayer().getPlayer();
                final int rank = player.getRank();

                if (choice.equals("Credits")) {
                    final int credits = player.getCreditCount();
                    final int creditsNeeded = AssetManager.getCreditUpgradeCost(player.getRank() + 1);

                    if (creditsNeeded > credits) {
                        JOptionPane.showOptionDialog(
                                ActionPanel.this.board.getBoardPanel(),
                                "You cannot afford to upgrade to any rank with credits!",
                                "Error",
                                JOptionPane.PLAIN_MESSAGE,
                                JOptionPane.ERROR_MESSAGE,
                                null,
                                new String[]{"Ok"},
                                "Ok");
                    } else {
                        final Integer goingTo = getUpgradingTo(rank);

                        if (goingTo == null) {
                            return;
                        }

                        ActionPanel.this.game.currentPlayerUpgrade(true, goingTo);
                    }
                } else {
                    final int dollars = player.getCreditCount();
                    final int dollarsNeeded = AssetManager.getDollarUpgradeCost(player.getRank() + 1);

                    if (dollarsNeeded > dollars) {
                        JOptionPane.showOptionDialog(
                                ActionPanel.this.board.getBoardPanel(),
                                "You cannot afford to upgrade to any rank with dollars!",
                                "Error",
                                JOptionPane.PLAIN_MESSAGE,
                                JOptionPane.ERROR_MESSAGE,
                                null,
                                new String[]{"Ok"},
                                "Ok");
                    } else {
                        final Integer goingTo = getUpgradingTo(rank);

                        if (goingTo == null) {
                            return;
                        }

                        ActionPanel.this.game.currentPlayerUpgrade(false, goingTo);
                    }
                }
            }

            private Integer getUpgradingTo (final int currentRank)
            {
                final Integer[] options = new Integer[6 - currentRank];
                for (int i = currentRank + 1, j = 0; i <= 6; ++i) {
                    options[j++] = i;
                }

                final int idx = JOptionPane.showOptionDialog(
                        ActionPanel.this.board.getBoardPanel(),
                        "Pick a rank to upgrade to...",
                        "Rank Choice",
                        JOptionPane.PLAIN_MESSAGE,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[0]);

                if (idx == -1) {
                    return null;
                }

                return options[idx];
            }
        });

        for (final JButton button : this.actionButtonMap.values()) {
            this.buttonPanel.add(button);
        }

        add(this.buttonPanel, BorderLayout.NORTH);

        this.statsPanel = new JPanel();
        this.statsPanel.setLayout(new BoxLayout(this.statsPanel, BoxLayout.Y_AXIS));

        createStats();

        add(this.statsPanel);

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

        updateStats();
    }

    private void createStats ()
    {
        createStat("day", "Current Day", "1");
        createStat("player", "Current Player", "...");
        createStat("rank", "Players Rank", "...");
        createStat("money", "Players Money", "...");
        createStat("credits", "Players Credits", "...");
        createStat("rehearsalPoints", "Rehearsal Points", "...");
    }

    private void createStat (final String stat, final String header, final String value)
    {
        final JLabel statsHeader = new JLabel(header);
        Font font = statsHeader.getFont();
        Font bigger = font.deriveFont(25f);
        statsHeader.setFont(bigger);
        statsHeader.setAlignmentX(CENTER_ALIGNMENT);

        final JLabel statValue = new JLabel(value);
        font = statValue.getFont();
        bigger = font.deriveFont(20f);
        statValue.setFont(bigger);
        statValue.setAlignmentX(CENTER_ALIGNMENT);

        this.statsMap.put(stat, statValue);

        this.statsPanel.add(Box.createVerticalStrut(15));
        this.statsPanel.add(statsHeader);
        this.statsPanel.add(statValue);
        this.statsPanel.add(Box.createVerticalStrut(15));
    }

    private void updateStats ()
    {
        this.statsMap.get("day").setText(String.valueOf((this.game.getMaxDays() + 1) - this.game.getDaysLeft()));
        final JLabel player = this.statsMap.get("player");

        final Player current = this.game.getCurrentPlayer().getPlayer();
        player.setText(current.getColor().name());
        player.setBackground(Color.black);
        player.setForeground(Color.decode("#" + current.getColor().getHex()));

        final int rank = current.getRank();
        final int money = current.getDollarCount();
        final int credits = current.getCreditCount();
        final int rehearsalPoinst = current.getPracticeChips();

        this.statsMap.get("rank").setText(String.valueOf(rank));
        this.statsMap.get("money").setText(String.valueOf(money));
        this.statsMap.get("credits").setText(String.valueOf(credits));
        this.statsMap.get("rehearsalPoints").setText(String.valueOf(rehearsalPoinst));
    }

    private void createActionButton (final String action, final Actionable actionable, final ActionListener listener)
    {
        final JButton button = new JButton(action);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.addActionListener(listener);

        this.actionButtonMap.put(actionable, button);
    }
}
