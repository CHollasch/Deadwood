package edu.wwu.cs.deadwood.assets;

import edu.wwu.cs.deadwood.util.Pair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * @author Connor Hollasch
 * @since November 14, 10:03 AM
 */
public class AssetManager
{
    //==================================================================================================================
    // Rank upgrade costs
    //==================================================================================================================

    private static final Map<Integer, Pair<Integer, Integer>> upgradeCostTable = new HashMap<Integer, Pair<Integer, Integer>>() {
        {
            // Standardized map of upgrade costs.
            put(2, 4, 5);
            put(3, 10, 10);
            put(4, 18, 15);
            put(5, 28, 20);
            put(6, 40, 25);
        }

        private void put (final int rank, final int dollars, final int credits)
        {
            put(rank, new Pair<>(dollars, credits));
        }
    };

    public static int getDollarUpgradeCost (final int rank)
    {
        return AssetManager.upgradeCostTable.containsKey(rank) ? AssetManager.upgradeCostTable.get(rank).getFirst() : -1;
    }

    public static int getCreditUpgradeCost (final int rank)
    {
        return AssetManager.upgradeCostTable.containsKey(rank) ? AssetManager.upgradeCostTable.get(rank).getSecond() : -1;
    }

    //==================================================================================================================
    // Static context singleton
    //==================================================================================================================

    private static AssetManager instance;

    //==================================================================================================================
    // Local variables
    //==================================================================================================================

    private final File assetDirectory;
    private final DocumentBuilder documentBuilder;

    private final Map<String, Room> roomMap;
    private final Map<String, Card> cardMap;

    private Room trailerRoom;
    private Room upgradeRoom;

    //==================================================================================================================
    // Singleton constructor.
    //==================================================================================================================

    private AssetManager (final File assetDirectory) throws Exception
    {
        this.assetDirectory = assetDirectory;

        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        this.documentBuilder = factory.newDocumentBuilder();

        this.roomMap = new HashMap<>();
        this.cardMap = new HashMap<>();

        loadAssets();
    }

    //==================================================================================================================
    // Asset loading and creation.
    //==================================================================================================================

    private void loadAssets () throws IOException, SAXException
    {
        loadBoard();
        loadCards();
    }

    private void loadBoard () throws IOException, SAXException
    {
        final Document doc = this.documentBuilder.parse(new File(this.assetDirectory, "board.xml"));
        final Element root = doc.getDocumentElement();

        final Map<String, PartiallyLoadedRoom> partiallyLoadedRoomMap = new HashMap<>();

        // Extract child nodes from root.
        for (final Node node : getElementTypeNodes(root)) {
            final String nodeName = node.getNodeName();

            final PartiallyLoadedRoom room;
            // Load the room based on room type, set/trailer/office.
            if (nodeName.equals("set")) {
                room = loadStandardRoom(node);
            }
            else if (nodeName.equals("trailer")) {
                // Load trailer.
                room = loadSpecialRoom(node, Room.Type.TRAILER, "trailer");
                this.trailerRoom = room.room;
            }
            else {
                // Load office.
                room = loadSpecialRoom(node, Room.Type.CASTING_OFFICE, "office");
                this.upgradeRoom = room.room;
            }

            // Add to partially loaded map as we have to link adjacent room objects.
            partiallyLoadedRoomMap.put(room.room.getName(), room);
        }

        // Link all adjacent room POJOs.
        for (final String roomName : partiallyLoadedRoomMap.keySet()) {
            final PartiallyLoadedRoom value = partiallyLoadedRoomMap.get(roomName);
            final Collection<String> adjacentList = value.neighbors;

            for (final String adjacent : adjacentList) {
                final Room room = partiallyLoadedRoomMap.get(adjacent).room;
                value.room.getAdjacentRooms().add(room);
            }

            this.roomMap.put(roomName.toLowerCase(), value.room);
        }
    }

    private PartiallyLoadedRoom loadStandardRoom (final Node node)
    {
        // Extract room data and create a room object.
        final String name = node.getAttributes().getNamedItem("name").getNodeValue();

        final Node neighbors = extractFirstOccurance(node, "neighbors");
        final Node takes = extractFirstOccurance(node, "takes");
        final Node parts = extractFirstOccurance(node, "parts");

        final Room room = new Room(Room.Type.STAGE, name, getElementTypeNodes(takes).size());
        final Collection<String> neighborNames = getNeighbors(neighbors);

        // Return a partially loaded room (no adjacent rooms actually linked).
        final PartiallyLoadedRoom plr = new PartiallyLoadedRoom(room, neighborNames);
        loadParts(room, parts);

        return plr;
    }

