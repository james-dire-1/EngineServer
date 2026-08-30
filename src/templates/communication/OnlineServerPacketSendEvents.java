package templates.communication;

import com.james.common.networking.Packet;
import com.james.networking.Server;
import templates.common.audio.Sound;
import templates.common.networking.PacketType;
import templates.common.simulation.objects.PhysicalObjectType;
import templates.serverSide.PlayerInfo;
import org.lwjgl.util.vector.Vector3f;
import templates.serverSide.communication.ServerPacketSendEvents;

import static templates.common.GlobalConstants.IS_DETAILED_NETWORK_DEBUG;
import static templates.common.GlobalConstants.IS_NETWORK_DEBUG;
import static com.james.common.tools.Logger.println;

public class OnlineServerPacketSendEvents implements ServerPacketSendEvents {

    @Override
    public void sendUsernamePrompt(PlayerInfo playerInfo) {
        if (IS_NETWORK_DEBUG) println("OnlineServerPacketSendEvents.promptUsername");

        Packet packet = new Packet(PacketType.USERNAME_PROMPT, (Object[]) null);
        playerInfo.getConnectedClient().sendPacket(packet);
    }

    @Override
    public void notifyUsernameSuccess(PlayerInfo playerInfo, String username, int color, Vector3f spawnPoint) {
        if (IS_NETWORK_DEBUG) println("OnlineServerPacketSendEvents.notifyUsernameSuccess");

        Object[] objects = { username, color, spawnPoint };

        Packet packet = new Packet(PacketType.USERNAME_SUCCESS, objects);
        playerInfo.getConnectedClient().sendPacket(packet);
    }

    // TODO: 2024-07-11 In the future, all calls to Server.get().sendToAllClients() should be replaced
    // TODO: 2024-07-11 since only players of that specific level should receive the data
    @Override
    public void notifyThatLevelIsReady(PlayerInfo playerInfo) {
        if (IS_NETWORK_DEBUG) println("OnlineServerPacketSendEvents.notifyThatLevelIsReady");

        Packet packet = new Packet(PacketType.LEVEL_IS_READY, (Object[]) null);
        playerInfo.getConnectedClient().sendPacket(packet);
    }

    @Override
    public void sendPhysicalObjectAddedToLevel(int id, PhysicalObjectType type, Vector3f position, Vector3f rotation, float scale, PlayerInfo... playerInfoArray) {
        if (IS_NETWORK_DEBUG) println("OnlineServerPacketSendEvents.sendPhysicalObjectAddedToLevel " + "{id=" + id + "} {type=" + type +"}");

        Object[] objects = { id, type, position.x, position.y, position.z, rotation.x, rotation.y, rotation.z, scale };

        Packet packet = new Packet(PacketType.PHYSICAL_OBJECT_ADDED_TO_LEVEL, objects);
        sendToGivenPlayers(packet, playerInfoArray);
    }

    @Override
    public void sendPhysicalObjectRemovedFromLevel(int id) {
        if (IS_NETWORK_DEBUG) System.out.println("OnlineServerPacketSendEvents.sendPhysicalObjectRemovedFromLevel");

        Object[] objects = { id };

        Packet packet = new Packet(PacketType.PHYSICAL_OBJECT_REMOVED_FROM_LEVEL, objects);
        Server.get().sendToAllClients(packet);
    }

    @Override
    public void sendPhysicalObjectMoved(int id, float x, float y, float z) {
        if (IS_DETAILED_NETWORK_DEBUG) println("OnlineServerPacketSendEvents.sendPhysicalObjectMoved");

        Object[] objects = { id, x, y, z };

        Packet packet = new Packet(PacketType.PHYSICAL_OBJECT_MOVED, objects);
        Server.get().sendToAllClients(packet);
    }

