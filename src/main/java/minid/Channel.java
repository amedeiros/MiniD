package minid;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Andrew Medeiros on 5/1/15.
 */
public class Channel {
    private static ConcurrentHashMap<String, Channel> globalChannels = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Connection> members = new ConcurrentHashMap<>();
    private String topic;
    private String name;

    public static ConcurrentHashMap<String, Channel> getGlobalChannels() { return globalChannels; }

    public void setTopic(String topic) { this.topic = topic; }
    public String getTopic() { return topic; }

    public void setName(String name) { this.name = name; }
    public String getName() { return name; }

    public boolean containsMember(String memeber) { return members.containsKey(memeber); }

    public void addMember(Connection connection) {
        members.put(connection.getUserConfig().getNick(), connection);
        msgMembers(":" + connection.getRepresentation() + " JOIN " + name); // Let everyone know you have joined
        sendTopic(connection);
        doList(connection);
    }

    public void sendTopic(Connection connection) {
        if (getTopic() == null)
            connection.send("332 " + connection.getUserConfig().getNick() +  " " + name + " :No topic is set");
        else
            connection.send("332 " + connection.getUserConfig().getNick() + " " + name + " :" + topic);
    }

    public void msgMembers(String message, Connection userTalking) {
        for (Connection connection : members.values())
            if (!userTalking.getUserConfig().getNick().equals(connection.getUserConfig().getNick()))
                connection.send(message);
    }

    public void msgMembers(String message) {
        for (Connection connection : members.values())
            connection.send(message);
    }

    public void doList(Connection connection) {
        for (Connection channelMember : members.values())
            connection.send("353 " + connection.getUserConfig().getNick() + " = " + name + " :" + channelMember.getUserConfig().getNick());

        connection.send("366 " + connection.getUserConfig().getNick() + " " + name + " :End of /NAMES list");
    }
}
