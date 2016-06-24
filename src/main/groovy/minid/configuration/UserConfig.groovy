package minid.configuration

import minid.Channel

/**
 * User configuration.
 */
class UserConfig {
    /**
     * Thread safe ArrayList
     */
    private final List<Channel> channelList = [].asSynchronized()

    /**
     * Username for authentication with the server
     */
    private String username

    /**
     * Nick to register with the server
     */
    private String nick

    /**
     * Add a channel name to the users channel list.
     * @param channel
     */
    void addToChannelList(Channel channel) {
        channelList << channel
    }

    /**
     * Current channels this user belongs to
     * @return
     */
    List<Channel> getChannelList() {
        channelList
    }

    /**
     * @param username Set the username
     */
    void setUsername(String username) {
        this.username = username
    }

    /**
     * @return Get the username
     */
    String getUsername() {
        username
    }

    /**
     * @param nick Set the nick
     */
    void setNick(String nick) {
        this.nick = nick
    }

    /**
     * @return Get the nick
     */
    String getNick() {
        nick
    }

    /**
     * Part all channels
     */
    void partAllChannels() {
        channelList*.removeMember(nick)
    }
}
