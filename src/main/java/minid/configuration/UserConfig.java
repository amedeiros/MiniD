package minid.configuration;

/**
 * Created by Andrew Medeiros on 4/30/15.
 */
public class UserConfig {
    /**
     * Username for authentication with the server
     */
    private String username;

    /**
     * Nick to register with the server
     */
    private String nick;

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
}
