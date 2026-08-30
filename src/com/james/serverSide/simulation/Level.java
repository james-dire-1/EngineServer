package com.james.serverSide.simulation;

import com.james.serverSide.LevelInitializer;
import com.james.serverSide.simulation.collisionEngine.hitboxes.AABBHitbox;
import com.james.serverSide.simulation.collisionEngine.CollisionHandler;
import com.james.serverSide.simulation.collisionEngine.hitboxes.EllipsoidHitbox;
import com.james.common.simulation.LevelProperties;
import com.james.serverSide.simulation.objects.*;
import com.james.serverSide.communication.NetworkBroadcaster;
import com.james.serverSide.simulation.objects.Updatable;
import com.james.serverSide.simulation.objects.MoveUpdatable;
import com.james.serverSide.simulation.collisionEngine.RSTCollisionHandler;
import com.james.serverSide.simulation.collisionEngine.hitboxes.SphereHitbox;
import com.james.common.simulation.details.FallingAndGravityState;
import org.lwjgl.util.vector.Vector3f;
import templates.serverSide.communication.ServerPacketSendEvents;
import templates.serverSide.PlayerInfo;
import com.james.serverSide.ServerThreadManager;

import java.util.*;

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
    private final NetworkBroadcaster broadcaster;

    private final Map<PlayerInfo, ConnectedPlayer> connectedPlayersMap = new HashMap<>();
    private final List<PhysicalObject> physicalObjects = new ArrayList<>();
    private final CollisionHandler collisionHandler = new CollisionHandler(this);
    private final RSTCollisionHandler rstCollisionHandler = new RSTCollisionHandler();
    private final List<Updatable> updatables = new ArrayList<>();

    private final List<VirtualLight> virtualLights = new ArrayList<>();
    private final List<VirtualDirectionalLight> virtualDirectionalLights = new ArrayList<>();
    private String skyboxName;
    private boolean skyboxUnmoving;

    private final List<ServerThreadManager.Action> actionsCopied = new ArrayList<>();

    public Level(String name, Vector3f primarySpawnPoint, ServerPacketSendEvents events) {
        this.primarySpawnPoint = primarySpawnPoint;
        this.events = events;
        this.broadcaster = new NetworkBroadcaster(events);

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

        broadcaster.broadcastAllChanges();

        if (!isPaused) {
            for (PhysicalObject object : physicalObjects) {
                if (object instanceof MoveUpdatable) {
                    MoveUpdatable moveUpdatable = (MoveUpdatable) object;
                    moveUpdatable.moveUpdate();

                    if (moveUpdatable instanceof MovableObject) {
                        MovableObject movableObject = (MovableObject) moveUpdatable;

                        if (!movableObject.canCollideWithTriangles) {
                            movableObject.setPositionBasedOnVelocity();
                        }

                        FallingAndGravityState fgState = movableObject.getFallingAndGravityState();
                        if (fgState != null) {
                            fgState.update(this);
                        }
                    }
                }
            }

            collisionHandler.update();
            rstCollisionHandler.updateSphereHitboxes();

            Iterator<Updatable> updatableIterator = updatables.iterator();
            while (updatableIterator.hasNext()) {
                Updatable updatable = updatableIterator.next();
                boolean completed = updatable.update();

                if (completed) {
                    updatableIterator.remove();
                    broadcaster.stopTrackingUpdatable(updatable);
                }
            }
        }
    }

    public void addConnectedPlayer(PlayerInfo playerInfo, ConnectedPlayer connectedPlayer) {
        connectedPlayersMap.put(playerInfo, connectedPlayer);
        broadcaster.trackConnectedPlayer(connectedPlayer);
    }

    public ConnectedPlayer removeConnectedPlayer(PlayerInfo playerInfo) {
        broadcaster.stopTrackingConnectedPlayer(playerInfo.getConnectedPlayer());
        return connectedPlayersMap.remove(playerInfo);
    }

    public Map<PlayerInfo, ConnectedPlayer> getConnectedPlayersMap() {
        return connectedPlayersMap;
    }

    public ConnectedPlayer getConnectedPlayer(PlayerInfo playerInfo) {
        return connectedPlayersMap.get(playerInfo);
    }

    public Set<PlayerInfo> getAllPlayerInfo() {
        return connectedPlayersMap.keySet();
    }

    public void add(PhysicalObject obj) {
        physicalObjects.add(obj);
        if (obj instanceof MoveUpdatable) broadcaster.trackPhysicalObjectAndQueueAdditionBroadcast(obj);
    }

    public boolean remove(PhysicalObject obj) {
        broadcaster.stopTrackingPhysicalObjectAndQueueRemovalBroadcast(obj);
        return physicalObjects.remove(obj);
    }

    public List<PhysicalObject> getPhysicalObjects() {
        return physicalObjects;
    }

    public void addEllipsoidHitbox(EllipsoidHitbox ellipsoidHitbox) {
        collisionHandler.ellipsoidHitboxes.add(ellipsoidHitbox);
    }

    public boolean removeEllipsoidHitbox(EllipsoidHitbox ellipsoidHitbox) {
        return collisionHandler.ellipsoidHitboxes.remove(ellipsoidHitbox);
    }

    public void addAABBHitbox(AABBHitbox aabbHitbox) {
        collisionHandler.aabbHitboxes.add(aabbHitbox);
        broadcaster.aabbHitboxQueueAdditionBroadcast(aabbHitbox);
    }

    public boolean removeAABBHitbox(AABBHitbox aabbHitbox) {
        broadcaster.aabbHitboxQueueRemovalBroadcast(aabbHitbox);
        return collisionHandler.aabbHitboxes.remove(aabbHitbox);
    }

    public List<AABBHitbox> getAABBHitboxes() {
        return collisionHandler.aabbHitboxes;
    }

    public void addSphereHitbox(SphereHitbox sphereHitbox) {
        rstCollisionHandler.sphereHitboxes.add(sphereHitbox);
        broadcaster.sphereHitboxQueueAdditionBroadcast(sphereHitbox);
    }

    public boolean removeSphereHitbox(SphereHitbox sphereHitbox) {
        broadcaster.sphereHitboxQueueRemovalBroadcast(sphereHitbox);
        return rstCollisionHandler.sphereHitboxes.remove(sphereHitbox);
    }

    public List<SphereHitbox> getSphereHitboxes() {
        return rstCollisionHandler.sphereHitboxes;
    }

    public void addUpdatable(Updatable updatable, Updatable.State updatableState) {
        updatables.add(updatable);
        if (updatableState != null) broadcaster.trackUpdatable(updatable, updatableState);
    }

    public void addVirtualLight(VirtualLight virtualLight, boolean shouldBeTracked) {
        virtualLights.add(virtualLight);
        broadcaster.trackVirtualLightAndQueueAdditionBroadcast(virtualLight, shouldBeTracked);
    }

    public boolean removeVirtualLight(VirtualLight virtualLight) {
        broadcaster.stopTrackingVirtualLightAndQueueRemovalBroadcast(virtualLight);
        return virtualLights.remove(virtualLight);
    }

    public List<VirtualLight> getVirtualLights() {
        return virtualLights;
    }

    public void addVirtualDirectionalLight(VirtualDirectionalLight virtualDirectionalLight, boolean shouldBeTracked) {
        virtualDirectionalLights.add(virtualDirectionalLight);
        broadcaster.trackVirtualDirectionalLightAndQueueAdditionBroadcast(virtualDirectionalLight, shouldBeTracked);
    }

    public boolean removeVirtualDirectionalLight(VirtualDirectionalLight virtualDirectionalLight) {
        broadcaster.stopTrackingVirtualDirectionalLightAndQueueRemovalBroadcast(virtualDirectionalLight);
        return virtualDirectionalLights.remove(virtualDirectionalLight);
    }

    public List<VirtualDirectionalLight> getVirtualDirectionalLights() {
        return virtualDirectionalLights;
    }

    public void setSkyboxDetails(String name, boolean unmoving) {
        this.skyboxName = name;
        this.skyboxUnmoving = unmoving;
        broadcaster.newSkyboxDetailsBroadcast(name, unmoving);
    }

    public String getSkyboxName() { return skyboxName; }
    public boolean getSkyboxUnmoving() { return skyboxUnmoving; }

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
