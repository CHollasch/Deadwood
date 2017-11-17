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
    private Deadwood deadwood;
    private Collection<Player> players;
    private PlayerTurn currentPlayer;
    private Board gameBoard;

    private int daysLeft;

    private boolean playerUsedMove;
    private boolean playerUsedTurn;

    public Game (final Deadwood deadwood)
    {
        this.deadwood = deadwood;

        this.players = new HashSet<>();
        this.currentPlayer = null;
        this.gameBoard = null;

        this.playerUsedMove = false;
        this.playerUsedTurn = false;
    }

    public void initializeGame (final Board gameBoard, final int playerCount)
    {
        this.gameBoard = gameBoard;
        this.daysLeft = 4;

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

        for (int i = 0; i < playerCount; ++i) {
            final Player.Color color = Player.Color.values()[i];
            this.players.add(new Player(color));
        }

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

        wrapDay(true);
    }

    public Collection<Actionable> getCurrentPlayerPossibleActions ()
    {
        final HashSet<Actionable> actionables = new HashSet<>();
        actionables.add(Actionable.END_TURN);

        if (this.playerUsedTurn) {
            return actionables;
        }

        if (!this.playerUsedMove) {
            actionables.add(Actionable.MOVE);
        }

        final Player player = getCurrentPlayer().getPlayer();
        final Room room = player.getCurrentRoom();

        final int playerRank = player.getRank();
        if (AssetManager.getInstance().getUpgradeRoom().equals(room)
                && AssetManager.getCreditUpgradeCost(playerRank + 1) > 0) {
            actionables.add(Actionable.UPGRADE);
            return actionables;
        }

        if (room.isSceneFinished()) {
            return actionables;
        }

        if (!AssetManager.getInstance().getTrailerRoom().equals(room)) {
            if (player.getActiveRole() != null) {
                actionables.add(Actionable.ACT);

                final Card card = room.getCard();

                if (card != null && player.getPracticeChips() + 1 < card.getCardBudget()) {
                    actionables.add(Actionable.REHEARSE);
                }
            } else {
                final Collection<Role> activeRoles = getActableRolesByPlayer(player);

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

        for (final Player otherPlayer : getPlayers()) {
            if (otherPlayer.equals(player) || otherPlayer.getActiveRole() == null) {
                continue;
            }

            activeRoles.remove(otherPlayer.getActiveRole());
        }

        return activeRoles;
    }

    public void currentPlayerAct ()
    {
        final Player player = getCurrentPlayer().getPlayer();
        final Role role = player.getActiveRole();

        final int practiceChips = player.getPracticeChips();
        final int diceRoll = rollDice();

        final boolean wasSuccessful = player.getCurrentRoom().getCard().getCardBudget() <= diceRoll + practiceChips;

        final int cashReward;
        final int creditReward;

        if (role.isExtraRole()) {
            creditReward = wasSuccessful ? 1 : 0;
            cashReward = 1;
        } else {
            creditReward = wasSuccessful ? 2 : 0;
            cashReward = 0;
        }

        this.playerUsedTurn = true;

        if (wasSuccessful) {
            player.getCurrentRoom().setCurrentShotCounter(player.getCurrentRoom().getCurrentShotCounter() + 1);
        }

        player.setCreditCount(player.getCreditCount() + creditReward);
        player.setDollarCount(player.getDollarCount() + cashReward);

        this.gameBoard.playerActed(player, wasSuccessful, diceRoll);

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

        if (useCredits) {
            player.setCreditCount(player.getCreditCount() - AssetManager.getCreditUpgradeCost(rank));
        } else {
            player.setDollarCount(player.getDollarCount() - AssetManager.getDollarUpgradeCost(rank));
        }

        this.playerUsedTurn = true;
        this.gameBoard.playerUpgraded(player, useCredits, rank);
    }

    public void wrapScene (final Room room)
    {
        final Card roomCard = room.getCard();

        roomCard.setVisible(false);
        room.setSceneFinished(true);

        final BiMap<Player, Role> playersOnCardRoles = HashBiMap.create();
        final Map<Player, Role> playersOnExtraRoles = new HashMap<>();

        for (final Player player : getPlayers()) {
            final Room currentPlayerRoom = player.getCurrentRoom();

            if (!currentPlayerRoom.equals(room)) {
                continue;
            }

            final Role role = player.getActiveRole();

            if (role == null) {
                continue;
            }

            player.setActiveRole(null);

            if (roomCard.getRoles().contains(role)) {
                playersOnCardRoles.put(player, role);
            } else {
                playersOnExtraRoles.put(player, role);
            }
        }

        final int cardBudget = roomCard.getCardBudget();
        final int[] diceRolls = new int[cardBudget];

        for (int i = 0; i < cardBudget; ++i) {
            diceRolls[i] = rollDice();
        }

        Arrays.sort(diceRolls);

        final Map<Player, Collection<Integer>> playerPayouts = new HashMap<>();
        final Map<Player, Integer> offCardPayouts = new HashMap<>();

        if (playersOnCardRoles.size() > 0) {
            final Role[] rolePriority = new Role[playersOnCardRoles.size()];
            int idx = 0;

            for (final Role role : playersOnCardRoles.values()) {
                rolePriority[idx++] = role;
            }

            Arrays.sort(rolePriority, (o1, o2) -> o2.getMinimumRank() - o1.getMinimumRank());

            for (final Player onCard : playersOnCardRoles.keySet()) {
                playerPayouts.put(onCard, new ArrayList<>());
            }

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

        for (final Player mainRolePlayer : playerPayouts.keySet()) {
            final Collection<Integer> sumPaid = playerPayouts.get(mainRolePlayer);
            final int sumOfDiceRolls = sumPaid.stream().mapToInt(Integer::intValue).sum();

            mainRolePlayer.setDollarCount(mainRolePlayer.getDollarCount() + sumOfDiceRolls);
        }

        for (final Player extraRolePlayer : playersOnExtraRoles.keySet()) {
            final Role extraRole = playersOnExtraRoles.get(extraRolePlayer);
            final int roleRank = extraRole.getMinimumRank();

            extraRolePlayer.setDollarCount(extraRolePlayer.getDollarCount() + roleRank);
            offCardPayouts.put(extraRolePlayer, roleRank);
        }

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

    private int rollDice ()
    {
        return (int) (Math.random() * 6) + 1;
    }

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
