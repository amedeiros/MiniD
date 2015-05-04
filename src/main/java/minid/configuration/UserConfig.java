package minid.configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Andrew Medeiros on 4/30/15.
 */
public class UserConfig {
    /**
     * Thread safe ArrayList
     */
    private List<String> channelList = Collections.synchronizedList(new ArrayList<>());

    /**
     * Username for authentication with the server
     */
    private String username;

    /**
     * Nick to register with the server
     */
    private String nick;

    /**
     * Current channels this user belongs to
     * @return
     */
    public List<String> getChannelList() { return channelList; }

    /**
     * @param username Set the username
     */
    public void setUsername(String username) { this.username = username; }

    /**
     * @return Get the username
     */
    public String getUsername() { return username; }

    /**
     * @param nick Set the nick
     */
    public void setNick(String nick) { this.nick = nick; }

    /**
     * @return Get the nick
     */
    public String getNick() { return nick; }

    /**
     * Part all channels
     */
    public void partAllChannels() {

    }
}
