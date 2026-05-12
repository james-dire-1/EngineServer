package templates.serverSide.communication;

import templates.common.simulation.objects.PhysicalObjectType;
import templates.serverSide.PlayerInfo;
import org.lwjgl.util.vector.Vector3f;

/**
 * Various unimplemented methods for things that should be done when particular events are triggered on
 * the server. Implementation of these methods depends on whether the server is online (i.e.
 * data needs to be sent to the client), or local (i.e. client methods can be called directly).
 */
public interface ServerPacketSendEvents {

    void sendUsernamePrompt(PlayerInfo playerInfo);
    void notifyUsernameSuccess(PlayerInfo playerInfo, String username, int color);
    void notifyThatLevelIsReady(PlayerInfo playerInfo);
    void sendPhysicalObjectAddedToLevel(int id, PhysicalObjectType type, Vector3f position, Vector3f rotation, float scale, PlayerInfo... playerInfoArray);
    void sendPhysicalObjectMoved(int id, float x, float y, float z);
    void sendPhysicalObjectRotated(int id, float rotX, float rotY, float rotZ);
    void sendPhysicalObjectScaled(int id, float scale);
    void sendPhysicalObjectTransformChanged(int id, Vector3f position, Vector3f rotation, float scale);
    void sendAABBHitboxAdded(int id, String meshPath, int subMeshIdentifier, PlayerInfo... playerInfoArray);
    void sendConnectedPlayerAdded(int id, String username, int color, float x, float y, float z, float rotY, PlayerInfo playerInfo);
    void sendConnectedPlayerTransformChanged(int id, float x, float y, float z, float rotY, PlayerInfo exceptPlayerInfo);
    void sendConnectedPlayerLeft(int id, PlayerInfo exceptPlayerInfo);
    void sendLevelSecondsPerGameTickChanged(float secondsPerGameTick);
    void sendLevelGravityChanged(float x, float y, float z);
    void confirmChatMessageReception(PlayerInfo playerInfo, int localMessageId);
    void broadcastChatMessage(int playerId, String message, PlayerInfo exceptPlayerInfo);
    void broadcastSystemMessage(String message);

}
