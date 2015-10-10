package minid;

import minid.configuration.UserConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Andrew Medeiros on 4/30/15.
 */
public class Connection implements Runnable {
    public boolean isPlayingPingPong = true;

    private Socket         socket;
    private Thread         pingPongThread;
    private UserConfig     userConfig = new UserConfig();
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;

    private static final Logger LOG = LogManager.getLogger(Server.class);
    private static ConcurrentHashMap<String, Connection> globalConnections = new ConcurrentHashMap<>();
    public static String globalServerName = "Programming-Mother-Fucker";

    /**
     * @param socket Socket to run in a separate thread
     */
    public Connection(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()), 8192 * 2);
        } catch (IOException e) { e.printStackTrace(); }
    }

    /**
     * @return ConncurrentHashMap of all the connections connected to this server.
     */
    public static ConcurrentHashMap<String, Connection> getGlobalConnections() { return globalConnections; }

    /**
     * Run this connection thread
     */
    @Override
    public void run() {
        // Also run a ping thread probably a better way to do this.
        this.pingPongThread = new Thread(new PingPong(this));
        pingPongThread.start();

        while (socket.isConnected()) {
            try {
                String line = bufferedReader.readLine();
                if (!line.isEmpty()) parse(line);
            } catch (IOException e) { break; }
        }

        close();
    }

    /**
     * Active if the socket is connected and the client is responding to ping requests.
     * @return
     */
    public boolean  active() { return socket.isConnected() && isPlayingPingPong; }

    /**
     * @return Return the users configuration
     */
    public UserConfig getUserConfig() { return userConfig; }

    /**
     * Get the connections host
     * @return String hostname
     */
    public String getHost() { return socket.getLocalAddress().getCanonicalHostName(); }

    /**
     * Part all channels
     * Remove connection from global connection
     * Interrupt the Ping Pong thread
     * Close the buffered writer and reader
     * Finally close the socket
     */
    public void close() {
        try {
            userConfig.partAllChannels();
            globalConnections.remove(userConfig.getNick());
            pingPongThread.interrupt();
            bufferedWriter.close();
            bufferedReader.close();
            socket.close();
            if (!Thread.currentThread().isInterrupted()) Thread.currentThread().interrupt();
        } catch (IOException e) { e.printStackTrace(); }
    }

    /**
     * Send a message to the client
     * @param message String message to send to the client
     */
    public void send(String message) {
        try {
            bufferedWriter.write(message + "\r\n");
            bufferedWriter.flush();
            LOG.info(">> " + message);
        } catch (IOException e) { LOG.debug(e); }
    }

    /**
     * Send the client a notice message.
     * @param message String message to send to the client
     */
    public void sendNotice(String message) {
        send(":" + globalServerName + " NOTICE " + userConfig.getNick() + " :" + message);
    }

    /**
     * @return String of the nicks host mask
     */
    public String getRepresentation() {
        return userConfig.getNick() + "!" + userConfig.getUsername() + "@" + getHost();
    }

    public void sendGlobal(String message) { send(":" + globalServerName + " " + message); }

    /**
     * Respond with a 401 that the nick or channel do not exist
     */
    public void noSuchNickChannel() { send("401 " + getUserConfig().getNick() + " :No such nick/channel"); }

    /**
     * @param line Line to run a command against
     */
    private void parse(String line) {
        LOG.info("<< " + line);
        String[] arguments = line.split(" ");
        if (line.startsWith("NICK"))         Commands.NICK.run(this, arguments);
        else if (line.startsWith("USER"))    Commands.USER.run(this, arguments);
        else if (line.startsWith("PRIVMSG")) Commands.PRIVMSG.run(this, arguments);
        else if (line.startsWith("QUIT"))    Commands.QUIT.run(this, arguments);
        else if (line.startsWith("WHOIS"))   Commands.WHOIS.run(this, arguments);
        else if (line.startsWith("JOIN"))    Commands.JOIN.run(this, arguments);
        else if (line.startsWith("MODE"))    Commands.MODE.run(this, arguments);
        else if (line.startsWith("VERSION")) Commands.VERSION.run(this, arguments);
        else if (line.startsWith("TOPIC"))   Commands.TOPIC.run(this, arguments);
        else if (line.startsWith("PONG"))    Commands.PONG.run(this, arguments);
        else if (line.startsWith("NAMES"))   Commands.NAMES.run(this, arguments);
        else Commands.UNKNOWNCOMMAND.run(this, arguments);
    }
}
