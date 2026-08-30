package com.james.serverSide.simulation.objects;

import templates.serverSide.communication.ServerPacketSendEvents;

@FunctionalInterface
public interface Updatable {
    boolean update();

    @FunctionalInterface
    interface State {
        void checkStateAndBroadcastIfNecessary(ServerPacketSendEvents events);
    }
}
