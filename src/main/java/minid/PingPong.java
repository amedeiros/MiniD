package minid;

import java.util.concurrent.TimeUnit;

/**
 * Created by Andrew Medeiros on 5/1/15.
 */
public class PingPong implements Runnable {
    private Connection connection;
    public PingPong(Connection connection) { this.connection = connection; }

    public void run() {
        while(connection.active()) {
            try {
                connection.send("PING 0");
                TimeUnit.SECONDS.sleep(30);
            } catch (InterruptedException e) {
                // NO-OP
            } finally {
                if (!connection.isPlayingPingPong) {
                    connection.close();
                }
            }
        }
    }
}
