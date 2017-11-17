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

import edu.wwu.cs.deadwood.Actionable;
import edu.wwu.cs.deadwood.Game;
import edu.wwu.cs.deadwood.Player;
import edu.wwu.cs.deadwood.assets.AssetManager;
import edu.wwu.cs.deadwood.assets.Card;
import edu.wwu.cs.deadwood.assets.Role;
import edu.wwu.cs.deadwood.assets.Room;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Connor Hollasch
 * @since October 31, 1:54 PM
 */
public class CommandLineBoard implements Board
{
    private Scanner consoleScanner;
    private Game game;

    private Map<String, Command> commandMap;

    public CommandLineBoard (final Game game)
    {
        this.game = game;
        this.commandMap = new HashMap<>();

        this.commandMap.put("who", args -> {
            final StringBuilder who = new StringBuilder();

            final Player current = this.game.getCurrentPlayer().getPlayer();
            who.append(current.getColor().name().toLowerCase() + " ");
            who.append("($" + current.getDollarCount() + ", " + current.getCreditCount() + "cr, rank " + current.getRank() + ")");

            if (current.getActiveRole() != null) {
                final Role active = current.getActiveRole();
                who.append(" working " + active.getName() + ", \"" + active.getLine() + "\"");
            }

            System.out.println(who.toString());
        });

        this.commandMap.put("where", args -> {
            final StringBuilder where = new StringBuilder();

            final Player current = this.game.getCurrentPlayer().getPlayer();
            final Room room = current.getCurrentRoom();

            where.append("in " + room.getName());

            if (!(AssetManager.getInstance().getTrailerRoom().equals(room)
                    || AssetManager.getInstance().getUpgradeRoom().equals(room))) {
                if (room.isSceneFinished()) {
                    where.append(" wrapped");
                } else {
                    where.append(" shooting ");
                    where.append(room.getCard().getName());
                    where.append(" scene ");
                    where.append(room.getCard().getSceneNumber());
                }
            }

            System.out.println(where);
        });

        this.commandMap.put("moves", args -> {
            final Collection<Actionable> actions = this.game.getCurrentPlayerPossibleActions();
            System.out.println("Possible actions: " + actions);
        });

        this.commandMap.put("move", args -> {
            if (!this.game.getCurrentPlayerPossibleActions().contains(Actionable.MOVE)) {
                System.out.println("You cannot move right now.");
                return;
            }

            if (args.length == 0) {
                System.out.println("No room entered.");
                return;
            }

            final String roomName = Arrays.toString(args).replaceAll("[\\[\\],]", "");
            final Room room = AssetManager.getInstance().getRoomMap().get(roomName.toLowerCase().replace("_", " "));
            final Room current = this.game.getCurrentPlayer().getPlayer().getCurrentRoom();

            if (room == null) {
                System.out.println("No such room. Here are a list of rooms: "
                        + current.getAdjacentRooms().stream().map(Room::getName).collect(Collectors.toList()));
                return;
            }

            if (!current.isAdjacentTo(room)) {
                System.out.println("You are not next to this room.");
                return;
            }

            this.game.currentPlayerMove(room);
        });

        this.commandMap.put("work", args -> {
            if (!this.game.getCurrentPlayerPossibleActions().contains(Actionable.TAKE_ROLE)) {
                System.out.println("You cannot take a role right now.");
                return;
            }

            if (args.length == 0) {
                System.out.println("No role entered.");
                return;
            }

            final String roleName = Arrays.toString(args).replaceAll("[\\[\\],]", "");
            final Collection<Role> actable = this.game.getActableRolesByPlayer(this.game.getCurrentPlayer().getPlayer());

            for (final Role role : actable) {
                if (!role.getName().equalsIgnoreCase(roleName)) {
                    continue;
                }

                this.game.currentPlayerTakeRole(role);
                return;
            }

            System.out.println("No such role on card: "
                    + roleName + ", available roles: "
                    + actable.stream().map(Role::getName).collect(Collectors.toList()));
        });

        this.commandMap.put("upgrade", args -> {
            if (!this.game.getCurrentPlayerPossibleActions().contains(Actionable.UPGRADE)) {
                System.out.println("You cannot upgrade right now.");
                return;
            }

            if (args.length < 2) {
                System.out.println("Format: upgrade [$/cr] (level)");
                return;
            }

            final String method = args[0];

            Integer rank;
            try {
                rank = Integer.parseInt(args[1]);
            } catch (final NumberFormatException e) {
                System.out.println(args[1] + " is not a valid rank number.");
                return;
            }

            final Player player = this.game.getCurrentPlayer().getPlayer();
            final int currentRank = player.getRank();

            if (rank > 6 || rank <= 1 || currentRank >= rank) {
                System.out.println("You must enter a rank between " + (currentRank + 1) + " and 6");
                return;
            }

            if (method.equals("$")) {
                final int dollars = player.getDollarCount();
                final int dollarsNeeded = AssetManager.getDollarUpgradeCost(rank);

                if (dollarsNeeded > dollars) {
                    System.out.println("You cannot afford this upgrade.");
                    return;
                }

                this.game.currentPlayerUpgrade(false, rank);
            } else if (method.equalsIgnoreCase("cr")) {
                final int credits = player.getCreditCount();
                final int creditsNeeded = AssetManager.getCreditUpgradeCost(rank);

                if (creditsNeeded > credits) {
                    System.out.println("You cannot afford this upgrade.");
                    return;
                }

                this.game.currentPlayerUpgrade(true, rank);
            } else {
                System.out.println("No such upgrade method, must be either $ or cr");
                return;
            }
        });

        this.commandMap.put("rehearse", args -> {
            if (!this.game.getCurrentPlayerPossibleActions().contains(Actionable.REHEARSE)) {
                System.out.println("You cannot rehearse right now.");
                return;
            }

            this.game.currentPlayerRehearse();
        });

        this.commandMap.put("act", args -> {
            if (!this.game.getCurrentPlayerPossibleActions().contains(Actionable.ACT)) {
                System.out.println("You cannot act right now.");
                return;
            }

            this.game.currentPlayerAct();
        });

        this.commandMap.put("end", args -> {
            this.game.currentPlayerEndTurn();
        });
    }

