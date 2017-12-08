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
import java.awt.event.*;
import java.util.Collection;
import java.util.HashSet;

/**
 * @author Connor Hollasch
 * @since December 06, 1:08 PM
 */
class BoardPanel extends JPanel
{
    private static final int SCALE_WIDTH = 1200;
    private static final int SCALE_HEIGHT = 900;

    private final Game game;

    private int width;
    private int height;

    private Room roomHoveringOver;
    private Role roleHoveringOver;

    private boolean isTakingRoomInput = false;

    private Room activeRolesRoom;
    private Collection<Role> availableRoles;
    private Collection<Role> allRoleInputRoles;
    private boolean isTakingRoleInput = false;

    BoardPanel (final Game game)
    {
        this.game = game;
        this.setPreferredSize(new Dimension(this.width = SCALE_WIDTH, this.height = SCALE_HEIGHT));

        addMouseMotionListener(new MouseMotionListener()
        {
            @Override
            public void mouseDragged (final MouseEvent e)
            {
                moveLogic(e);
            }

            @Override
            public void mouseMoved (final MouseEvent e)
            {
                moveLogic(e);
            }

            private void moveLogic (final MouseEvent e)
            {
                if (BoardPanel.this.isTakingRoomInput || BoardPanel.this.isTakingRoleInput) {
                    final int x = e.getX();
                    final int y = e.getY();

                    if (BoardPanel.this.isTakingRoomInput) {
                        final Room oldRoom = BoardPanel.this.roomHoveringOver;

                        boolean wasSet = false;
                        for (final Room room : AssetManager.getInstance().getRoomMap().values()) {
                            final Location hoverLocation = room.getHoverLocation();
                            final Location scaled = scaleLocation(hoverLocation);

                            if (runBoundsCheck(x, y, scaled)) {
                                BoardPanel.this.roomHoveringOver = room;
                                wasSet = true;
                                break;
                            }
                        }

                        if (!wasSet) {
                            BoardPanel.this.roomHoveringOver = null;
                        }

                        if ((((oldRoom != null) && (BoardPanel.this.roomHoveringOver != null))
                                && (!oldRoom.equals(BoardPanel.this.roomHoveringOver)))) {
                            repaint();
                        } else if (((oldRoom == null) && (BoardPanel.this.roomHoveringOver != null))
                                || ((oldRoom != null) && (BoardPanel.this.roomHoveringOver == null))) {
                            repaint();
                        }
                    } else {
                        final Room current = BoardPanel.this.activeRolesRoom;
                        final Location toCheck = scaleLocation(current.getHoverLocation());

                        // Player is hovering in the room, now check for roles.
                        if (runBoundsCheck(x, y, toCheck)) {

                            final Role oldRole = BoardPanel.this.roleHoveringOver;
                            boolean wasSet = false;

                            for (final Role role : BoardPanel.this.allRoleInputRoles) {
                                final Location scaled;

                                if (role.isExtraRole()) {
                                    scaled = scaleLocation(role.getLocation());
                                } else {
                                    scaled = scaleLocation(role.getLocation().add(current.getCardLocation()));
                                }

                                if (runBoundsCheck(x, y, scaled)) {
                                    BoardPanel.this.roleHoveringOver = role;
                                    wasSet = true;
                                }
                            }

                            if (!wasSet) {
                                BoardPanel.this.roleHoveringOver = null;
                            }

                            if ((oldRole == null && BoardPanel.this.roleHoveringOver != null)
                                    || (oldRole != null && BoardPanel.this.roleHoveringOver == null)
                                    || ((oldRole != null && BoardPanel.this.roleHoveringOver != null)
                                    && (!oldRole.equals(BoardPanel.this.roleHoveringOver)))) {
                                repaint();
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    public void repaint ()
    {
        BoardPanel.this.width = getWidth();
        BoardPanel.this.height = getHeight();

        super.repaint();
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

            // Draw cards when possible.
            if (room.getCardLocation() != null && room.getCard() != null) {
                final Card card = room.getCard();
                final Location cardLocation = room.getCardLocation();

                if (card.isVisible() && !room.isSceneFinished()) {
                    drawImageWithScaling(g, AssetManager.getInstance().getCardImage(card), cardLocation);
                } else if (!card.isVisible() && !room.isSceneFinished()) {
                    drawImageWithScaling(g, AssetManager.getInstance().getCardBackDrawable(), cardLocation);
                }
            }
        }

        // Draw all players and their current locations.
        for (final Player.Color color : Player.Color.values()) {
            final Player player = this.game.getPlayerColorMap().get(color);

            if (player == null) {
                continue;
            }

            if (this.game.getCurrentPlayer().getPlayer().equals(player)) {
                continue;
            }

            drawPlayer(g, player);
        }

        drawPlayer(g, this.game.getCurrentPlayer().getPlayer());

        if (this.isTakingRoomInput) {
            final Room hovering = this.roomHoveringOver;

            if (hovering != null) {
                final Location hoverLocation = hovering.getHoverLocation();
                final Collection<Room> adjacent = this.game.getCurrentPlayer().getPlayer().getCurrentRoom().getAdjacentRooms();

                if (adjacent.contains(hovering)) {
                    drawImageWithScaling(g, AssetManager.getInstance().getHoverImage(hovering, true), hoverLocation);
                } else {
                    drawImageWithScaling(g, AssetManager.getInstance().getHoverImage(hovering, false), hoverLocation);
                }
            }
        } else if (this.isTakingRoleInput) {
            final Role hovering = this.roleHoveringOver;

            if (hovering != null) {
                final Location roleLocation = hovering.getLocation();
                final Image toDraw = (this.availableRoles.contains(hovering)
                        ? AssetManager.getInstance().getRoleSelectorAvailable()
                        : AssetManager.getInstance().getRoleSelectorUnavailable());

                if (hovering.isExtraRole()) {
                    drawImageWithScaling(g, toDraw, roleLocation);
                } else {
                    drawForRoleOnCard(g, toDraw, this.activeRolesRoom.getCardLocation(), hovering);
                }
            }
        }
    }

    void setTakingRoomInput (final boolean takingRoomInput)
    {
        this.isTakingRoomInput = takingRoomInput;
    }

    void setTakingRoleInput (final Room activeRolesRoom, final Collection<Role> availableRoles)
    {
        this.isTakingRoleInput = true;
        this.activeRolesRoom = activeRolesRoom;
        this.availableRoles = availableRoles;

        this.allRoleInputRoles = new HashSet<>();

        this.allRoleInputRoles.addAll(activeRolesRoom.getExtraRoles());
        this.allRoleInputRoles.addAll(activeRolesRoom.getCard().getRoles());
    }

    void clearTakingRoleInput ()
    {
        this.isTakingRoleInput = false;
        this.activeRolesRoom = null;
        this.availableRoles = null;
        this.allRoleInputRoles = null;
    }

    Room getRoomHoveringOver ()
    {
        return this.roomHoveringOver;
    }

    Role getRoleHoveringOver ()
    {
        return this.roleHoveringOver;
    }

    private void drawPlayer (final Graphics g, final Player player)
    {
        final Player.Color color = player.getColor();
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
        final Location scaled = scaleLocation(location);
        graphics.drawImage(image, scaled.getX(), scaled.getY(), scaled.getWidth(), scaled.getHeight(), null);
    }

    private boolean runBoundsCheck (final int x, final int y, final Location boundary)
    {
        return (x >= boundary.getX()
                && y >= boundary.getY()
                && x <= (boundary.getX() + boundary.getWidth())
                && y <= (boundary.getY() + boundary.getHeight()));
    }

    private Location scaleLocation (final Location location)
    {
        final double xScale = (double) this.width / SCALE_WIDTH;
        final double yScale = (double) this.height / SCALE_HEIGHT;

        final int newX = (int) (location.getX() * xScale);
        final int newY = (int) (location.getY() * yScale);

        final int newWidth = (int) (location.getWidth() * xScale);
        final int newHeight = (int) (location.getHeight() * yScale);

        return new Location(newX, newY, newWidth, newHeight);
    }
}
