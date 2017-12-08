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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.*;

/**
 * @author Connor Hollasch
 * @since December 06, 1:08 PM
 */
class ActionPanel extends JPanel
{
    private LinkedHashMap<Actionable, JButton> actionButtonMap;
    private LinkedHashMap<String, JLabel> statsMap;

    private Game game;
    private GUIBoard board;

    private JPanel buttonPanel;
    private JPanel statsPanel;
    private JLabel statusLabel;

    private MouseListener boardActionMouseListener;

    ActionPanel (final Game game, final GUIBoard board)
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

        createActionButtons();

        add(this.buttonPanel, BorderLayout.NORTH);

        this.statsPanel = new JPanel();
        this.statsPanel.setLayout(new BoxLayout(this.statsPanel, BoxLayout.Y_AXIS));
        createStats();
        add(this.statsPanel);

        this.statusLabel = new JLabel("Waiting for input...");
        this.statusLabel.setAlignmentX(CENTER_ALIGNMENT);
        increaseFontSize(this.statusLabel, 14f);

        final JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
        statusPanel.add(this.statusLabel);

        add(statusPanel, BorderLayout.SOUTH);

        update();
    }

    private void createActionButtons ()
    {
        createActionButton("Act", Actionable.ACT, actionEvent -> this.game.currentPlayerAct());
        createActionButton("End Turn", Actionable.END_TURN, actionEvent -> this.game.currentPlayerEndTurn());

        createActionButton("Move", Actionable.MOVE, actionEvent -> {
            // Let the board panel know we're performing a move event.
            ActionPanel.this.board.getBoardPanel().setTakingRoomInput(true);
            setStatusText("Hover on room to select");

            ActionPanel.this.boardActionMouseListener = new MouseListener()
            {
                @Override
                public void mouseClicked (MouseEvent e)
                {
                    check();
                }

                @Override
                public void mousePressed (MouseEvent e)
                {
                    check();
                }

                @Override
                public void mouseReleased (MouseEvent e)
                {
                    check();
                }

                public void mouseEntered (MouseEvent e) {}
                public void mouseExited (MouseEvent e) {}

                private void check ()
                {
                    final Room hoveringOver = ActionPanel.this.board.getBoardPanel().getRoomHoveringOver();
                    final Collection<Room> adjacent = ActionPanel.this.game.getCurrentPlayer().getPlayer()
                            .getCurrentRoom().getAdjacentRooms();

                    // Move to the room being hovered over if it is valid.
                    if (hoveringOver != null && adjacent.contains(hoveringOver)) {
                        ActionPanel.this.game.currentPlayerMove(hoveringOver);
                        clearChoiceState();
                        resetStatusText();
                    } else {
                        setStatusText("Cannot move there!");
                    }
                }
            };

            ActionPanel.this.board.getBoardPanel().addMouseListener(ActionPanel.this.boardActionMouseListener);
        });

        createActionButton("Rehearse", Actionable.REHEARSE, actionEvent -> this.game.currentPlayerRehearse());

        createActionButton("Take Role", Actionable.TAKE_ROLE, actionEvent -> {
            final Player player = ActionPanel.this.game.getCurrentPlayer().getPlayer();

            final Room currentRoom = player.getCurrentRoom();
            final Collection<Role> availableRoles = ActionPanel.this.game.getActableRolesByPlayer(player);

            ActionPanel.this.board.getBoardPanel().setTakingRoleInput(currentRoom, availableRoles);
            setStatusText("Hover on role to select");

            ActionPanel.this.boardActionMouseListener = new MouseListener()
            {
                @Override
                public void mouseClicked (final MouseEvent e)
                {
                    check();
                }

                @Override
                public void mousePressed (MouseEvent e)
                {
                    check();
                }

                @Override
                public void mouseReleased (MouseEvent e)
                {
                    check();
                }

                public void mouseEntered (MouseEvent e) {}
                public void mouseExited (MouseEvent e) {}

                private void check ()
                {
                    final Role hovering = ActionPanel.this.board.getBoardPanel().getRoleHoveringOver();

                    if (hovering == null || !availableRoles.contains(hovering)) {
                        setStatusText("Cannot take that role!");
                        return;
                    }

                    // Take the role if an available role is being hovered over.
                    ActionPanel.this.game.currentPlayerTakeRole(hovering);
                    clearChoiceState();
                    resetStatusText();
                }
            };

            ActionPanel.this.board.getBoardPanel().addMouseListener(ActionPanel.this.boardActionMouseListener);
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

                if (choice.equals("Credits")) {
                    final int credits = player.getCreditCount();
                    final int creditsNeeded = AssetManager.getCreditUpgradeCost(player.getRank() + 1);

                    if (creditsNeeded > credits) {
                        JOptionPane.showOptionDialog(
                                ActionPanel.this.board.getBoardPanel(),
                                "You cannot afford to upgrade to any rank with credits!",
                                "Error",
                                JOptionPane.DEFAULT_OPTION,
                                JOptionPane.ERROR_MESSAGE,
                                null,
                                new String[]{"Ok"},
                                "Ok");
                    } else {
                        final Integer goingTo = getUpgradingTo(player, true);

                        if (goingTo == null) {
                            return;
                        }

                        ActionPanel.this.game.currentPlayerUpgrade(true, goingTo);
                    }
                } else {
                    final int dollars = player.getDollarCount();
                    final int dollarsNeeded = AssetManager.getDollarUpgradeCost(player.getRank() + 1);

                    if (dollarsNeeded > dollars) {
                        JOptionPane.showOptionDialog(
                                ActionPanel.this.board.getBoardPanel(),
                                "You cannot afford to upgrade to any rank with dollars!",
                                "Error",
                                JOptionPane.DEFAULT_OPTION,
                                JOptionPane.ERROR_MESSAGE,
                                null,
                                new String[]{"Ok"},
                                "Ok");
                    } else {
                        final Integer goingTo = getUpgradingTo(player, false);

                        if (goingTo == null) {
                            return;
                        }

                        ActionPanel.this.game.currentPlayerUpgrade(false, goingTo);
                    }
                }
            }

            private Integer getUpgradingTo (final Player player, final boolean usingCredits)
            {
                final int currentRank = player.getRank();
                final java.util.List<Integer> canUpgradeTo = new ArrayList<>();

                for (int i = currentRank + 1; i <= 6; ++i) {
                    final int cost = (usingCredits
                            ? AssetManager.getCreditUpgradeCost(i) : AssetManager.getDollarUpgradeCost(i));
                    final int has = (usingCredits ? player.getCreditCount() : player.getDollarCount());

                    if (has >= cost) {
                        canUpgradeTo.add(i);
                    }
                }

                final Integer[] options = canUpgradeTo.toArray(new Integer[canUpgradeTo.size()]);

                final int idx = JOptionPane.showOptionDialog(
                        ActionPanel.this.board.getBoardPanel(),
                        "Pick a rank to upgrade to...",
                        "Rank Choice",
                        JOptionPane.DEFAULT_OPTION,
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
    }

    private void clearChoiceState ()
    {
        // Remove the action mouse listener whenever the choice state is cleared (new action chosen, etc).
        this.board.getBoardPanel().removeMouseListener(this.boardActionMouseListener);

        this.board.getBoardPanel().setTakingRoomInput(false);
        this.board.getBoardPanel().clearTakingRoleInput();
    }

    void update ()
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
        createStat("rank", "Rank", "...");
        createStat("money", "Dollars", "...");
        createStat("credits", "Credits", "...");
        createStat("rehearsalPoints", "Rehearsal Points", "...");
        createStat("score", "Players Score", "...");
    }

    private void resetStatusText ()
    {
        this.statusLabel.setText("Waiting for input...");
    }

    private void setStatusText (final String status)
    {
        this.statusLabel.setText(status);
    }

    private void createStat (final String stat, final String header, final String value)
    {
        final JLabel statsHeader = new JLabel(header);
        increaseFontSize(statsHeader, 25f);
        statsHeader.setAlignmentX(CENTER_ALIGNMENT);

        final JLabel statValue = new JLabel(value);
        increaseFontSize(statValue, 20f);
        statValue.setAlignmentX(CENTER_ALIGNMENT);

        this.statsMap.put(stat, statValue);

        this.statsPanel.add(Box.createVerticalStrut(15));
        this.statsPanel.add(statsHeader);
        this.statsPanel.add(statValue);
        this.statsPanel.add(Box.createVerticalStrut(15));
    }

    private void increaseFontSize(final JLabel label, final float newFontSize)
    {
        Font font = label.getFont();
        Font bigger = font.deriveFont(newFontSize);
        label.setFont(bigger);
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
        this.statsMap.get("score").setText(String.valueOf(current.getScore()));
    }

    private void createActionButton (final String action, final Actionable actionable, final ActionListener listener)
    {
        final JButton button = new JButton(action);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        button.addActionListener(e -> {
            clearChoiceState();
            listener.actionPerformed(e);
        });

        this.actionButtonMap.put(actionable, button);
    }
}
