package templates.serverSide.communication;

import templates.serverSide.PlayerInfo;

/**
 * Contains an unimplemented method for what should happen when a player joins the server. This method
 * gets called from ServerPacketReceiveActions. Of course, the contents of this method depend on whether
 * the server is local or online.
 */
public interface ServerProperties {
    void assignPlayerInfo(PlayerInfo playerInfo);
}