    @Override
    public void sendPhysicalObjectRotated(int id, float rotX, float rotY, float rotZ) {
        if (IS_DETAILED_NETWORK_DEBUG) println("OnlineServerPacketSendEvents.sendPhysicalObjectRotated");

        Object[] objects = { id, rotX, rotY, rotZ };

        Packet packet = new Packet(PacketType.PHYSICAL_OBJECT_ROTATED, objects);
        Server.get().sendToAllClients(packet);
    }

    @Override
    public void sendPhysicalObjectScaled(int id, float scale) {
        if (IS_DETAILED_NETWORK_DEBUG) println("OnlineServerPacketSendEvents.sendPhysicalObjectScaled");

        Object[] objects = { id, scale };

        Packet packet = new Packet(PacketType.PHYSICAL_OBJECT_SCALED, objects);
        Server.get().sendToAllClients(packet);
    }

    @Override
    public void sendPhysicalObjectTransformChanged(int id, Vector3f position, Vector3f rotation, float scale) {
        if (IS_DETAILED_NETWORK_DEBUG) println("OnlineServerPacketSendEvents.sendPhysicalObjectTransformChanged");

        Object[] objects = { id, position.x, position.y, position.z, rotation.x, rotation.y, rotation.z, scale };

        Packet packet = new Packet(PacketType.PHYSICAL_OBJECT_TRANSFORM_CHANGED, objects);
        Server.get().sendToAllClients(packet);
    }

    @Override
    public void sendAABBHitboxAdded(int id, String meshPath, int subMeshIdentifier, PlayerInfo... playerInfoArray) {
        if (IS_NETWORK_DEBUG) println("OnlineServerPacketSendEvents.sendAABBHitboxAdded " + "{id=" + id + "}");

        Object[] objects = { id, meshPath, subMeshIdentifier };

        Packet packet = new Packet(PacketType.AABB_HITBOX_ADDED, objects);
        sendToGivenPlayers(packet, playerInfoArray);
    }

    @Override
    public void sendAABBHitboxRemoved(int id, String meshPath, int subMeshIdentifier) {
        if (IS_NETWORK_DEBUG) System.out.println("OnlineServerPacketSendEvents.sendAABBHitboxRemoved");

        Object[] objects = { id, meshPath, subMeshIdentifier };

        Packet packet = new Packet(PacketType.AABB_HITBOX_REMOVED, objects);
        Server.get().sendToAllClients(packet);
    }

    @Override
    public void sendSphereHitboxAdded(int id, float radius, PlayerInfo... playerInfoArray) {
        if (IS_NETWORK_DEBUG) System.out.println("OnlineServerPacketSendEvents.sendSphereHitboxAdded");

        Object[] objects = { id, radius };

        Packet packet = new Packet(PacketType.SPHERE_HITBOX_ADDED, objects);
        sendToGivenPlayers(packet, playerInfoArray);
    }

    @Override
    public void sendSphereHitboxRemoved(int id, float radius) {
        if (IS_NETWORK_DEBUG) System.out.println("OnlineServerPacketSendEvents.sendSphereHitboxRemoved");

        Object[] objects = { id, radius };

        Packet packet = new Packet(PacketType.SPHERE_HITBOX_REMOVED, objects);
        Server.get().sendToAllClients(packet);
    }

    @Override
    public void sendConnectedPlayerAdded(int id, String username, int color, float x, float y, float z, float rotY, PlayerInfo playerInfo) {
        if (IS_NETWORK_DEBUG) println("OnlineServerPacketSendEvents.sendConnectedPlayerAdded " + "{id=" + id + "} to {id=" + playerInfo.getConnectedPlayer().id + "}");

        Object[] objects = { id, username, color, x, y, z, rotY };

        Packet packet = new Packet(PacketType.CONNECTED_PLAYER_ADDED, objects);
        sendToGivenPlayers(packet, playerInfo);
    }