    public void setupConsoleListener ()
    {
        this.consoleScanner = new Scanner(System.in);

        while (true) {
            System.out.print("> ");
            final String line = this.consoleScanner.nextLine();

            if (line.equalsIgnoreCase("quit")) {
                break;
            }

            processInput(line);
        }

        closeInputStreams();
    }

    private void processInput (final String command)
    {
        if (command.trim().equals("")) {
            return;
        }

        final String[] arguments = command.split("[ ]");

        if (arguments.length == 0) {
            return;
        }

        final String cmd = arguments[0];

        final String[] args = new String[arguments.length - 1];
        System.arraycopy(arguments, 1, args, 0, args.length);

        if (this.commandMap.containsKey(cmd.toLowerCase())) {
            this.commandMap.get(cmd.toLowerCase()).onCommand(args);
        } else {
            System.out.println("No such command \"" + cmd + "\"");
        }
    }

    public void closeInputStreams ()
    {
        this.consoleScanner.close();
    }

    @Override
    public void refreshBoard ()
    {
        return;
    }

    @Override
    public void sceneWrapped (final Room room, final Map<Player, Collection<Integer>> onCardPayouts, final Map<Player, Integer> offCardPayouts)
    {
        final Card card = room.getCard();
        System.out.println("That's a wrap for " + card.getName() + " in " + room.getName());
        System.out.println("The payouts are as follows: ");

        for (final Player player : onCardPayouts.keySet()) {
            final Collection<Integer> payouts = onCardPayouts.get(player);
            final int sum = payouts.stream().mapToInt(Integer::intValue).sum();

            System.out.println(" - " + player.getColor().name().toLowerCase() + " received the rolls " + payouts + " and gets $" + sum);
        }

        for (final Player player : offCardPayouts.keySet()) {
            final int paid = offCardPayouts.get(player);
            System.out.println(" - " + player.getColor().name().toLowerCase() + " receives $" + paid);
        }
    }

    @Override
    public void dayWrapped ()
    {
        System.out.println("That's it for the day... " + this.game.getDaysLeft() + " days are left.");
    }

    @Override
    public void endGame ()
    {
        System.out.println("That's the game!");

        final ArrayList<Player> winners = new ArrayList<>(this.game.getPlayers());
        Collections.sort(winners, (o1, o2) -> {
            final int s1 = o1.getScore();
            final int s2 = o2.getScore();

            return s2 - s1;
        });

        System.out.println("Here are the player rankings:");
        int idx = 1;
        for (final Player player : winners) {
            System.out.println(" " + idx + ". " + player.getColor().name().toLowerCase() + " scored " + player.getScore());
        }
    }

    @Override
    public void playerActed (final Player player, final boolean successful, final int diceRoll)
    {
        System.out.println(player.getColor().name().toLowerCase() + " acted (rolled a "
                + diceRoll + ") and was " + (successful ? "" : "un") + "successful.");
    }

    @Override
    public void playerEndedTurn (final Player old, final Player newPlayer)
    {
        System.out.println(newPlayer.getColor().name().toLowerCase() + ", it's your turn!");
    }

    @Override
    public void playerMoved (final Player player, final Room newRoom)
    {
        System.out.println(player.getColor().name().toLowerCase() + " moved to " + newRoom.getName());
    }

    @Override
    public void playerRehearsed (final Player player)
    {
        System.out.println(player.getColor().name().toLowerCase() + " rehearsed and has "
                + player.getPracticeChips() + " practice chips now.");
    }

    @Override
    public void playerTookRole (final Player player, final Role role)
    {
        System.out.println(player.getColor().name().toLowerCase() + " took role " + role.getName());
    }

    @Override
    public void playerUpgraded (final Player player, final boolean usedCredits, final int rankUpgradingTo)
    {
        System.out.println(player.getColor().name().toLowerCase() + " upgraded to " + rankUpgradingTo + " with " + (usedCredits ? "credits" : "dollars"));
    }

    private interface Command
    {
        void onCommand (final String[] args);
    }
}
