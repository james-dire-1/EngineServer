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
import templates.serverSide.PlayerInfo;
import com.james.serverSide.ServerThreadManager;

import java.util.*;

// TODO: 2026-06-04 This class is kind of a mess; maybe restructure?
/**
 * Class representing an instance of a game world, sort of what some game engines would call a "Scene". Each
 * instance of Level contains a list of PhysicalObjects belonging to it, as well as an update method which gets
 * called from its LevelInitializer every game tick (determined by the Level).
 * @see LevelInitializer
 * @see PhysicalObject
 */
public class Level extends LevelProperties {

    public final ServerPacketSendEvents events;
    public final Vector3f primarySpawnPoint;
    public boolean isPaused = false;

    private final Map<PlayerInfo, ConnectedPlayer> connectedPlayersMap = new HashMap<>();
    private final List<PhysicalObject> physicalObjects = new ArrayList<>();
    private final CollisionHandler collisionHandler = new CollisionHandler(this);

    private final List<ServerThreadManager.Action> actionsCopied = new ArrayList<>();

    private final Vector3f prevPosition = new Vector3f();
    private final Vector3f prevRotation = new Vector3f();

    public Level(String name, Vector3f primarySpawnPoint, ServerPacketSendEvents events) {
        this.primarySpawnPoint = primarySpawnPoint;
        this.events = events;

        synchronized (lock) {
            nameToLevelMap.put(name, this);
        }
    }

    /**
     * Method that gets called every game tick, calling all the Level's PhysicalObjects' update() method. It
     * also updates collisions.
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

            // Logic other than move (including rotations!)
            Iterator<PhysicalObject> iterator = physicalObjects.iterator();
            while (iterator.hasNext()) {
                PhysicalObject obj = iterator.next();

                prevRotation.set(obj.getRotation());
                boolean shouldDelete = obj.update();

                if (shouldDelete) {
                    // TODO: 2025-07-02 At the moment, we don't have a way of removing PhysicalObjects client side
                    iterator.remove();
                } else {
                    Vector3f newRotation = obj.getRotation();
                    if (!newRotation.equals(prevRotation)) {
                        events.sendPhysicalObjectRotated(obj.id, newRotation.x, newRotation.y, newRotation.z);
                    }
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

    // TODO: 2025-07-23 Continue from here (replace Server.get().sendToAllClients()) - this is from server code
    public Set<PlayerInfo> getAllPlayerInfo() {
        return connectedPlayersMap.keySet();
    }

    private final List<PhysicalObject> objectsToAdd = new ArrayList<>();
    public void add(PhysicalObject obj) {
        objectsToAdd.add(obj);
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
        synchronized (lock) {
            return nameToLevelMap.get(name);
        }
    }

    public static Level getFirstLevel() {
        synchronized (lock) {
            return nameToLevelMap.values().toArray(new Level[0])[0];
        }
    }

    public static void clearNameToLevelMap() {
        synchronized (lock) {
            nameToLevelMap.clear();
        }
    }

    private static final Object lock = new Object();

}
