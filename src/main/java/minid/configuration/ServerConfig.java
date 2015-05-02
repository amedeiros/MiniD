package minid.configuration;

/**
 * Created by Andrew Medeiros on 4/30/15.
 */
public class ServerConfig {
    /**
     * Host to run the server on
     */
    private String host = "localhost";

    /**
     * Port for the server to run on
     */
    private int port = 6667;

    /**
     * @param host Set the host
     */
    public void setHost(String host) { this.host = host; }

    /**
     * @return Return the host
     */
    public String getHost() { return host; }

    /**
     * @param port Set the port
     */
    public void setPort(int port) { this.port = port; }

    /**
     * @return Return the port
     */
    public int getPort() { return port; }
}
