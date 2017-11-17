package edu.wwu.cs.deadwood;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import edu.wwu.cs.deadwood.assets.AssetManager;
import edu.wwu.cs.deadwood.assets.Card;
import edu.wwu.cs.deadwood.assets.Role;
import edu.wwu.cs.deadwood.assets.Room;
import edu.wwu.cs.deadwood.board.Board;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Connor Hollasch
 * @since October 31, 1:40 PM
 */
public class Game
{
    //==================================================================================================================
    // Local variables.
    //==================================================================================================================

    private Deadwood deadwood;
    private Collection<Player> players;
    private PlayerTurn currentPlayer;
    private Board gameBoard;

    private int daysLeft;

    private boolean playerUsedMove;
    private boolean playerUsedTurn;

    //==================================================================================================================
    // Constructors.
    //==================================================================================================================

    public Game (final Deadwood deadwood)
    {
        this.deadwood = deadwood;

        this.players = new HashSet<>();
        this.currentPlayer = null;
        this.gameBoard = null;

        this.playerUsedMove = false;
        this.playerUsedTurn = false;
    }

    //==================================================================================================================
    // Game setup and accessor logic.
    //==================================================================================================================

    public void initializeGame (final Board gameBoard, final int playerCount)
    {
        this.gameBoard = gameBoard;
        this.daysLeft = 4;

        // Create player list with player colors in ordinal order.
        for (int i = 0; i < playerCount; ++i) {
            final Player.Color color = Player.Color.values()[i];
            this.players.add(new Player(color));
        }

        // Apply special game starting properties based on player count.
        switch (playerCount) {
            case 2:
            case 3:
                this.daysLeft = 3;
                break;
            case 5:
                this.players.forEach(p -> p.setCreditCount(2));
                break;
            case 6:
                this.players.forEach(p -> p.setCreditCount(4));
                break;
            case 7:
            case 8:
                this.players.forEach(p -> p.setRank(2));
                break;
        }

        // Shuffle the players to get a random player order.
        final ArrayList<Player> randomPlayerOrder = new ArrayList<>(this.players);
        Collections.shuffle(randomPlayerOrder);

        PlayerTurn previous = null;
        for (final Player player : randomPlayerOrder) {
            final PlayerTurn turn = new PlayerTurn(player);

            if (previous != null) {
                previous.setNext(turn);
            }

            if (getCurrentPlayer() == null) {
                setCurrentPlayer(turn);
            }

            previous = turn;
        }

        if (previous != null) {
            previous.setNext(getCurrentPlayer());
        }

        // Wrap the day and mark wrap as initialization.
        // Wrapping the day will setup the cards and player location states.
        wrapDay(true);
    }

    public Collection<Actionable> getCurrentPlayerPossibleActions ()
    {
        final HashSet<Actionable> actionables = new HashSet<>();

        // Players can always end their turn.
        actionables.add(Actionable.END_TURN);

        // If the player used their turn, they can only end their turn.
        if (this.playerUsedTurn) {
            return actionables;
        }

        final Player player = getCurrentPlayer().getPlayer();
        final Room room = player.getCurrentRoom();

        // If the player hasn't already moved and they are not on a role, they can move.
        if (!this.playerUsedMove && player.getActiveRole() == null) {
            actionables.add(Actionable.MOVE);
        }

        // If the player is in the office and they aren't the highest rank, they can upgrade.
        final int playerRank = player.getRank();
        if (AssetManager.getInstance().getUpgradeRoom().equals(room)
                && AssetManager.getCreditUpgradeCost(playerRank + 1) > 0) {
            actionables.add(Actionable.UPGRADE);
            return actionables;
        }

        // If the room the player is in is wrapped, nothing else they can do.
        if (room.isSceneFinished()) {
            return actionables;
        }

        if (!AssetManager.getInstance().getTrailerRoom().equals(room)) {
            // If the player has an active role, they can act.
            if (player.getActiveRole() != null) {
                actionables.add(Actionable.ACT);

                final Card card = room.getCard();

                // If they have room for practice chips, they can rehearse.
                if (card != null && player.getPracticeChips() + 1 < card.getCardBudget()) {
                    actionables.add(Actionable.REHEARSE);
                }
            } else {
                final Collection<Role> activeRoles = getActableRolesByPlayer(player);

                // If there is a role available based on the players rank, they can take a role.
                if (activeRoles.size() > 0) {
                    actionables.add(Actionable.TAKE_ROLE);
                }
            }
        }

        return actionables;
    }

    public Collection<Role> getActableRolesByPlayer (final Player player)
    {
        final Room room = player.getCurrentRoom();
        final Card card = room.getCard();

        final Collection<Role> activeRoles = new HashSet<>();

        // Look for all roles on and off the card for the player.
        // Filter by the roles minimum rank requirement.
        if (card != null) {
            activeRoles.addAll(room.getExtraRoles()
                    .stream()
                    .filter(role -> player.getRank() >= role.getMinimumRank())
                    .collect(Collectors.toList()));

            activeRoles.addAll(card.getRoles()
                    .stream()
                    .filter(role -> player.getRank() >= role.getMinimumRank())
                    .collect(Collectors.toList()));
        }

        // Remove any roles that have been taken by other players.
        for (final Player otherPlayer : getPlayers()) {
            if (otherPlayer.equals(player) || otherPlayer.getActiveRole() == null) {
                continue;
            }

            activeRoles.remove(otherPlayer.getActiveRole());
        }

        return activeRoles;
    }

