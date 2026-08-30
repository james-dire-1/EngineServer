package com.james.serverSide.communication;

import com.james.serverSide.simulation.collisionEngine.hitboxes.AABBHitbox;
import com.james.serverSide.simulation.objects.*;
import com.james.serverSide.simulation.objects.MoveUpdatable;
import com.james.serverSide.simulation.collisionEngine.hitboxes.SphereHitbox;
import org.lwjgl.util.vector.Vector3f;
import templates.serverSide.communication.ServerPacketSendEvents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetworkBroadcaster {

    private final Map<PhysicalObject, PhysicalObjectState> physicalObjectStates = new HashMap<>();
    private final Map<ConnectedPlayer, ConnectedPlayerState> connectedPlayerStates = new HashMap<>();
    private final Map<Updatable, Updatable.State> updatableStates = new HashMap<>();
    private final Map<VirtualLight, VirtualLightState> virtualLightStates = new HashMap<>();
    private final Map<VirtualDirectionalLight, VirtualDirectionalLightState> virtualDirectionalLightStates = new HashMap<>();

    private final List<PhysicalObject> addedPhysicalObjects = new ArrayList<>();
    private final List<VirtualLight> addedVirtualLights = new ArrayList<>();
    private final List<VirtualDirectionalLight> addedVirtualDirectionalLights = new ArrayList<>();
    private final List<AABBHitbox> addedAABBHitboxes = new ArrayList<>();
    private final List<SphereHitbox> addedSphereHitboxes = new ArrayList<>();
    private SkyboxDetails newSkyboxDetails = null;

    private final List<PhysicalObject> removedPhysicalObjects = new ArrayList<>();
    private final List<VirtualLight> removedVirtualLights = new ArrayList<>();
    private final List<VirtualDirectionalLight> removedVirtualDirectionalLights = new ArrayList<>();
    private final List<AABBHitbox> removedAABBHitboxes = new ArrayList<>();
    private final List<SphereHitbox> removedSphereHitboxes = new ArrayList<>();

    private final ServerPacketSendEvents events;

    public NetworkBroadcaster(ServerPacketSendEvents events) {
        this.events = events;
    }

    public void trackPhysicalObjectAndQueueAdditionBroadcast(PhysicalObject object) {
        if (object instanceof MoveUpdatable) {
            if (physicalObjectStates.containsKey(object)) {
                System.err.println("Already tracking this PhysicalObject");
                throw new RuntimeException();
            }

            physicalObjectStates.put(object, new PhysicalObjectState());
        }

        addedPhysicalObjects.add(object);
    }

    public void trackConnectedPlayer(ConnectedPlayer player) {
        if (connectedPlayerStates.containsKey(player)) {
            System.err.println("Already tracking this ConnectedPlayer");
            throw new RuntimeException();
        }

        connectedPlayerStates.put(player, new ConnectedPlayerState());
    }

    public void trackUpdatable(Updatable updatable, Updatable.State updatableState) {
        if (updatableStates.containsKey(updatable)) {
            System.err.println("Already tracking this Updatable");
            throw new RuntimeException();
        }

        updatableStates.put(updatable, updatableState);
    }

    public void trackVirtualLightAndQueueAdditionBroadcast(VirtualLight light, boolean shouldBeTracked) {
        if (shouldBeTracked) {
            if (virtualLightStates.containsKey(light)) {
                System.err.println("Already tracking this VirtualLight");
                throw new RuntimeException();
            }

            virtualLightStates.put(light, new VirtualLightState());
        }

        addedVirtualLights.add(light);
    }

    public void trackVirtualDirectionalLightAndQueueAdditionBroadcast(VirtualDirectionalLight light, boolean shouldBeTracked) {
        if (shouldBeTracked) {
            if (virtualDirectionalLightStates.containsKey(light)) {
                System.err.println("Already tracking this VirtualDirectionalLight");
                throw new RuntimeException();
            }

            virtualDirectionalLightStates.put(light, new VirtualDirectionalLightState());
        }

        addedVirtualDirectionalLights.add(light);
    }

    public void aabbHitboxQueueAdditionBroadcast(AABBHitbox hitbox) {
        addedAABBHitboxes.add(hitbox);
    }

    public void sphereHitboxQueueAdditionBroadcast(SphereHitbox hitbox) {
        addedSphereHitboxes.add(hitbox);
    }

    public void newSkyboxDetailsBroadcast(String name, boolean unmoving) {
        this.newSkyboxDetails = new SkyboxDetails(name, unmoving);
    }

    public void stopTrackingPhysicalObjectAndQueueRemovalBroadcast(PhysicalObject object) {
        physicalObjectStates.remove(object); // if present
        removedPhysicalObjects.add(object);
    }

    public void stopTrackingConnectedPlayer(ConnectedPlayer player) {
        ConnectedPlayerState previousValue = connectedPlayerStates.remove(player); // should always be present
        if (previousValue == null) throw new RuntimeException();
    }

    public void stopTrackingUpdatable(Updatable updatable) {
        updatableStates.remove(updatable); // if present
    }

    public void stopTrackingVirtualLightAndQueueRemovalBroadcast(VirtualLight light) {
        virtualLightStates.remove(light); // if present
        removedVirtualLights.add(light);
    }

    public void stopTrackingVirtualDirectionalLightAndQueueRemovalBroadcast(VirtualDirectionalLight light) {
        virtualDirectionalLightStates.remove(light); // if present
        removedVirtualDirectionalLights.add(light);
    }

    public void aabbHitboxQueueRemovalBroadcast(AABBHitbox hitbox) {
        removedAABBHitboxes.add(hitbox);
    }

    public void sphereHitboxQueueRemovalBroadcast(SphereHitbox hitbox) {
        removedSphereHitboxes.add(hitbox);
    }

    public void broadcastAllChanges() {
        broadcastAllAdditions();
        broadcastAllRemovals();

        broadcastPhysicalObjectChanges();
        broadcastConnectedPlayerChanges();
        broadcastUpdatableChanges();
        broadcastVirtualLightChanges();
        broadcastVirtualDirectionalLightChanges();
    }

    private void broadcastAllAdditions() {
        for (PhysicalObject object : addedPhysicalObjects) {
            events.sendPhysicalObjectAddedToLevel(object.id, object.type, object.getPosition(), object.getRotation(), object.getScale());
        }
        addedPhysicalObjects.clear();

        for (VirtualLight light : addedVirtualLights) {
            events.sendVirtualLightAddedToLevel(light.id, light.getPosition(), light.getColor(), light.getAttenuation());
        }
        addedVirtualLights.clear();

        for (VirtualDirectionalLight light : addedVirtualDirectionalLights) {
            events.sendVirtualDirectionalLightAddedToLevel(light.id, light.getToLightDirection(), light.getColor());
        }
        addedVirtualDirectionalLights.clear();

        for (AABBHitbox hitbox : addedAABBHitboxes) {
            events.sendAABBHitboxAdded(((PhysicalObject) hitbox.object).id, hitbox.meshPath, hitbox.subMeshIdentifier);
        }
        addedAABBHitboxes.clear();

        for (SphereHitbox hitbox : addedSphereHitboxes) {
            events.sendSphereHitboxAdded(((PhysicalObject) hitbox.object).id, hitbox.radius);
        }
        addedSphereHitboxes.clear();

        if (newSkyboxDetails != null) {
            events.sendSkyboxChanged(newSkyboxDetails.name, newSkyboxDetails.unmoving);
            newSkyboxDetails = null;
        }
    }

    private void broadcastAllRemovals() {
        for (PhysicalObject object : removedPhysicalObjects) {
            events.sendPhysicalObjectRemovedFromLevel(object.id);
        }
        removedPhysicalObjects.clear();

        for (VirtualLight light : removedVirtualLights) {
            events.sendVirtualLightRemovedFromLevel(light.id);
        }
        removedVirtualLights.clear();

        for (VirtualDirectionalLight light : removedVirtualDirectionalLights) {
            events.sendVirtualDirectionalLightRemovedFromLevel(light.id);
        }
        removedVirtualDirectionalLights.clear();

        for (AABBHitbox hitbox : removedAABBHitboxes) {
            events.sendAABBHitboxRemoved(((PhysicalObject) hitbox.object).id, hitbox.meshPath, hitbox.subMeshIdentifier);
        }
        removedAABBHitboxes.clear();

        for (SphereHitbox hitbox : removedSphereHitboxes) {
            events.sendSphereHitboxRemoved(((PhysicalObject) hitbox.object).id, hitbox.radius);
        }
        removedSphereHitboxes.clear();
    }

    private void broadcastPhysicalObjectChanges() {
        for (PhysicalObject object : physicalObjectStates.keySet()) {
            PhysicalObjectState state = physicalObjectStates.get(object);
            int id = object.id;
            Vector3f position = object.getPosition();
            Vector3f rotation = object.getRotation();
            float scale = object.getScale();

            boolean positionChanged = !state.prevPosition.equals(position);
            boolean rotationChanged = !state.prevRotation.equals(rotation);
            boolean scaleChanged = state.prevScale != scale;

            if (positionChanged) state.prevPosition.set(position);
            if (rotationChanged) state.prevRotation.set(rotation);
            if (scaleChanged) state.prevScale = scale;

            if (positionChanged && !rotationChanged && !scaleChanged) {
                events.sendPhysicalObjectMoved(id, position.x, position.y, position.z);
            } else if (!positionChanged && rotationChanged && !scaleChanged) {
                events.sendPhysicalObjectRotated(id, rotation.x, rotation.y, rotation.z);
            } else if (!positionChanged && !rotationChanged && scaleChanged) {
                events.sendPhysicalObjectScaled(id, scale);
            } else if (positionChanged || rotationChanged) {
                events.sendPhysicalObjectTransformChanged(id, position, rotation, scale);
            }
        }
    }

    private void broadcastConnectedPlayerChanges() {
        for (ConnectedPlayer player : connectedPlayerStates.keySet()) {
            ConnectedPlayerState state = connectedPlayerStates.get(player);
            Vector3f position = player.getPosition();
            float rotY = player.getRotation().y;

            boolean positionChanged = !state.prevPosition.equals(position);
            boolean rotYChanged = state.prevRotY != rotY;

            if (positionChanged) state.prevPosition.set(position);
            if (rotYChanged) state.prevRotY = rotY;

            if (positionChanged || rotYChanged) {
                events.sendConnectedPlayerTransformChanged(player.id, position.x, position.y, position.z, rotY, player.playerInfo);
            }
        }
    }

    private void broadcastUpdatableChanges() {
        for (Updatable.State updatableState : updatableStates.values()) {
            updatableState.checkStateAndBroadcastIfNecessary(events);
        }
    }

    private void broadcastVirtualLightChanges() {
        for (VirtualLight light : virtualLightStates.keySet()) {
            VirtualLightState state = virtualLightStates.get(light);
            int id = light.id;
            Vector3f position = light.getPosition();
            Vector3f color = light.getColor();
            Vector3f attenuation = light.getAttenuation();

            boolean positionChanged = !state.prevPosition.equals(position);
            boolean colorChanged = !state.prevColor.equals(color);
            boolean attenuationChanged = !state.prevAttenuation.equals(attenuation);

            if (positionChanged) state.prevPosition.set(position);
            if (colorChanged) state.prevColor.set(color);
            if (attenuationChanged) state.prevAttenuation.set(attenuation);

            if (positionChanged && !colorChanged && !attenuationChanged) {
                events.sendVirtualLightMoved(id, position.x, position.y, position.z);
            } else if (!positionChanged && colorChanged && !attenuationChanged) {
                events.sendVirtualLightColorChanged(id, color.x, color.y, color.z);
            } else if (!positionChanged && !colorChanged && attenuationChanged) {
                events.sendVirtualLightAttenuationChanged(id, attenuation.x, attenuation.y, attenuation.z);
            } else if (positionChanged || colorChanged) {
                events.sendVirtualLightPropertiesChanged(id, position, color, attenuation);
            }
        }
    }

    private void broadcastVirtualDirectionalLightChanges() {
        for (VirtualDirectionalLight light : virtualDirectionalLightStates.keySet()) {
            VirtualDirectionalLightState state = virtualDirectionalLightStates.get(light);
            int id = light.id;
            Vector3f toLightDirection = light.getToLightDirection();
            Vector3f color = light.getColor();

            boolean toLightDirectionChanged = !state.prevToLightDirection.equals(toLightDirection);
            boolean colorChanged = !state.prevColor.equals(color);

            if (toLightDirectionChanged) state.prevToLightDirection.set(toLightDirection);
            if (colorChanged) state.prevColor.set(color);

            if (toLightDirectionChanged && !colorChanged) {
                events.sendVirtualDirectionalLightToLightDirectionChanged(id, toLightDirection.x, toLightDirection.y, toLightDirection.z);
            } else if (!toLightDirectionChanged && colorChanged) {
                events.sendVirtualDirectionalLightColorChanged(id, color.x, color.y, color.z);
            } else if (toLightDirectionChanged) {
                events.sendVirtualDirectionalLightPropertiesChanged(id, toLightDirection, color);
            }
        }
    }

    private static class PhysicalObjectState {
        private final Vector3f prevPosition = new Vector3f();
        private final Vector3f prevRotation = new Vector3f();
        private float prevScale;
    }

    private static class ConnectedPlayerState {
        private final Vector3f prevPosition = new Vector3f();
        private float prevRotY;
    }

    private static class VirtualLightState {
        private final Vector3f prevPosition = new Vector3f();
        private final Vector3f prevColor = new Vector3f();
        private final Vector3f prevAttenuation = new Vector3f();
    }

    private static class VirtualDirectionalLightState {
        private final Vector3f prevToLightDirection = new Vector3f();
        private final Vector3f prevColor = new Vector3f();
    }

    private static class SkyboxDetails {
        private final String name;
        private final boolean unmoving;

        private SkyboxDetails(String name, boolean unmoving) {
            this.name = name;
            this.unmoving = unmoving;
        }
    }

}
