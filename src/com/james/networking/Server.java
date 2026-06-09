package com.james.networking;

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
    private volatile boolean shouldRun = true;

    public Server(int port) throws IOException {
        this.server = new ServerSocket(port, 100);
        new Thread(this).start();

        instance = this;
    }

    @Override
    public void run() {
        try {
            while (shouldRun) {
                Socket connection = server.accept();
                ConnectedClient connectedClient = new ConnectedClient(this, connection);
                synchronized (connectedClients) {
                    connectedClients.add(connectedClient);
                }

                connectedClientAddedListener.accept(connectedClient);
            }
        } catch (IOException e) {
            if (shouldRun) {
                e.printStackTrace();
            }
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

    public void disconnect() throws IOException {
        shouldRun = false;

        synchronized (connectedClients) {
            for (ConnectedClient connectedClient : connectedClients) {
                connectedClient.disconnect();
            }
        }

        server.close();
    }

    public void sendToAllClients(Packet packet) {
        synchronized (connectedClients) {
            for (ConnectedClient connectedClient : connectedClients) {
                connectedClient.sendPacket(packet);
            }
        }
    }

    public void sendToAllClientsExcept(ConnectedClient except, Packet packet) {
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