    //==================================================================================================================
    // Game logic performers.
    //==================================================================================================================

    public void currentPlayerAct ()
    {
        final Player player = getCurrentPlayer().getPlayer();
        final Role role = player.getActiveRole();

        final int practiceChips = player.getPracticeChips();
        final int diceRoll = rollDice();

        // Successful act determinant.
        final boolean wasSuccessful = player.getCurrentRoom().getCard().getCardBudget() <= diceRoll + practiceChips;

        final int cashReward;
        final int creditReward;

        // Calculate rewards based on role type.
        if (role.isExtraRole()) {
            creditReward = wasSuccessful ? 1 : 0;
            cashReward = 1;
        } else {
            creditReward = wasSuccessful ? 2 : 0;
            cashReward = 0;
        }

        this.playerUsedTurn = true;

        // Increment shot counter if success.
        if (wasSuccessful) {
            player.getCurrentRoom().setCurrentShotCounter(player.getCurrentRoom().getCurrentShotCounter() + 1);
        }

        player.setCreditCount(player.getCreditCount() + creditReward);
        player.setDollarCount(player.getDollarCount() + cashReward);

        // Invoke game board listeners.
        this.gameBoard.playerActed(player, wasSuccessful, diceRoll);

        // Check to see if we need to wrap the scene.
        if (player.getCurrentRoom().getCurrentShotCounter() >= player.getCurrentRoom().getTotalShotMarkers()) {
            wrapScene(player.getCurrentRoom());
        }
    }

    public void currentPlayerEndTurn ()
    {
        this.currentPlayer = this.currentPlayer.getNext();

        this.playerUsedMove = false;
        this.playerUsedTurn = false;
    }

    public void currentPlayerMove (final Room room)
    {
        final Player player = getCurrentPlayer().getPlayer();

        player.setCurrentRoom(room);
        this.playerUsedMove = true;

        this.gameBoard.playerMoved(player, room);
    }

    public void currentPlayerRehearse ()
    {
        final Player player = getCurrentPlayer().getPlayer();

        player.setPracticeChips(player.getPracticeChips() + 1);
        this.playerUsedTurn = true;

        this.gameBoard.playerRehearsed(player);
    }

    public void currentPlayerTakeRole (final Role role)
    {
        final Player player = getCurrentPlayer().getPlayer();

        player.setActiveRole(role);
        this.playerUsedTurn = true;

        this.gameBoard.playerTookRole(getCurrentPlayer().getPlayer(), role);
    }

    public void currentPlayerUpgrade (final boolean useCredits, final int rank)
    {
        final Player player = getCurrentPlayer().getPlayer();

        player.setRank(rank);

        // Determine players new credit/dollar combos based on upgrade type.
        if (useCredits) {
            player.setCreditCount(player.getCreditCount() - AssetManager.getCreditUpgradeCost(rank));
        } else {
            player.setDollarCount(player.getDollarCount() - AssetManager.getDollarUpgradeCost(rank));
        }

        this.playerUsedTurn = true;
        this.gameBoard.playerUpgraded(player, useCredits, rank);
    }

    //==================================================================================================================
    // Scene and day wrapping.
    //==================================================================================================================

