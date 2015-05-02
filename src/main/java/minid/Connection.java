package minid;

import minid.configuration.UserConfig;

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

    private static ConcurrentHashMap<String, Connection> globalConnections = new ConcurrentHashMap<>();
    public static String globalServerName = "Programming-Mother-Fucker";

    public Connection(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()), 8192 * 2);
        } catch (IOException e) { e.printStackTrace(); }
    }

    public static ConcurrentHashMap<String, Connection> getGlobalConnections() { return globalConnections; }

    public void run() {
        // Also run a ping thread probably a better way to do this.
        this.pingPongThread = new Thread(new PingPong(this));
        pingPongThread.start();

        while (socket.isConnected()) {
            try {
                String line = bufferedReader.readLine();
                if (!line.isEmpty()) parse(line);
            } catch (IOException e) { e.printStackTrace(); }
        }

        close();
    }

    public boolean  active() { return socket.isConnected() && isPlayingPingPong; }

    public UserConfig getUserConfig() { return userConfig; }

    public String getHost() { return socket.getLocalAddress().getCanonicalHostName(); }

    public void close() {
        try {
            bufferedWriter.close();
            bufferedReader.close();
            socket.close();
            globalConnections.remove(userConfig.getNick());
            pingPongThread.interrupt();
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void send(String message) {
        try {
            bufferedWriter.write(message + "\r\n");
            bufferedWriter.flush();
            System.out.println(">> " + message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendNotice(String message) {
        send(":" + globalServerName + " NOTICE " + userConfig.getNick() + " :" + message);
    }

    public String getRepresentation() {
        return userConfig.getNick() + "!" + userConfig.getUsername() + "@" + getHost();
    }

    public void sendGlobal(String message) {
        send(":" + globalServerName + " " + message); }

    private void parse(String line) {
        System.out.println("<< " + line);
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
        else Commands.UNKNOWNCOMMAND.run(this, arguments);
    }
}
