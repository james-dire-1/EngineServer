package templates.serverSide.communication;

import templates.serverSide.PlayerInfo;
import com.james.serverSide.simulation.Level;
import com.james.serverSide.simulation.collisionEngine.hitboxes.AABBHitbox;
import com.james.serverSide.simulation.objects.ConnectedPlayer;
import com.james.serverSide.simulation.objects.PhysicalObject;
import game.serverSide.PlayerColors;
import org.lwjgl.util.vector.Vector3f;

import static templates.common.GlobalConstants.IS_DETAILED_NETWORK_DEBUG;
import static templates.common.GlobalConstants.IS_NETWORK_DEBUG;
import static com.james.common.tools.Logger.log;

/**
 * Methods that handle what should happen on the server side when particular events occur on the client side.
 */
public class ServerPacketReceiveActions {

    public static void clientJoined(ServerProperties serverProperties) {
        if (IS_NETWORK_DEBUG) log("ServerPacketReceiveActions.clientJoined");

        PlayerInfo playerInfo = new PlayerInfo();
        playerInfo.color = PlayerColors.getNextAvailableColor();
        serverProperties.assignPlayerInfo(playerInfo);

        Level startLevel = Level.getFirstLevel();
        startLevel.events.sendUsernamePrompt(playerInfo);
    }

    public static void playerUsernameReceived(PlayerInfo playerInfo, String username) {
        if (IS_NETWORK_DEBUG) log("ServerPacketReceiveActions.playerUsernameReceived");

        playerInfo.username = username;

        Level startLevel = Level.getFirstLevel();
        startLevel.events.notifyUsernameSuccess(playerInfo, playerInfo.username, playerInfo.color, startLevel.primarySpawnPoint);
    }

    // TODO: 2024-06-27 Make the server decide where the player should be placed initially
    public static void playerJoinedReceived(PlayerInfo playerInfo, float x, float y, float z, float rotY) {
        if (IS_NETWORK_DEBUG) log("ServerPacketReceiveActions.playerJoinedReceived");

        Level startLevel = Level.getFirstLevel();
        playerInfo.level = startLevel;
        ConnectedPlayer connectedPlayer = new ConnectedPlayer(new Vector3f(x, y, z), new Vector3f(0, rotY, 0));
        startLevel.addConnectedPlayer(playerInfo, connectedPlayer);

        for (PhysicalObject obj : startLevel.getPhysicalObjects()) {
            startLevel.events.sendPhysicalObjectAddedToLevel(obj.id, obj.type, obj.getPosition(), obj.getRotation(), obj.getScale(), playerInfo);
        }
        for (AABBHitbox aabbHitbox : startLevel.getAABBHitboxes()) {
            startLevel.events.sendAABBHitboxAdded(((PhysicalObject) aabbHitbox.object).id, aabbHitbox.meshPath, aabbHitbox.subMeshIdentifier, playerInfo);
        }
        for (PlayerInfo otherPlayerInfo : startLevel.getConnectedPlayersMap().keySet()) {
            ConnectedPlayer otherConnectedPlayer = otherPlayerInfo.getConnectedPlayer();

            if (otherConnectedPlayer.equals(connectedPlayer)) continue;

            // Send other ConnectedPlayer's info to the newly joined ConnectedPlayer
            Vector3f otherPosition = otherConnectedPlayer.getPosition();
            float otherRotY = otherConnectedPlayer.getRotation().y;
            startLevel.events.sendConnectedPlayerAdded(otherConnectedPlayer.id, otherPlayerInfo.username, otherPlayerInfo.color, otherPosition.x, otherPosition.y, otherPosition.z, otherRotY, playerInfo);

            // Send newly joined ConnectedPlayer's info to the other ConnectedPlayer
            Vector3f joinedPosition = connectedPlayer.getPosition();
            float joinedRotY = connectedPlayer.getRotation().y;
            startLevel.events.sendConnectedPlayerAdded(connectedPlayer.id, playerInfo.username, playerInfo.color, joinedPosition.x, joinedPosition.y, joinedPosition.z, joinedRotY, otherPlayerInfo);
        }
        startLevel.events.notifyThatLevelIsReady(playerInfo);
        startLevel.events.broadcastSystemMessage(playerInfo.username + " has joined the game");
    }

    public static void playerTransformChangedReceived(PlayerInfo playerInfo, float x, float y, float z, float rotY) {
        if (IS_DETAILED_NETWORK_DEBUG) log("ServerPacketReceiveActions.playerMovedReceived");

        ConnectedPlayer connectedPlayer = playerInfo.getConnectedPlayer();
        connectedPlayer.setPosition(x, y, z);
        connectedPlayer.setRotation(0, rotY, 0);
    }

    public static void playerLeftReceived(PlayerInfo playerInfo) {
        if (IS_NETWORK_DEBUG) log("ServerPacketReceiveActions.playerLeftReceived");

        Level playerLevel = playerInfo.level;
        ConnectedPlayer connectedPlayer = playerLevel.removeConnectedPlayer(playerInfo);
        PlayerColors.freeColor(playerInfo.color);

        playerLevel.events.sendConnectedPlayerLeft(connectedPlayer.id, playerInfo);
        playerLevel.events.broadcastSystemMessage(playerInfo.username + " has left the game");
    }

    public static void changePauseStateReceived(PlayerInfo playerInfo, boolean shouldPause) {
        if (IS_NETWORK_DEBUG) log("ServerPacketReceiveActions.changePauseStateReceived " + "{shouldPause=" + shouldPause + "}");

        playerInfo.level.isPaused = shouldPause;
    }

    public static void chatMessageReceived(PlayerInfo playerInfo, int localMessageId, String message) {
        if (IS_NETWORK_DEBUG) log("ServerPacketReceiveActions.chatMessageReceived");

        Level playerLevel = playerInfo.level;

        playerLevel.events.confirmChatMessageReception(playerInfo, localMessageId);

        int playerId = playerInfo.getConnectedPlayer().id;
        playerLevel.events.broadcastChatMessage(playerId, message, playerInfo);
    }

}
