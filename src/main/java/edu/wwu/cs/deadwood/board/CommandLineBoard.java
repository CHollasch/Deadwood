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
    //==================================================================================================================
    // Local variables.
    //==================================================================================================================

    private Scanner consoleScanner;
    private Game game;

    private Map<String, Command> commandMap;

    //==================================================================================================================
    // Constructors.
    //==================================================================================================================

    public CommandLineBoard (final Game game)
    {
        this.game = game;
        this.commandMap = new HashMap<>();

        //==============================================================================================================
        // Who command.
        //==============================================================================================================

        this.commandMap.put("who", args -> {
            final StringBuilder who = new StringBuilder();

            final Player current = this.game.getCurrentPlayer().getPlayer();
            who.append(current.getColor().name().toLowerCase()).append(" ");

            who.append("($").append(current.getDollarCount())
                    .append(", ")
                    .append(current.getCreditCount())
                    .append("cr, rank ")
                    .append(current.getRank())
                    .append(")");

            if (current.getActiveRole() != null) {
                final Role active = current.getActiveRole();
                who.append(" working ")
                        .append(active.getName())
                        .append(", \"")
                        .append(active.getLine())
                        .append("\"");
            }

            System.out.println(who.toString());
        });

        //==============================================================================================================
        // Where command.
        //==============================================================================================================

        this.commandMap.put("where", args -> {
            final StringBuilder where = new StringBuilder();

            final Player current = this.game.getCurrentPlayer().getPlayer();
            final Room room = current.getCurrentRoom();

            where.append("in ")
                    .append(room.getName());

            // Format where message based on the room the player is in.
            // Trailer and office cannot be wrapped and thus are disregarded in this portion.
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

        //==============================================================================================================
        // Moves command.
        //==============================================================================================================

        this.commandMap.put("moves", args -> {
            final Collection<Actionable> actions = this.game.getCurrentPlayerPossibleActions();
            System.out.println("Possible actions: " + actions);
        });

        //==============================================================================================================
        // Move command.
        //==============================================================================================================

        this.commandMap.put("move", args -> {
            // Check if move is a valid command.
            if (!this.game.getCurrentPlayerPossibleActions().contains(Actionable.MOVE)) {
                System.out.println("You cannot move right now.");
                return;
            }

            if (args.length == 0) {
                System.out.println("No room entered.");
                return;
            }

            // Format command line input arguments and grab room name.
            final String roomName = Arrays.toString(args).replaceAll("[\\[\\],]", "");
            final Room room = AssetManager.getInstance().getRoomMap().get(roomName.toLowerCase().replace("_", " "));
            final Room current = this.game.getCurrentPlayer().getPlayer().getCurrentRoom();

            // Verify room exists.
            if (room == null) {
                System.out.println("No such room. Here are a list of rooms: "
                        + current.getAdjacentRooms().stream().map(Room::getName).collect(Collectors.toList()));
                return;
            }

            // Check adjacency.
            if (!current.isAdjacentTo(room)) {
                System.out.println("You are not next to this room.");
                return;
            }

            // Invoke game move.
            this.game.currentPlayerMove(room);
        });

        //==============================================================================================================
        // Work command.
        //==============================================================================================================

        this.commandMap.put("work", args -> {
            // Check if work is a valid command.
            if (!this.game.getCurrentPlayerPossibleActions().contains(Actionable.TAKE_ROLE)) {
                System.out.println("You cannot take a role right now.");
                return;
            }

            // Validate input exists.
            if (args.length == 0) {
                System.out.println("No role entered.");
                return;
            }

            // Format arguments into role name.
            final String roleName = Arrays.toString(args).replaceAll("[\\[\\],]", "");
            final Collection<Role> actable = this.game.getActableRolesByPlayer(
                    this.game.getCurrentPlayer().getPlayer());

            // Look for role based on a list of actable roles.
            for (final Role role : actable) {
                if (!role.getName().equalsIgnoreCase(roleName)) {
                    continue;
                }

                this.game.currentPlayerTakeRole(role);
                return;
            }

            // If no valid role was found.
            System.out.println("No such valid role or role is unavailable: "
                    + roleName + ", available roles: "
                    + actable.stream().map(Role::getName).collect(Collectors.toList()));
        });

        //==============================================================================================================
        // Upgrade command.
        //==============================================================================================================

        this.commandMap.put("upgrade", args -> {
            // Check if upgrade is a valid command.
            if (!this.game.getCurrentPlayerPossibleActions().contains(Actionable.UPGRADE)) {
                System.out.println("You cannot upgrade right now.");
                return;
            }

            // Validate argument count.
            if (args.length < 2) {
                System.out.println("Format: upgrade [$/cr] (level)");
                return;
            }

            // Parse payment method.
            final String method = args[0];

            // Load rank being upgraded to.
            Integer rank;
            try {
                rank = Integer.parseInt(args[1]);
            } catch (final NumberFormatException e) {
                System.out.println(args[1] + " is not a valid rank number.");
                return;
            }

            final Player player = this.game.getCurrentPlayer().getPlayer();
            final int currentRank = player.getRank();

            // Check rank against bounds.
            if (rank > 6 || rank <= 1 || currentRank >= rank) {
                System.out.println("You must enter a rank between " + (currentRank + 1) + " and 6");
                return;
            }

            // Process payment and upgrade based on method.
            if (method.equals("$")) {
                final int dollars = player.getDollarCount();
                final int dollarsNeeded = AssetManager.getDollarUpgradeCost(rank);

                if (dollarsNeeded > dollars) {
                    System.out.println("You cannot afford this upgrade.");
                    return;
                }

                // Fire game event to upgrade with dollars.
                this.game.currentPlayerUpgrade(false, rank);
            } else if (method.equalsIgnoreCase("cr")) {
                final int credits = player.getCreditCount();
                final int creditsNeeded = AssetManager.getCreditUpgradeCost(rank);

                if (creditsNeeded > credits) {
                    System.out.println("You cannot afford this upgrade.");
                    return;
                }

                // Fire game event to upgrade with credits.
                this.game.currentPlayerUpgrade(true, rank);
            } else {
                // If an invalid method was entered.
                System.out.println("No such upgrade method, must be either $ or cr");
            }
        });

        //==============================================================================================================
        // Rehearse command.
        //==============================================================================================================

        this.commandMap.put("rehearse", args -> {
            // Check if rehearse is a valid command.
            if (!this.game.getCurrentPlayerPossibleActions().contains(Actionable.REHEARSE)) {
                System.out.println("You cannot rehearse right now.");
                return;
            }

            this.game.currentPlayerRehearse();
        });

        //==============================================================================================================
        // Act command.
        //==============================================================================================================

        this.commandMap.put("act", args -> {
            // Check if act is a valid action.
            if (!this.game.getCurrentPlayerPossibleActions().contains(Actionable.ACT)) {
                System.out.println("You cannot act right now.");
                return;
            }

            this.game.currentPlayerAct();
        });

        //==============================================================================================================
        // End turn command.
        //==============================================================================================================

        this.commandMap.put("end", args -> this.game.currentPlayerEndTurn());
    }

    //==================================================================================================================
    // Console IO
    //==================================================================================================================

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

    private void closeInputStreams ()
    {
        this.consoleScanner.close();
    }

    //==================================================================================================================
    // Game interactions.
    //==================================================================================================================

    @Override
    public void sceneWrapped (
            final Room room,
            final Map<Player, Collection<Integer>> onCardPayouts,
            final Map<Player, Integer> offCardPayouts)
    {
        final Card card = room.getCard();
        System.out.println("That's a wrap for " + card.getName() + " in " + room.getName());
        System.out.println("The payouts are as follows: ");

        for (final Player player : onCardPayouts.keySet()) {
            final Collection<Integer> payouts = onCardPayouts.get(player);
            final int sum = payouts.stream().mapToInt(Integer::intValue).sum();

            System.out.println(" - " + player.getColor().name().toLowerCase() + " received the rolls "
                    + payouts + " and gets $" + sum);
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
        winners.sort((o1, o2) -> {
            final int s1 = o1.getScore();
            final int s2 = o2.getScore();

            return s2 - s1;
        });

        System.out.println("Here are the player rankings:");
        int idx = 1;
        for (final Player player : winners) {
            System.out.println(" " + idx + ". " + player.getColor().name().toLowerCase() + " scored "
                    + player.getScore());
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
        System.out.println(player.getColor().name().toLowerCase() + " upgraded to " + rankUpgradingTo
                + " with " + (usedCredits ? "credits" : "dollars"));
    }

    //==================================================================================================================
    // Command interface.
    //==================================================================================================================

    private interface Command
    {
        void onCommand (final String[] args);
    }
}
