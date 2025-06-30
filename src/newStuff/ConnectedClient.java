package newStuff;

import com.james.common.networking.Packet;
import templates.common.networking.PacketType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class ConnectedClient implements Runnable {

    private final Server serverInstance;
    private final Socket connection;
    private final ObjectOutputStream output;
    private final ObjectInputStream input;

    // TODO: 2024-07-07 make sure any collections accessed from different threads are synchronized!
    private final Map<PacketType, BiConsumer<ConnectedClient, Object[]>> readListeners = new HashMap<>();
    private volatile BiConsumer<Exception, ConnectedClient> onClientDisconnectListener;

    public ConnectedClient(Server serverInstance, Socket connection) throws IOException {
        this.serverInstance = serverInstance;
        this.connection = connection;
        this.output = new ObjectOutputStream(connection.getOutputStream());
        this.output.flush();
        this.input = new ObjectInputStream(connection.getInputStream());

        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        try {
            while (true) {
                Packet packet = (Packet) input.readObject();

                BiConsumer<ConnectedClient, Object[]> listener;
                synchronized (readListeners) {
                    listener = readListeners.get(packet.type);
                }

                if (listener == null)
                    throw new RuntimeException("A listener was not set up! " + packet.type);

                listener.accept(this, packet.data);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();

            serverInstance.removeConnectedClient(this);
            disconnect();

            onClientDisconnectListener.accept(e, this);
        }
    }

    public void setReadListener(PacketType type, BiConsumer<ConnectedClient, Object[]> listener) {
        synchronized (readListeners) {
            readListeners.put(type, listener);
        }
    }

    public void setDisconnectListener(BiConsumer<Exception, ConnectedClient> listener) {
        onClientDisconnectListener = listener;
    }

    public void sendPacket(Packet packet) {
        try {
            output.writeObject(packet);
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            output.close();
            output.flush();
            input.close();
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
