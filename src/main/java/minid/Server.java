package minid;

import minid.configuration.ServerConfig;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Andrew Medeiros on 4/30/15.
 */
public class Server implements Runnable {
    private static final Logger LOG = LogManager.getLogger(Server.class);
    private ServerSocket serverSocket;

    /**
     * Configuration for the server
     */
    private ServerConfig serverConfig;

    public Server(ServerConfig serverConfig) throws Exception {
        // Create a default channel
        Channel minid = new Channel();
        minid.setTopic("Default channel for MiniD");
        minid.setName("#MiniD");
        Channels.getGlobalChannels().put(minid.getName(), minid);

        this.serverConfig = serverConfig;
        this.serverSocket = new ServerSocket(serverConfig.getPort());
    }

    public void run() {
        while (serverSocket.isBound()) {
            try {
                Socket socket = serverSocket.accept();
                new Thread(new Connection(socket)).start();
            } catch (Exception e) {
                LOG.error("Server died!", e);
                break;
            }
        }
    }
}