    @Override
    public void sendConnectedPlayerTransformChanged(int id, float x, float y, float z, float rotY, PlayerInfo exceptPlayerInfo) {
        if (IS_DETAILED_NETWORK_DEBUG) println("OnlineServerPacketSendEvents.sendConnectedPlayerMoved");

        Object[] objects = { id, x, y, z, rotY };

        Packet packet = new Packet(PacketType.CONNECTED_PLAYER_TRANSFORM_CHANGED, objects);
        Server.get().sendToAllClientsExcept(exceptPlayerInfo.getConnectedClient(), packet);
    }

    @Override
    public void sendConnectedPlayerLeft(int id, PlayerInfo exceptPlayerInfo) {
        if (IS_NETWORK_DEBUG) println("OnlineServerPacketSendEvents.sendConnectedPlayerLeft " + "{id=" + id + "}");

        Object[] objects = { id };

        Packet packet = new Packet(PacketType.CONNECTED_PLAYER_LEFT, objects);
        Server.get().sendToAllClientsExcept(exceptPlayerInfo.getConnectedClient(), packet);
    }

    @Override
    public void sendLevelSecondsPerGameTickChanged(float secondsPerGameTick) {
        if (IS_NETWORK_DEBUG) println("OnlineServerPacketSendEvents.sendLevelSecondsPerGameTickChanged");

        Object[] objects = { secondsPerGameTick };

        Packet packet = new Packet(PacketType.LEVEL_SECONDS_PER_GAME_TICK_CHANGED, objects);
        Server.get().sendToAllClients(packet);
    }

    @Override
    public void sendLevelGravityChanged(float x, float y, float z) {
        if (IS_NETWORK_DEBUG) println("OnlineServerPacketSendEvents.sendLevelGravityChanged");

        Object[] objects = { x, y, z };

        Packet packet = new Packet(PacketType.LEVEL_GRAVITY_CHANGED, objects);
        Server.get().sendToAllClients(packet);
    }

    @Override
    public void confirmChatMessageReception(PlayerInfo playerInfo, int localMessageId) {
        if (IS_NETWORK_DEBUG) println("OnlineServerPacketSendEvents.confirmChatMessageReception");

        Object[] objects = { localMessageId };

        Packet packet = new Packet(PacketType.CONFIRM_CHAT_MESSAGE_RECEPTION, objects);
        playerInfo.getConnectedClient().sendPacket(packet);
    }

    @Override
    public void broadcastChatMessage(int playerId, String message, PlayerInfo exceptPlayerInfo) {
        if (IS_NETWORK_DEBUG) println("OnlineServerPacketSendEvents.broadcastChatMessage");

        Object[] objects = { playerId, message };

        Packet packet = new Packet(PacketType.BROADCASTING_CHAT_MESSAGE, objects);
        Server.get().sendToAllClientsExcept(exceptPlayerInfo.getConnectedClient(), packet);
    }

    @Override
    public void broadcastSystemMessage(String message) {
        if (IS_NETWORK_DEBUG) println("OnlineServerPacketSendEvents.broadcastSystemMessage");

        Object[] objects = { message };

        Packet packet = new Packet(PacketType.BROADCASTING_SYSTEM_MESSAGE, objects);
        Server.get().sendToAllClients(packet);
    }

    @Override
    public void sendVirtualLightAddedToLevel(int id, Vector3f position, Vector3f color, Vector3f attenuation, PlayerInfo... playerInfoArray) {
        if (IS_NETWORK_DEBUG) System.out.println("OnlineServerPacketSendEvents.sendVirtualLightAddedToLevel");

        Object[] objects = { id, position, color, attenuation };

        Packet packet = new Packet(PacketType.VIRTUAL_LIGHT_ADDED, objects);
        sendToGivenPlayers(packet, playerInfoArray);
    }

    @Override
    public void sendVirtualLightRemovedFromLevel(int id) {
        if (IS_NETWORK_DEBUG) System.out.println("OnlineServerPacketSendEvents.sendVirtualLightRemovedFromLevel");

        Object[] objects = { id };

        Packet packet = new Packet(PacketType.VIRTUAL_LIGHT_REMOVED, objects);
        Server.get().sendToAllClients(packet);
    }

