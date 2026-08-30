package com.james.serverSide.simulation.objects;

import org.lwjgl.util.vector.Vector3f;

public class VirtualDirectionalLight {

    public final int id;

    private final Vector3f toLightDirection;
    private final Vector3f color;

    public Vector3f getToLightDirection() { return toLightDirection; }
    public Vector3f getColor() { return color; }

    private static int count;

    public VirtualDirectionalLight(Vector3f toLightDirection, Vector3f color) {
        this.toLightDirection = toLightDirection;
        this.color = color;

        count++;
        this.id = count;
    }

    public void setToLightDirection(float x, float y, float z) {
        this.toLightDirection.set(x, y, z);
    }

    public void setColor(float r, float g, float b) {
        this.color.set(r, g, b);
    }

}
