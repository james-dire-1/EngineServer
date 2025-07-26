package newStuff;

import com.james.common.networking.Packet;
import templates.common.networking.PacketType;
import com.james.common.simulation.objects.PhysicalObjectType;
import com.james.serverSide.PlayerInfo;
import org.lwjgl.util.vector.Vector3f;
import templates.serverSide.communication.ServerPacketSendEvents;

import static templates.common.GlobalConstants.IS_DETAILED_NETWORK_DEBUG;
import static templates.common.GlobalConstants.IS_NETWORK_DEBUG;
import static newStuff.Logger.log;

public class OnlineServerPacketSendEvents implements ServerPacketSendEvents {

    @Override
    public void sendUsernamePrompt(PlayerInfo playerInfo) {
        if (IS_NETWORK_DEBUG) log("OnlineServerPacketSendEvents.promptUsername");

        Packet packet = new Packet(PacketType.USERNAME_PROMPT, (Object[]) null);
        playerInfo.getConnectedClient().sendPacket(packet);
    }

    @Override
    public void notifyUsernameSuccess(PlayerInfo playerInfo, String username, int color) {
        if (IS_NETWORK_DEBUG) log("OnlineServerPacketSendEvents.notifyUsernameSuccess");

        Object[] objects = { username, color };

        Packet packet = new Packet(PacketType.USERNAME_SUCCESS, objects);
        playerInfo.getConnectedClient().sendPacket(packet);
    }

    // TODO: 2024-07-11 In the future, all calls to Server.get().sendToAllClients() should be replaced
    // TODO: 2024-07-11 since only players of that specific level should receive the data
    @Override
    public void notifyThatLevelIsReady(PlayerInfo playerInfo) {
        if (IS_NETWORK_DEBUG) log("OnlineServerPacketSendEvents.notifyThatLevelIsReady");

        Packet packet = new Packet(PacketType.LEVEL_IS_READY, (Object[]) null);
        playerInfo.getConnectedClient().sendPacket(packet);
    }

    @Override
    public void sendPhysicalObjectAddedToLevel(int id, PhysicalObjectType type, Vector3f position, Vector3f rotation, float scale, PlayerInfo... playerInfoArray) {
        if (IS_NETWORK_DEBUG) log("OnlineServerPacketSendEvents.sendPhysicalObjectAddedToLevel " + "{id=" + id + "} {type=" + type +"}");

        Object[] objects = { id, type, position.x, position.y, position.z, rotation.x, rotation.y, rotation.z, scale };

        Packet packet = new Packet(PacketType.PHYSICAL_OBJECT_ADDED_TO_LEVEL, objects);
        sendToGivenPlayers(packet, playerInfoArray);
    }

    @Override
    public void sendPhysicalObjectMoved(int id, float x, float y, float z) {
        if (IS_DETAILED_NETWORK_DEBUG) log("OnlineServerPacketSendEvents.sendPhysicalObjectMoved");

        Object[] objects = { id, x, y, z };

        Packet packet = new Packet(PacketType.PHYSICAL_OBJECT_MOVED, objects);
        Server.get().sendToAllClients(packet);
    }

    @Override
    public void sendPhysicalObjectRotated(int id, float rotX, float rotY, float rotZ) {
        if (IS_DETAILED_NETWORK_DEBUG) log("OnlineServerPacketSendEvents.sendPhysicalObjectRotated");

        Object[] objects = { id, rotX, rotY, rotZ };

        Packet packet = new Packet(PacketType.PHYSICAL_OBJECT_ROTATED, objects);
        Server.get().sendToAllClients(packet);
    }

    @Override
    public void sendPhysicalObjectScaled(int id, float scale) {
        if (IS_DETAILED_NETWORK_DEBUG) log("OnlineServerPacketSendEvents.sendPhysicalObjectScaled");

        Object[] objects = { id, scale };

        Packet packet = new Packet(PacketType.PHYSICAL_OBJECT_SCALED, objects);
        Server.get().sendToAllClients(packet);
    }

