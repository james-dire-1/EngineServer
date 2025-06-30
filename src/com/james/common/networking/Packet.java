package com.james.common.networking;

import templates.common.networking.PacketType;

import java.io.Serializable;

public class Packet implements Serializable {

    public final PacketType type;
    public final Object[] data;

    public Packet(PacketType type, Object... data) {
        this.type = type;
        this.data = data;
    }

}