    private PartiallyLoadedRoom loadSpecialRoom (final Node node, final Room.Type type, final String name)
    {
        // Create special room (trailer / upgrade room).
        final Room room = new Room(type, name, 0);
        final Collection<String> neighborNames = getNeighbors(extractFirstOccurance(node, "neighbors"));

        return new PartiallyLoadedRoom(room, neighborNames);
    }

    private void loadParts (final Room room, final Node parts)
    {
        // Load all parts for a room based on the parts node in the board xml.
        for (final Node part : getElementTypeNodes(parts)) {
            final String name = part.getAttributes().getNamedItem("name").getNodeValue();
            final String level = part.getAttributes().getNamedItem("level").getNodeValue();
            final String line = extractFirstOccurance(part, "line").getTextContent();

            room.getExtraRoles().add(new Role(name, line, Integer.parseInt(level), true));
        }
    }

    private Collection<String> getNeighbors (final Node node)
    {
        // Get neighbors based on node values.
        final Collection<String> neighbors = new HashSet<>();

        for (final Node element : getElementTypeNodes(node)) {
            neighbors.add(element.getAttributes().getNamedItem("name").getNodeValue());
        }

        return neighbors;
    }

    private void loadCards () throws IOException, SAXException
    {
        final Document doc = this.documentBuilder.parse(new File(this.assetDirectory, "cards.xml"));
        final Element root = doc.getDocumentElement();

        final Collection<Node> cards = getElementTypeNodes(root);

        // Get all element nodes for cards.
        for (final Node cardNode : cards) {
            // Extract card attributes and create cards.
            final String name = cardNode.getAttributes().getNamedItem("name").getNodeValue();
            final int budget = Integer.parseInt(cardNode.getAttributes().getNamedItem("budget").getNodeValue());

            final Node sceneNode = extractFirstOccurance(cardNode, "scene");
            final int sceneNumber = Integer.parseInt(sceneNode.getAttributes().getNamedItem("number").getNodeValue());

            final Collection<Role> roles = new HashSet<>();

            // Load parts into the card objects.
            for (final Node potentialPart : getElementTypeNodes(cardNode)) {
                if (potentialPart.getNodeName().equals("part")) {
                    final String partName = potentialPart.getAttributes().getNamedItem("name").getNodeValue();
                    final int level = Integer.parseInt(potentialPart.getAttributes().getNamedItem("level").getNodeValue());
                    final String line = extractFirstOccurance(potentialPart, "line").getTextContent();
                    roles.add(new Role(partName, line, level, false));
                }
            }

            // Format the description in the XML tag.
            final String description = sceneNode.getTextContent().replace("\n", "").replaceAll("[ ]{2,}", " ").trim();

            // Create card and insert into card map.
            final Card card = new Card(name, description, sceneNumber, budget, roles);
            this.cardMap.put(name, card);
        }
    }

    //==================================================================================================================
    // Private API Utility.
    //==================================================================================================================

    private Node extractFirstOccurance (final Node parent, final String elementTag)
    {
        for (final Node node : getElementTypeNodes(parent)) {
            final String nodeName = node.getNodeName();

            if (nodeName.equals(elementTag)) {
                return node;
            }
        }

        return null;
    }

    private Collection<Node> getElementTypeNodes (final Node root)
    {
        final Collection<Node> nodes = new HashSet<>();

        for (int i = 0; i < root.getChildNodes().getLength(); ++i) {
            final Node node = root.getChildNodes().item(i);

            // Ignore text nodes and other node types.
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                nodes.add(node);
            }
        }

        return nodes;
    }

    //==================================================================================================================
    // Public API.
    //==================================================================================================================

    public Map<String, Card> getCardMap ()
    {
        return this.cardMap;
    }

    public Map<String, Room> getRoomMap ()
    {
        return this.roomMap;
    }

    public Room getTrailerRoom ()
    {
        return this.trailerRoom;
    }

    public Room getUpgradeRoom ()
    {
        return this.upgradeRoom;
    }

    //==================================================================================================================
    // Static access.
    //==================================================================================================================

    public static AssetManager getInstance ()
    {
        return AssetManager.instance;
    }

    public static void setupAssetManager (final File assetDirectory) throws Exception
    {
        AssetManager.instance = new AssetManager(assetDirectory);
    }

    //==================================================================================================================
    // Nested classes.
    //==================================================================================================================

    private static class PartiallyLoadedRoom
    {
        private Room room;
        private Collection<String> neighbors;

        private PartiallyLoadedRoom (final Room room, final Collection<String> neighbors)
        {
            this.room = room;
            this.neighbors = neighbors;
        }

        @Override
        public String toString ()
        {
            return "[" + room.toString() + "|" + neighbors.toString() + "]";
        }
    }
}
