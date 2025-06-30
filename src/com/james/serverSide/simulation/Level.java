package com.james.serverSide.simulation;

import com.james.serverSide.LevelInitializer;
import com.james.serverSide.simulation.collisionEngine.hitboxes.AABBHitbox;
import com.james.serverSide.simulation.collisionEngine.CollisionHandler;
import com.james.serverSide.simulation.collisionEngine.hitboxes.EllipsoidHitbox;
import com.james.common.simulation.LevelProperties;
import com.james.serverSide.simulation.objects.MovableObject;
import com.james.serverSide.simulation.objects.PhysicalObject;
import org.lwjgl.util.vector.Vector3f;
import templates.serverSide.communication.ServerPacketSendEvents;
import com.james.serverSide.simulation.objects.ConnectedPlayer;
import com.james.serverSide.PlayerInfo;
import com.james.serverSide.ServerThreadManager;

import java.util.*;

/**
 * Class representing an instance of a game world, sort of what some game engines would call a "Scene".
 * Each instance of Level contains a list of PhysicalObjects belonging to it, as well as an update method
 * which gets called from its LevelInitializer every game tick (determined by the Level).
 * @see LevelInitializer
 * @see PhysicalObject
 */
public class Level extends LevelProperties {

    public final ServerPacketSendEvents events;
    public boolean isPaused = false;

    private final Map<PlayerInfo, ConnectedPlayer> connectedPlayersMap = new HashMap<>();
    private final List<PhysicalObject> physicalObjects = new ArrayList<>();
    private final CollisionHandler collisionHandler = new CollisionHandler(this);

    private final List<ServerThreadManager.Action> actionsCopied = new ArrayList<>();

    private final Vector3f prevPosition = new Vector3f();

    public Level(String name, ServerPacketSendEvents events) {
        this.events = events;

        nameToLevelMap.put(name, this);
    }

    /**
     * Method that gets called every game tick, calling all the Level's PhysicalObjects' update() method.
     * It also updates collisions.
     */
    public void update() {
        boolean actionToExecute = ServerThreadManager.getActionsForLevel(this, actionsCopied);

        if (actionToExecute) {
            for (ServerThreadManager.Action action : actionsCopied) {
                action.invoke();
            }
        }

        // Send any necessary stuff to clients

        // TODO: 2024-07-21 Should this always be sent, even if the player didn't move?
        for (Map.Entry<PlayerInfo, ConnectedPlayer> entry : connectedPlayersMap.entrySet()) {
            PlayerInfo playerInfo = entry.getKey();
            ConnectedPlayer connectedPlayer = entry.getValue();
            Vector3f position = connectedPlayer.getPosition();
            float rotY = connectedPlayer.getRotation().y;

            events.sendConnectedPlayerTransformChanged(connectedPlayer.id, position.x, position.y, position.z, rotY, playerInfo);
        }

        if (!isPaused) {
            // Move
            for (PhysicalObject obj : physicalObjects) {
                if (obj instanceof MovableObject) {
                    MovableObject movableObj = (MovableObject) obj;

                    if (!movableObj.isAffectedByAABBCollisions) {
                        prevPosition.set(movableObj.getPosition());
                        movableObj.moveUpdate();

                        Vector3f newPosition = movableObj.getPosition();
                        if (!newPosition.equals(prevPosition)) {
                            events.sendPhysicalObjectMoved(movableObj.id, newPosition.x, newPosition.y, newPosition.z);
                        }
                    }
                }
            }
            collisionHandler.update();

            // Other logic other than move
            Iterator<PhysicalObject> iterator = physicalObjects.iterator();
            while (iterator.hasNext()) {
                PhysicalObject obj = iterator.next();

                boolean shouldDelete = obj.update();
                if (shouldDelete) {
                    iterator.remove();
                }
            }

            physicalObjects.addAll(objectsToAdd);
            objectsToAdd.clear();
        }
    }

    public void addConnectedPlayer(PlayerInfo playerInfo, ConnectedPlayer connectedPlayer) {
        connectedPlayersMap.put(playerInfo, connectedPlayer);
    }

    public ConnectedPlayer removeConnectedPlayer(PlayerInfo playerInfo) {
        return connectedPlayersMap.remove(playerInfo);
    }

    public Map<PlayerInfo, ConnectedPlayer> getConnectedPlayersMap() {
        return connectedPlayersMap;
    }

    public ConnectedPlayer getConnectedPlayer(PlayerInfo playerInfo) {
        return connectedPlayersMap.get(playerInfo);
    }

    private final List<PhysicalObject> objectsToAdd = new ArrayList<>();
    public void add(PhysicalObject obj) {
        physicalObjects.add(obj);
    }

    public List<PhysicalObject> getPhysicalObjects() {
        return physicalObjects;
    }

    public void addEllipsoidHitbox(EllipsoidHitbox ellipsoidHitbox) {
        collisionHandler.ellipsoidHitboxes.add(ellipsoidHitbox);
    }

    public void addAABBHitbox(AABBHitbox aabbHitbox) {
        collisionHandler.aabbHitboxes.add(aabbHitbox);
    }

    public List<AABBHitbox> getAABBHitboxes() {
        return collisionHandler.aabbHitboxes;
    }

    /**
     * All Levels can be accessed through this map.
     */
    private static final Map<String, Level> nameToLevelMap = new HashMap<>();
    public static Level getByName(String name) {
        return nameToLevelMap.get(name);
    }

}
