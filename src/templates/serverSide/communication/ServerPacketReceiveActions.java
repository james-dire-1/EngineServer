package templates.serverSide.communication;

import com.james.serverSide.PlayerInfo;
import com.james.serverSide.simulation.Level;
import com.james.serverSide.simulation.collisionEngine.hitboxes.AABBHitbox;
import com.james.serverSide.simulation.objects.ConnectedPlayer;
import com.james.serverSide.simulation.objects.PhysicalObject;
import org.lwjgl.util.vector.Vector3f;

import static templates.common.GlobalConstants.IS_DETAILED_NETWORK_DEBUG;
import static templates.common.GlobalConstants.IS_NETWORK_DEBUG;

/**
 * Methods that handle what should happen on the server side when particular events occur on the client side.
 */
public class ServerPacketReceiveActions {

    // TODO: 2024-06-27 Make the server decide where the player should be placed initially
    public static void playerJoinedReceived(ServerProperties serverProperties, float x, float y, float z, float rotY) {
        if (IS_NETWORK_DEBUG) System.out.println("ServerPacketReceiveActions.playerJoinedReceived");

        Level startLevel = Level.getByName("main");
        PlayerInfo playerInfo = new PlayerInfo(startLevel);
        ConnectedPlayer connectedPlayer = new ConnectedPlayer(new Vector3f(x, y, z), new Vector3f(0, rotY, 0));
        startLevel.addConnectedPlayer(playerInfo, connectedPlayer);

        serverProperties.assignPlayerInfo(playerInfo);

        for (PhysicalObject obj : startLevel.getPhysicalObjects()) {
            startLevel.events.sendPhysicalObjectAddedToLevel(obj.id, obj.type, obj.getPosition(), obj.getRotation(), obj.getScale(), playerInfo);
        }
        for (AABBHitbox aabbHitbox : startLevel.getAABBHitboxes()) {
            startLevel.events.sendAABBHitboxAdded(((PhysicalObject) aabbHitbox.object).id, aabbHitbox.meshPath, playerInfo);
        }
        for (PlayerInfo otherPlayerInfo : startLevel.getConnectedPlayersMap().keySet()) {
            ConnectedPlayer otherConnectedPlayer = otherPlayerInfo.getConnectedPlayer();

            if (otherConnectedPlayer.equals(connectedPlayer)) continue;

            // Send other ConnectedPlayer's info to the newly joined ConnectedPlayer
            Vector3f otherPosition = otherConnectedPlayer.getPosition();
            float otherRotY = otherConnectedPlayer.getRotation().y;
            startLevel.events.sendConnectedPlayerAdded(otherConnectedPlayer.id, otherPosition.x, otherPosition.y, otherPosition.z, otherRotY, playerInfo);

            // Send newly joined ConnectedPlayer's info to the other ConnectedPlayer
            Vector3f joinedPosition = connectedPlayer.getPosition();
            float joinedRotY = connectedPlayer.getRotation().y;
            startLevel.events.sendConnectedPlayerAdded(connectedPlayer.id, joinedPosition.x, joinedPosition.y, joinedPosition.z, joinedRotY, otherPlayerInfo);
        }
        startLevel.events.notifyThatLevelIsReady(playerInfo);
    }

    public static void playerTransformChangedReceived(PlayerInfo playerInfo, float x, float y, float z, float rotY) {
        if (IS_DETAILED_NETWORK_DEBUG) System.out.println("ServerPacketReceiveActions.playerMovedReceived");

        ConnectedPlayer connectedPlayer = playerInfo.getConnectedPlayer();
        connectedPlayer.setPosition(x, y, z);
        connectedPlayer.setRotation(0, rotY, 0);
    }

    public static void playerLeftReceived(PlayerInfo playerInfo) {
        Level playerLevel = playerInfo.level;
        ConnectedPlayer connectedPlayer = playerLevel.removeConnectedPlayer(playerInfo);

        playerLevel.events.sendConnectedPlayerLeft(connectedPlayer.id, playerInfo);
    }

    public static void changePauseStateReceived(PlayerInfo playerInfo, boolean shouldPause) {
        if (IS_NETWORK_DEBUG) System.out.println("ServerPacketReceiveActions.changePauseStateReceived " + "{shouldPause=" + shouldPause + "}");

        playerInfo.level.isPaused = shouldPause;
    }

}