    @Override
    public void sendVirtualLightMoved(int id, float x, float y, float z) {
        if (IS_NETWORK_DEBUG) System.out.println("OnlineServerPacketSendEvents.sendVirtualLightMoved");

        Object[] objects = { id, x, y, z };

        Packet packet = new Packet(PacketType.VIRTUAL_LIGHT_MOVED, objects);
        Server.get().sendToAllClients(packet);
    }

    @Override
    public void sendVirtualLightColorChanged(int id, float r, float g, float b) {
        if (IS_NETWORK_DEBUG) System.out.println("OnlineServerPacketSendEvents.sendVirtualLightColorChanged");

        Object[] objects = { id, r, g, b };

        Packet packet = new Packet(PacketType.VIRTUAL_LIGHT_COLOR_CHANGED, objects);
        Server.get().sendToAllClients(packet);
    }

    @Override
    public void sendVirtualLightAttenuationChanged(int id, float att1, float att2, float att3) {
        if (IS_NETWORK_DEBUG) System.out.println("OnlineServerPacketSendEvents.sendVirtualLightAttenuationChanged");

        Object[] objects = { id, att1, att2, att3 };

        Packet packet = new Packet(PacketType.VIRTUAL_LIGHT_ATTENUATION_CHANGED, objects);
        Server.get().sendToAllClients(packet);
    }

    @Override
    public void sendVirtualLightPropertiesChanged(int id, Vector3f position, Vector3f color, Vector3f attenuation) {
        if (IS_NETWORK_DEBUG) System.out.println("OnlineServerPacketSendEvents.sendVirtualLightPropertiesChanged");

        Object[] objects = { id, position, color, attenuation };

        Packet packet = new Packet(PacketType.VIRTUAL_LIGHT_PROPERTIES_CHANGED, objects);
        Server.get().sendToAllClients(packet);
    }

    @Override
    public void sendVirtualDirectionalLightAddedToLevel(int id, Vector3f toLightDirection, Vector3f color, PlayerInfo... playerInfoArray) {
        if (IS_NETWORK_DEBUG) System.out.println("OnlineServerPacketSendEvents.sendVirtualDirectionalLightAddedToLevel");

        Object[] objects = { id, toLightDirection, color };

        Packet packet = new Packet(PacketType.VIRTUAL_DIRECTIONAL_LIGHT_ADDED, objects);
        sendToGivenPlayers(packet, playerInfoArray);
    }

    @Override
    public void sendVirtualDirectionalLightRemovedFromLevel(int id) {
        if (IS_NETWORK_DEBUG) System.out.println("OnlineServerPacketSendEvents.sendVirtualDirectionalLightRemovedFromLevel");

        Object[] objects = { id };

        Packet packet = new Packet(PacketType.VIRTUAL_DIRECTIONAL_LIGHT_REMOVED, objects);
        Server.get().sendToAllClients(packet);
    }

    @Override
    public void sendVirtualDirectionalLightToLightDirectionChanged(int id, float x, float y, float z) {
        if (IS_NETWORK_DEBUG) System.out.println("OnlineServerPacketSendEvents.sendVirtualDirectionalLightToLightDirectionChanged");

        Object[] objects = { id, x, y, z };

        Packet packet = new Packet(PacketType.VIRTUAL_DIRECTIONAL_LIGHT_DIRECTION_CHANGED, objects);
        Server.get().sendToAllClients(packet);
    }

    @Override
    public void sendVirtualDirectionalLightColorChanged(int id, float r, float g, float b) {
        if (IS_NETWORK_DEBUG) System.out.println("OnlineServerPacketSendEvents.sendVirtualDirectionalLightColorChanged");

        Object[] objects = { id, r, g, b };

        Packet packet = new Packet(PacketType.VIRTUAL_DIRECTIONAL_LIGHT_COLOR_CHANGED, objects);
        Server.get().sendToAllClients(packet);
    }

