package minid.configuration

/**
 * Configuration for the server.
 */
class ServerConfig {
    /**
     * Run SSL
     */
    private boolean ssl = false

    /**
     * Host to run the server on
     */
    private String host = 'localhost'

    /**
     * Port for the server to run on
     */
    private int port = 6667

    /**
     * @param host Set the host
     */
    void setHost(String host) {
        this.host = host
    }

    /**
     * @return Return the host
     */
    String getHost() {
        host
    }

    /**
     * @param port Set the port
     */
    void setPort(int port) {
        this.port = port
    }

    /**
     * @return Return the port
     */
    int getPort() {
        port
    }

    /**
     * @param ssl
     */
    void setSsl(boolean ssl) {
        this.ssl = ssl
    }

    /**
     * @return Return if SSL is true
     */
    boolean getSsl() {
        ssl
    }
}