    @Override
    public void sendPhysicalObjectTransformChanged(int id, Vector3f position, Vector3f rotation, float scale) {
        if (IS_DETAILED_NETWORK_DEBUG) log("OnlineServerPacketSendEvents.sendPhysicalObjectTransformChanged");

        Object[] objects = { id, position.x, position.y, position.z, rotation.x, rotation.y, rotation.z, scale };

        Packet packet = new Packet(PacketType.PHYSICAL_OBJECT_TRANSFORM_CHANGED, objects);
        Server.get().sendToAllClients(packet);
    }

    @Override
    public void sendAABBHitboxAdded(int id, String meshPath, PlayerInfo... playerInfoArray) {
        if (IS_NETWORK_DEBUG) log("OnlineServerPacketSendEvents.sendAABBHitboxAdded " + "{id=" + id + "}");

        Object[] objects = { id, meshPath };

        Packet packet = new Packet(PacketType.AABB_HITBOX_ADDED, objects);
        sendToGivenPlayers(packet, playerInfoArray);
    }

    @Override
    public void sendConnectedPlayerAdded(int id, String username, int color, float x, float y, float z, float rotY, PlayerInfo playerInfo) {
        if (IS_NETWORK_DEBUG) log("OnlineServerPacketSendEvents.sendConnectedPlayerAdded " + "{id=" + id + "} to {id=" + playerInfo.getConnectedPlayer().id + "}");

        Object[] objects = { id, username, color, x, y, z, rotY };

        Packet packet = new Packet(PacketType.CONNECTED_PLAYER_ADDED, objects);
        sendToGivenPlayers(packet, playerInfo);
    }

    @Override
    public void sendConnectedPlayerTransformChanged(int id, float x, float y, float z, float rotY, PlayerInfo exceptPlayerInfo) {
        if (IS_DETAILED_NETWORK_DEBUG) log("OnlineServerPacketSendEvents.sendConnectedPlayerMoved");

        Object[] objects = { id, x, y, z, rotY };

        Packet packet = new Packet(PacketType.CONNECTED_PLAYER_TRANSFORM_CHANGED, objects);
        Server.get().sendToAllOtherClientsExcept(exceptPlayerInfo.getConnectedClient(), packet);
    }

    @Override
    public void sendConnectedPlayerLeft(int id, PlayerInfo exceptPlayerInfo) {
        if (IS_NETWORK_DEBUG) log("OnlineServerPacketSendEvents.sendConnectedPlayerLeft " + "{id=" + id + "}");

        Object[] objects = { id };

        Packet packet = new Packet(PacketType.CONNECTED_PLAYER_LEFT, objects);
        Server.get().sendToAllOtherClientsExcept(exceptPlayerInfo.getConnectedClient(), packet);
    }

    @Override
    public void sendLevelSecondsPerGameTickChanged(float secondsPerGameTick) {
        if (IS_NETWORK_DEBUG) log("OnlineServerPacketSendEvents.sendLevelSecondsPerGameTickChanged");

        Object[] objects = { secondsPerGameTick };

        Packet packet = new Packet(PacketType.LEVEL_SECONDS_PER_GAME_TICK_CHANGED, objects);
        Server.get().sendToAllClients(packet);
    }

    @Override
    public void sendLevelGravityChanged(float x, float y, float z) {
        if (IS_NETWORK_DEBUG) log("OnlineServerPacketSendEvents.sendLevelGravityChanged");

        Object[] objects = { x, y, z };

        Packet packet = new Packet(PacketType.LEVEL_GRAVITY_CHANGED, objects);
        Server.get().sendToAllClients(packet);
    }

    @Override
    public void confirmChatMessageReception(PlayerInfo playerInfo, int localMessageId) {
        if (IS_NETWORK_DEBUG) log("OnlineServerPacketSendEvents.confirmChatMessageReception");

        Object[] objects = { localMessageId };

        Packet packet = new Packet(PacketType.CONFIRM_CHAT_MESSAGE_RECEPTION, objects);
        playerInfo.getConnectedClient().sendPacket(packet);
    }

    @Override
    public void broadcastChatMessage(int playerId, String message, PlayerInfo exceptPlayerInfo) {
        if (IS_NETWORK_DEBUG) log("OnlineServerPacketSendEvents.broadcastChatMessage");

        Object[] objects = { playerId, message };

        Packet packet = new Packet(PacketType.BROADCASTING_CHAT_MESSAGE, objects);
        Server.get().sendToAllOtherClientsExcept(exceptPlayerInfo.getConnectedClient(), packet);
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
