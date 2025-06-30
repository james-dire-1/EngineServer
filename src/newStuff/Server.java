package newStuff;

import com.james.common.networking.Packet;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Server implements Runnable {

    private final ServerSocket server;
    private final List<ConnectedClient> connectedClients = new ArrayList<>();

    private Consumer<ConnectedClient> connectedClientAddedListener;

    public Server(int port) throws IOException {
        server = new ServerSocket(port, 100);
        new Thread(this).start();

        instance = this;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Socket connection = server.accept();
                ConnectedClient connectedClient = new ConnectedClient(this, connection);
                synchronized (connectedClients) {
                    connectedClients.add(connectedClient);
                }

                connectedClientAddedListener.accept(connectedClient);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setConnectedClientAddedListener(Consumer<ConnectedClient> listener) {
        this.connectedClientAddedListener = listener;
    }

    public void removeConnectedClient(ConnectedClient connectedClient) {
        synchronized (connectedClients) {
            connectedClients.remove(connectedClient);
        }
    }

    public void disconnect() {
        synchronized (connectedClients) {
            for (ConnectedClient connectedClient : connectedClients) {
                connectedClient.disconnect();
            }
        }
    }

    public void sendToAllClients(Packet packet) {
        synchronized (connectedClients) {
            for (ConnectedClient connectedClient : connectedClients) {
                connectedClient.sendPacket(packet);
            }
        }
    }

    public void sendToAllOtherClientsExcept(ConnectedClient except, Packet packet) {
        synchronized (connectedClients) {
            for (ConnectedClient connectedClient : connectedClients) {
                if (!connectedClient.equals(except)) {
                    connectedClient.sendPacket(packet);
                }
            }
        }
    }

    private static Server instance;
    public static Server get() { return instance; }

}
