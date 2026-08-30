package templates.serverSide.communication;

import templates.common.audio.Sound;
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
    void notifyUsernameSuccess(PlayerInfo playerInfo, String username, int color, Vector3f spawnPoint);
    void notifyThatLevelIsReady(PlayerInfo playerInfo);
    void sendPhysicalObjectAddedToLevel(int id, PhysicalObjectType type, Vector3f position, Vector3f rotation, float scale, PlayerInfo... playerInfoArray);
    void sendPhysicalObjectRemovedFromLevel(int id);
    void sendPhysicalObjectMoved(int id, float x, float y, float z);
    void sendPhysicalObjectRotated(int id, float rotX, float rotY, float rotZ);
    void sendPhysicalObjectScaled(int id, float scale);
    void sendPhysicalObjectTransformChanged(int id, Vector3f position, Vector3f rotation, float scale);
    void sendAABBHitboxAdded(int id, String meshPath, int subMeshIdentifier, PlayerInfo... playerInfoArray);
    void sendAABBHitboxRemoved(int id, String meshPath, int subMeshIdentifier);
    void sendSphereHitboxAdded(int id, float radius, PlayerInfo... playerInfoArray);
    void sendSphereHitboxRemoved(int id, float radius);
    void sendConnectedPlayerAdded(int id, String username, int color, float x, float y, float z, float rotY, PlayerInfo playerInfo);
    void sendConnectedPlayerTransformChanged(int id, float x, float y, float z, float rotY, PlayerInfo exceptPlayerInfo);
    void sendConnectedPlayerLeft(int id, PlayerInfo exceptPlayerInfo);
    void sendLevelSecondsPerGameTickChanged(float secondsPerGameTick);
    void sendLevelGravityChanged(float x, float y, float z);
    void confirmChatMessageReception(PlayerInfo playerInfo, int localMessageId);
    void broadcastChatMessage(int playerId, String message, PlayerInfo exceptPlayerInfo);
    void broadcastSystemMessage(String message);
    void sendVirtualLightAddedToLevel(int id, Vector3f position, Vector3f color, Vector3f attenuation, PlayerInfo... playerInfoArray);
    void sendVirtualLightRemovedFromLevel(int id);
    void sendVirtualLightMoved(int id, float x, float y, float z);
    void sendVirtualLightColorChanged(int id, float r, float g, float b);
    void sendVirtualLightAttenuationChanged(int id, float att1, float att2, float att3);
    void sendVirtualLightPropertiesChanged(int id, Vector3f position, Vector3f color, Vector3f attenuation);
    void sendVirtualDirectionalLightAddedToLevel(int id, Vector3f toLightDirection, Vector3f color, PlayerInfo... playerInfoArray);
    void sendVirtualDirectionalLightRemovedFromLevel(int id);
    void sendVirtualDirectionalLightToLightDirectionChanged(int id, float x, float y, float z);
    void sendVirtualDirectionalLightColorChanged(int id, float r, float g, float b);
    void sendVirtualDirectionalLightPropertiesChanged(int id, Vector3f toLightDirection, Vector3f color);
    void sendSkyboxChanged(String name, boolean unmoving, PlayerInfo... playerInfoArray);
    void sendPlaySoundAtPhysicalObject(Sound sound, int id);
    void sendPlaySoundAtPosition(Sound sound, Vector3f position);
    void sendCreateSoundEmitter(int customIdentifier, float x, float y, float z, PlayerInfo... playerInfoArray);
    void sendDestroySoundEmitter(int customIdentifier);
    void sendPlaySoundAtSoundEmitter(Sound sound, int customIdentifier);
    void sendUpdatePositionOfSoundEmitter(int customIdentifier, float x, float y, float z);

}