    public void wrapScene (final Room room)
    {
        final Card roomCard = room.getCard();

        // Mark the card and room as finished.
        roomCard.setVisible(false);
        room.setSceneFinished(true);

        // Store players and their roles in the room.
        // We'll use a BiMap to lookup by role on main roles.
        final BiMap<Player, Role> playersOnCardRoles = HashBiMap.create();
        final Map<Player, Role> playersOnExtraRoles = new HashMap<>();

        // Load players and their roles into the maps.
        for (final Player player : getPlayers()) {
            final Room currentPlayerRoom = player.getCurrentRoom();

            if (!currentPlayerRoom.equals(room)) {
                continue;
            }

            final Role role = player.getActiveRole();

            if (role == null) {
                continue;
            }

            // Mark the player as having no role as the scene is wrapped.
            player.setActiveRole(null);

            if (roomCard.getRoles().contains(role)) {
                playersOnCardRoles.put(player, role);
            } else {
                playersOnExtraRoles.put(player, role);
            }
        }

        // Load up dice rolls based on the card budget.
        final int cardBudget = roomCard.getCardBudget();
        final int[] diceRolls = new int[cardBudget];

        for (int i = 0; i < cardBudget; ++i) {
            diceRolls[i] = rollDice();
        }

        // Sort the dice rolls ascending.
        Arrays.sort(diceRolls);

        // Store payouts.
        // On card payouts, we need to store all of the roles a player earned.
        final Map<Player, Collection<Integer>> playerPayouts = new HashMap<>();
        final Map<Player, Integer> offCardPayouts = new HashMap<>();

        // Only worry about on card roles if there are players on main roles.
        if (playersOnCardRoles.size() > 0) {
            final Role[] rolePriority = new Role[playersOnCardRoles.size()];
            int idx = 0;

            for (final Role role : playersOnCardRoles.values()) {
                rolePriority[idx++] = role;
            }

            // Sort players based on the roles they are acting.
            Arrays.sort(rolePriority, (o1, o2) -> o2.getMinimumRank() - o1.getMinimumRank());

            for (final Player onCard : playersOnCardRoles.keySet()) {
                playerPayouts.put(onCard, new ArrayList<>());
            }

            // Iterate through all the roles and assign players dice rolls.
            int roleIndex = 0;
            for (int i = diceRolls.length - 1; i >= 0; --i) {
                final int roll = diceRolls[i];
                final Player payingTo = playersOnCardRoles.inverse().get(rolePriority[roleIndex]);

                if (++roleIndex >= rolePriority.length) {
                    roleIndex = 0;
                }

                playerPayouts.get(payingTo).add(roll);
            }
        }

        // Pay players that have on card roles.
        for (final Player mainRolePlayer : playerPayouts.keySet()) {
            final Collection<Integer> sumPaid = playerPayouts.get(mainRolePlayer);

            // Pay with sum of dice rolls as dollar amount.
            final int sumOfDiceRolls = sumPaid.stream().mapToInt(Integer::intValue).sum();

            mainRolePlayer.setDollarCount(mainRolePlayer.getDollarCount() + sumOfDiceRolls);
        }

        // Pay players that have off card roles.
        for (final Player extraRolePlayer : playersOnExtraRoles.keySet()) {
            final Role extraRole = playersOnExtraRoles.get(extraRolePlayer);
            final int roleRank = extraRole.getMinimumRank();

            extraRolePlayer.setDollarCount(extraRolePlayer.getDollarCount() + roleRank);
            offCardPayouts.put(extraRolePlayer, roleRank);
        }

        // Get the total room count and look at the number of wrapped rooms.
        // Trailer and office are automatically considered wrapped.
        final int totalRooms = AssetManager.getInstance().getRoomMap().size();
        int wrappedRooms = 0;

        for (final Room state : AssetManager.getInstance().getRoomMap().values()) {
            if (state.equals(AssetManager.getInstance().getTrailerRoom())
                    || state.equals(AssetManager.getInstance().getUpgradeRoom())) {
                wrappedRooms++;
            } else {
                wrappedRooms += state.isSceneFinished() ? 1 : 0;
            }
        }

        // Update the game board of the scene wrap.
        this.gameBoard.sceneWrapped(room, playerPayouts, offCardPayouts);

        // Check to see if we need to end the day now that the last scene has been wrapped up.
        if (wrappedRooms + 1 >= totalRooms) {
            wrapDay(false);
        }
    }

    public void wrapDay (final boolean init)
    {
        // Reset states for all players and rooms to default.
        this.players.forEach(p -> p.setCurrentRoom(AssetManager.getInstance().getTrailerRoom()));
        AssetManager.getInstance().getRoomMap().values().forEach(r -> {
            r.setCurrentShotCounter(0);
            r.setCard(null);
        });

        // Get a random selection of cards and shuffle them.
        final ArrayList<Card> cards = new ArrayList<>(AssetManager.getInstance().getCardMap().values());
        Collections.shuffle(cards);
        final Iterator<Card> randomCards = cards.iterator();

        for (final Card card : cards) {
            card.setVisible(false);
        }

        // We don't have to worry about our iterator running out of data as we always have more cards than rooms.
        for (final Room room : AssetManager.getInstance().getRoomMap().values()) {
            room.setCard(randomCards.next());
            room.setSceneFinished(false);
        }

        if (!init) {
            this.gameBoard.dayWrapped();

            if (--this.daysLeft <= 0) {
                this.gameBoard.endGame();
            }
        }
    }

    //==================================================================================================================
    // Getters.
    //==================================================================================================================

    public Deadwood getDeadwood ()
    {
        return this.deadwood;
    }

    public Collection<Player> getPlayers ()
    {
        return this.players;
    }

    public PlayerTurn getCurrentPlayer ()
    {
        return this.currentPlayer;
    }

    public void setCurrentPlayer (final PlayerTurn currentPlayer)
    {
        this.currentPlayer = currentPlayer;
    }

    public int getDaysLeft ()
    {
        return this.daysLeft;
    }

    //==================================================================================================================
    // Private utility API.
    //==================================================================================================================

    private int rollDice ()
    {
        return (int) (Math.random() * 6) + 1;
    }

    //==================================================================================================================
    // Player turn nested class.
    //==================================================================================================================

    public static class PlayerTurn
    {
        private final Player    player;
        private PlayerTurn      next;

        public PlayerTurn (final Player player)
        {
            this.player = player;
        }

        public Player getPlayer ()
        {
            return this.player;
        }

        public PlayerTurn getNext ()
        {
            return this.next;
        }

        public void setNext (final PlayerTurn next)
        {
            this.next = next;
        }
    }
}
