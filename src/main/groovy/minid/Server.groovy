package minid

import minid.configuration.ServerConfig

import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.LogManager

import javax.net.ssl.SSLServerSocketFactory

/**
 * The MiniD socket server.
 */
class Server implements Runnable {
    private static final Logger LOG = LogManager.getLogger(Server)

    /**
     * The socket server.
     */
    private final ServerSocket serverSocket

    /**
     * Configuration for the server
     */
    private final ServerConfig serverConfig

    /**
     * Default constructor for the server.
     * @param serverConfig
     * @throws Exception
     */
    Server(ServerConfig serverConfig) throws Exception {
        setDefaultChannel()
        this.serverConfig = serverConfig

        if (serverConfig.ssl) {
            this.serverSocket = SSLServerSocketFactory.default.createServerSocket(serverConfig.port)
        } else {
            this.serverSocket = new ServerSocket(serverConfig.port)
        }
    }

    @Override
    void run() {
        while (serverSocket.isBound()) {
            try {
                Socket socket = serverSocket.accept()
                new Thread(new Connection(socket)).start()
            } catch (IOException e) {
                LOG.error('Server died!', e)
                break
            }
        }
    }

    /**
     * Create a default channel.
     */
    private static void setDefaultChannel() {
        Channel minid = new Channel()
        minid.setTopic('Default channel for MiniD')
        minid.setName('#MiniD')
        Channels.globalChannels[minid.name] = minid
    }
}
