package com.james.serverSide.simulation.objects;

import org.lwjgl.util.vector.Vector3f;
import templates.serverSide.PlayerInfo;

/**
 * Represents a player object on the server-side.
 */
// TODO: 2024-06-30 Make this extend PhysicalObject or something
public class ConnectedPlayer {

    public final int id;
    public final PlayerInfo playerInfo;

    private final Vector3f position;
    private final Vector3f rotation;

    public Vector3f getPosition() { return position; }
    public Vector3f getRotation() { return rotation; }

    private static int count;

    public ConnectedPlayer(PlayerInfo playerInfo, Vector3f position, Vector3f rotation) {
        this.playerInfo = playerInfo;
        this.position = position;
        this.rotation = rotation;

        count++;
        this.id = count;
    }

    public void setPosition(float x, float y, float z) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }

    public void setRotation(float x, float y, float z) {
        this.rotation.x = x;
        this.rotation.y = y;
        this.rotation.z = z;
    }

}
