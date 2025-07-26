package com.james.serverSide;

import com.james.serverSide.simulation.Level;
import com.james.serverSide.simulation.objects.ConnectedPlayer;
import com.james.networking.ConnectedClient;
import templates.serverSide.communication.ServerPacketReceiveActions;

// TODO: 2025-07-26 Consider moving PlayerInfo to another package
/**
 * General info for each player connected to the server, such as which Level the player is currently in.
 * Note that this does not represent physical players themselves; that is for ConnectedPlayers. Many of
 * the methods in ServerPacketReceiveActions require that a PlayerInfo object be passed in.
 * @see ConnectedPlayer
 * @see ServerPacketReceiveActions
 */
public class PlayerInfo {

    public String username;
    public int color;

    public Level level;

    private ConnectedClient connectedClient;
    public ConnectedClient getConnectedClient() { return connectedClient; }

    public void setConnectedClient(ConnectedClient connectedClient) {
        this.connectedClient = connectedClient;
    }

    /**
     * Easy way to retrieve this PlayerInfo's ConnectedPlayer object.
     */
    public ConnectedPlayer getConnectedPlayer() {
        return level.getConnectedPlayer(this);
    }

}
