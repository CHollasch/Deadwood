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
import edu.wwu.cs.deadwood.Player;
import edu.wwu.cs.deadwood.assets.AssetManager;
import edu.wwu.cs.deadwood.assets.Card;
import edu.wwu.cs.deadwood.assets.Role;
import edu.wwu.cs.deadwood.assets.Room;
import edu.wwu.cs.deadwood.util.Location;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * @author Connor Hollasch
 * @since December 06, 1:08 PM
 */
public class BoardPanel extends JPanel
{
    private static final int SCALE_WIDTH = 1200;
    private static final int SCALE_HEIGHT = 900;

    private final Game game;

    private int width;
    private int height;

    public BoardPanel (final Game game)
    {
        this.game = game;
        this.setPreferredSize(new Dimension(this.width = SCALE_WIDTH, this.height = SCALE_HEIGHT));

        addComponentListener(new ComponentListener()
        {
            @Override
            public void componentResized (final ComponentEvent componentEvent)
            {
                BoardPanel.this.width = getWidth();
                BoardPanel.this.height = getHeight();
            }

            public void componentMoved (final ComponentEvent componentEvent) {}
            public void componentShown (final ComponentEvent componentEvent) {}
            public void componentHidden (final ComponentEvent componentEvent) {}
        });
    }

    @Override
    public void paint (final Graphics g)
    {
        if (g instanceof Graphics2D) {
            final Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }

        // Clear screen buffer.
        g.setColor(Color.black);
        g.fillRect(0, 0, this.width, this.height);
        g.setColor(Color.white);

        // Draw the game board first.
        g.drawImage(AssetManager.getInstance().getBoardDrawable(), 0, 0, this.width, this.height, null);

        // Draw all shot markers and cards.
        for (final Room room : AssetManager.getInstance().getRoomMap().values()) {
            // Draw shot markers.
            final int shotsTaken = room.getCurrentShotCounter();
            final int maxShots = room.getTotalShotMarkers();

            for (int i = maxShots - 1; i >= shotsTaken; --i) {
                final Location shotLocation = room.getShotMarkerLocations().get(i + 1);

                drawImageWithScaling(g, AssetManager.getInstance().getShotDrawable(), shotLocation);
            }

            room.setCurrentShotCounter(1);

            // Draw cards when possible.
            if (room.getCardLocation() != null && room.getCard() != null) {
                final Card card = room.getCard();
                final Location cardLocation = room.getCardLocation();

                if (card.isVisible() && !room.isSceneFinished()) {
                    drawImageWithScaling(g, AssetManager.getInstance().getCardImage(card), cardLocation);
                }
            }
        }

        // Draw all players and their current locations.
        for (final Player.Color color : Player.Color.values()) {
            final Player player = this.game.getPlayerColorMap().get(color);

            if (player == null) {
                continue;
            }

            final Room currentRoom = player.getCurrentRoom();
            final Image playerImage = AssetManager.getInstance().getPlayerDice(color, player.getRank());

            if (player.getActiveRole() != null) {
                final Role role = player.getActiveRole();
                if (role.isExtraRole()) {
                    drawImageWithScaling(g, playerImage, role.getLocation());
                } else {
                    drawForRoleOnCard(g, playerImage, currentRoom.getCardLocation(), role);
                }
            } else {
                if (currentRoom.getPlayerLocations().containsKey(color)) {
                    final Location drawTo = currentRoom.getPlayerLocations().get(color);
                    drawImageWithScaling(g, playerImage, drawTo);
                }
            }
        }
    }

    private void drawForRoleOnCard (
            final Graphics graphics,
            final Image image,
            final Location cardLocation,
            final Role role)
    {
        drawImageWithScaling(graphics, image, role.getLocation().add(cardLocation));
    }

    private void drawImageWithScaling (
            final Graphics graphics,
            final Image image,
            final Location location)
    {
        final double xScale = (double) this.width / SCALE_WIDTH;
        final double yScale = (double) this.height / SCALE_HEIGHT;

        final int newX = (int) (location.getX() * xScale);
        final int newY = (int) (location.getY() * yScale);

        final int newWidth = (int) (location.getWidth() * xScale);
        final int newHeight = (int) (location.getHeight() * yScale);

        graphics.drawImage(image, newX, newY, newWidth, newHeight, null);
    }
}