    @Override
    public void sendVirtualDirectionalLightPropertiesChanged(int id, Vector3f toLightDirection, Vector3f color) {
        if (IS_NETWORK_DEBUG) System.out.println("OnlineServerPacketSendEvents.sendVirtualDirectionalLightPropertiesChanged");

        Object[] objects = { id, toLightDirection, color };

        Packet packet = new Packet(PacketType.VIRTUAL_DIRECTIONAL_LIGHT_PROPERTIES_CHANGED, objects);
        Server.get().sendToAllClients(packet);
    }

    @Override
    public void sendSkyboxChanged(String name, boolean unmoving, PlayerInfo... playerInfoArray) {
        if (IS_NETWORK_DEBUG) System.out.println("OnlineServerPacketSendEvents.sendSkyboxChanged");

        Object[] objects = { name, unmoving };

        Packet packet = new Packet(PacketType.SKYBOX_CHANGED, objects);
        sendToGivenPlayers(packet, playerInfoArray);
    }

    @Override
    public void sendPlaySoundAtPhysicalObject(Sound sound, int id) {
        if (IS_NETWORK_DEBUG) System.out.println("OnlineServerPacketSendEvents.sendPlaySoundAtPhysicalObject");

        Object[] objects = { sound, id };

        Packet packet = new Packet(PacketType.PLAY_SOUND_AT_PHYSICAL_OBJECT, objects);
        Server.get().sendToAllClients(packet);
    }

    @Override
    public void sendPlaySoundAtPosition(Sound sound, Vector3f position) {
        if (IS_NETWORK_DEBUG) System.out.println("OnlineServerPacketSendEvents.sendPlaySoundAtPosition");

        Object[] objects = { sound, position };

        Packet packet = new Packet(PacketType.PLAY_SOUND_AT_POSITION, objects);
        Server.get().sendToAllClients(packet);
    }

    @Override
    public void sendCreateSoundEmitter(int customIdentifier, float x, float y, float z, PlayerInfo... playerInfoArray) {
        if (IS_NETWORK_DEBUG) System.out.println("OnlineServerPacketSendEvents.sendCreateSoundEmitter");

        Object[] objects = { customIdentifier, x, y, z };

        Packet packet = new Packet(PacketType.CREATE_SOUND_EMITTER, objects);
        sendToGivenPlayers(packet, playerInfoArray);
    }

    @Override
    public void sendDestroySoundEmitter(int customIdentifier) {
        if (IS_NETWORK_DEBUG) System.out.println("OnlineServerPacketSendEvents.sendDestroySoundEmitter");

        Object[] objects = { customIdentifier };

        Packet packet = new Packet(PacketType.DESTROY_SOUND_EMITTER, objects);
        Server.get().sendToAllClients(packet);
    }

    @Override
    public void sendPlaySoundAtSoundEmitter(Sound sound, int customIdentifier) {
        if (IS_NETWORK_DEBUG) System.out.println("OnlineServerPacketSendEvents.sendPlaySoundAtSoundEmitter");

        Object[] objects = { sound, customIdentifier };

        Packet packet = new Packet(PacketType.PLAY_SOUND_AT_SOUND_EMITTER, objects);
        Server.get().sendToAllClients(packet);
    }

    @Override
    public void sendUpdatePositionOfSoundEmitter(int customIdentifier, float x, float y, float z) {
        if (IS_NETWORK_DEBUG) System.out.println("OnlineServerPacketSendEvents.sendUpdatePositionOfSoundEmitter");

        Object[] objects = { customIdentifier, x, y, z };

        Packet packet = new Packet(PacketType.UPDATE_POSITION_OF_SOUND_EMITTER, objects);
        Server.get().sendToAllClients(packet);
    }

    private void sendToGivenPlayers(Packet packet, PlayerInfo... playerInfoArray) {
        if (playerInfoArray.length == 0) {
            Server.get().sendToAllClients(packet);
        } else {
            for (PlayerInfo playerInfo : playerInfoArray) {
                playerInfo.getConnectedClient().sendPacket(packet);
            }
        }
    }

}
