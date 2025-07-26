package newStuff;

import templates.common.networking.PacketType;
import com.james.serverSide.PlayerInfo;
import com.james.serverSide.ServerThreadManager;
import com.james.serverSide.simulation.Level;
import templates.serverSide.communication.ServerPacketReceiveActions;
import templates.serverSide.communication.ServerProperties;

import java.util.HashMap;
import java.util.Map;

public class OnlineServerProperties implements ServerProperties {

    private static final Map<ConnectedClient, PlayerInfo> clientsToPlayerInfoMap = new HashMap<>();
    // TODO: 2024-07-11 The most recent connected client thing is a bad system
    private static volatile ConnectedClient mostRecentConnectedClient;

    @Override
    public void assignPlayerInfo(PlayerInfo playerInfo) {
        synchronized (clientsToPlayerInfoMap) {
            clientsToPlayerInfoMap.put(mostRecentConnectedClient, playerInfo);
        }
        playerInfo.setConnectedClient(mostRecentConnectedClient);
    }

    // TODO: 2024-07-10 Setting the read listeners probably shouldn't be done after ConnectedClient creation..
    public static void clientJoined(ConnectedClient connectedClient) {
        Window.get().println("A client has connected to the server.");

        connectedClient.setReadListener(PacketType.PLAYER_USERNAME, OnlineServerProperties::playerUsernameReceived);
        connectedClient.setReadListener(PacketType.PLAYER_JOINED, OnlineServerProperties::playerJoinedReceived);
        connectedClient.setReadListener(PacketType.PLAYER_MOVED, OnlineServerProperties::playerMovedReceived);
        connectedClient.setReadListener(PacketType.LEVEL_CHANGE_PAUSE_STATE, OnlineServerProperties::changePauseStateReceived);
        connectedClient.setReadListener(PacketType.CHAT_MESSAGE, OnlineServerProperties::chatMessageReceived);
        connectedClient.setDisconnectListener(OnlineServerProperties::onClientDisconnect);

        OnlineServerProperties serverProperties = new OnlineServerProperties();
        // TODO: 2024-07-07 does it matter whether this is done on this thread or on the level thread?
        mostRecentConnectedClient = connectedClient;

        ServerThreadManager.executeOnALevelThread(Level.getByName("main"), () -> {
            ServerPacketReceiveActions.clientJoined(serverProperties);
        });
    }

    private static void playerUsernameReceived(ConnectedClient connectedClient, Object[] objects) {
        String username = (String) objects[0];

        PlayerInfo playerInfo = getPlayerInfo(connectedClient);

        ServerThreadManager.executeOnALevelThread(Level.getByName("main"), () -> {
            ServerPacketReceiveActions.playerUsernameReceived(playerInfo, username);
        });
    }

    private static void playerJoinedReceived(ConnectedClient connectedClient, Object[] objects) {
        Window.get().println("A player has joined.");

        float x = (float) objects[0];
        float y = (float) objects[1];
        float z = (float) objects[2];
        float rotY = (float) objects[3];

        PlayerInfo playerInfo = getPlayerInfo(connectedClient);

        ServerThreadManager.executeOnALevelThread(Level.getByName("main"), () -> {
            ServerPacketReceiveActions.playerJoinedReceived(playerInfo, x, y, z, rotY);
        });
    }

    private static void playerMovedReceived(ConnectedClient connectedClient, Object[] objects) {
        float x = (float) objects[0];
        float y = (float) objects[1];
        float z = (float) objects[2];
        float rotY = (float) objects[3];

        PlayerInfo playerInfo = getPlayerInfo(connectedClient);

        ServerThreadManager.executeOnALevelThread(playerInfo.level, () -> {
            ServerPacketReceiveActions.playerTransformChangedReceived(playerInfo, x, y, z, rotY);
        });
    }

    // TODO: 2024-07-07 are you sure you want to keep this in the future?
    private static void changePauseStateReceived(ConnectedClient connectedClient, Object[] objects) {
        boolean shouldPause = (boolean) objects[0];

        PlayerInfo playerInfo = getPlayerInfo(connectedClient);

        ServerThreadManager.executeOnALevelThread(playerInfo.level, () -> {
            ServerPacketReceiveActions.changePauseStateReceived(playerInfo, shouldPause);
        });
    }

    private static void chatMessageReceived(ConnectedClient connectedClient, Object[] objects) {
        int localMessageId = (int) objects[0];
        String message = (String) objects[1];

        PlayerInfo playerInfo = getPlayerInfo(connectedClient);

        ServerThreadManager.executeOnALevelThread(playerInfo.level, () -> {
            ServerPacketReceiveActions.chatMessageReceived(playerInfo, localMessageId, message);
        });
    }

    private static void onClientDisconnect(Exception e, ConnectedClient connectedClient) {
        Window.get().println("A client has disconnected: " + e.toString());

        PlayerInfo playerInfo;
        synchronized (clientsToPlayerInfoMap) {
            playerInfo = clientsToPlayerInfoMap.remove(connectedClient);
        }

        ServerThreadManager.executeOnALevelThread(playerInfo.level, () -> {
            ServerPacketReceiveActions.playerLeftReceived(playerInfo);
        });
    }

    private static PlayerInfo getPlayerInfo(ConnectedClient connectedClient) {
        PlayerInfo playerInfo;
        synchronized (clientsToPlayerInfoMap) {
            playerInfo = clientsToPlayerInfoMap.get(connectedClient);
        }

        return playerInfo;
    }

}
