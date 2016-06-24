package minid

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import java.util.concurrent.TimeUnit

/**
 * Ping Pong!
 */
class PingPong implements Runnable {
    private static final Logger LOG = LogManager.getLogger(PingPong)

    private final Connection connection

    /**
     * Default constructor for ping pong.
     * @param connection The connection to ping.
     */
    PingPong(Connection connection) {
        this.connection = connection
    }

    @Override
    void run() {
        while (connection.active()) {
            try {
                connection.send('PING 0')
                TimeUnit.SECONDS.sleep(30)
            } catch (InterruptedException e) {
                LOG.warn(e)
            } finally {
                if (!connection.isPlayingPingPong) {
                    connection.close()
                }
            }
        }
    }
}
