package minid;

import minid.tools.Helper;

import java.util.Arrays;

/**
 * Created by Andrew Medeiros on 4/30/15.
 */
public enum Commands implements Command {
    NICK() {
        @Override
        public void run(Connection connection, String[] arguments) { checkAndSetNick(arguments[1], connection); }

        private void checkAndSetNick(String nick, Connection connection) {
            if (Connection.getGlobalConnections().containsKey(nick) && Connection.getGlobalConnections().get(nick).active()) {
                connection.send("433 " + nick + " :Nickname is already in use");
            } else if (connection.getUserConfig() != null && connection.getUserConfig().getNick() != null
                    && Connection.getGlobalConnections().containsKey(connection.getUserConfig().getNick())) {
                // Changing name
                String oldNick           = connection.getUserConfig().getNick();
                String oldRepresentation = connection.getRepresentation();
                Connection.getGlobalConnections().remove(connection.getUserConfig().getNick());
                connection.getUserConfig().setNick(nick);
                Connection.getGlobalConnections().put(nick, connection);
                connection.getUserConfig().getChannelList().forEach(channelName -> {
                    Channel channel = Channels.getGlobalChannel(channelName);
                    channel.changeMember(connection, oldNick, oldRepresentation);
                });
            }  else {
                // New user
                Connection.getGlobalConnections().put(nick, connection);
                connection.getUserConfig().setNick(nick);

                // Auto Join #MiniD
                if (!Channels.getGlobalChannel("#MiniD").containsMember(nick)) {
                    String[] joinArguments = "JOIN #MiniD".split(" ");
                    Commands.JOIN.run(connection, joinArguments);
                }
            }
        }
    },

    USER() {
        @Override
        public void run(Connection connection, String[] arguments) {
            if (connection.getUserConfig().getUsername() == null) {
                connection.getUserConfig().setUsername(arguments[arguments.length - 1]);
                sendWelcome(connection);
            }
        }

        private void sendWelcome(Connection connection) {
            connection.send("001 " + connection.getUserConfig().getNick() + " :Welcome to " + Connection.globalServerName + ", a MiniD-powered IRC network.");
            connection.send("004 " + connection.getUserConfig().getNick() + " " + Connection.globalServerName + " MiniD");
            connection.send("375 " + connection.getUserConfig().getNick() + " :- " + Connection.globalServerName + " Message of the Day -");
            connection.send("372 " + connection.getUserConfig().getNick() + " :- Hello. Welcome to " + Connection.globalServerName + ", a MiniD-powered IRC network.");
            connection.send("372 " + connection.getUserConfig().getNick() + " :- See http://something.com " + "for more info on MiniD.");
            connection.send("376 " + connection.getUserConfig().getNick() + " :End of /MOTD command.");
        }
    },

    QUIT() {
        @Override
        public void run(Connection connection, String[] arguments) { connection.close(); }
    },

    WHOIS() {
        @Override
        public void run(Connection connection, String[] arguments) {
            String nick = arguments[1];
            if (Connection.getGlobalConnections().containsKey(nick)) {
                Connection whoIsConnection = Connection.getGlobalConnections().get(nick);
                connection.send("311 " + whoIsConnection.getUserConfig().getNick() + " " +
                        whoIsConnection.getUserConfig().getNick() + " " +
                        whoIsConnection.getUserConfig().getUsername() + " :" + whoIsConnection.getHost());
            } else {
                connection.noSuchNickChannel();
            }
        }
    },

    PRIVMSG() {
        @Override
        public void run(Connection connection, String[] arguments) {
            String target = arguments[1];
            if (arguments.length >= 3) {
                if (Connection.getGlobalConnections().containsKey(target)) {
                    Connection.getGlobalConnections().get(target).send(":" + connection.getUserConfig().getNick() + " PRIVMSG " + target + " " + Helper.join(Arrays.copyOfRange(arguments, 2, arguments.length), " "));
                } else if (Channels.getGlobalChannels().containsKey(target)) {
                    if (Channels.getGlobalChannels().get(target).containsMember(connection.getUserConfig().getNick())) {
                        Channels.getGlobalChannels().get(target).msgMembers(":" + connection.getUserConfig().getNick() + " PRIVMSG " + target + " " + Helper.join(Arrays.copyOfRange(arguments, 2, arguments.length), " "), connection);
                    } else {
                        connection.sendNotice("You can not send messages to a channel you are not a member of.");
                    }
                } else {
                    connection.noSuchNickChannel();
                }
            } else {
                connection.send("412 :No text to send");
            }
        }
    },

    UNKNOWNCOMMAND() {
        @Override
        public void run(Connection connection, String[] arguments) {
            connection.send("421 " + arguments[0] + " :Unknown command");
        }
    },

    JOIN() {
        @Override
        public void run(Connection connection, String[] arguments) {
            String channelName = arguments[1];
            if (channelName.startsWith("#")) {
                if (Channels.globalChannelExists(channelName)) {
                    Channel channel = Channels.getGlobalChannel(channelName);
                    if (channel.containsMember(connection.getUserConfig().getNick()) && connection.getUserConfig().getChannelList().contains(channelName)) {
                        connection.sendNotice("You are already a member of that channel.");
                    } else {
                        channel.addMember(connection);
                        connection.getUserConfig().getChannelList().add(channelName);
                    }
                } else {
                    Channel newChannel = new Channel();
                    newChannel.setName(channelName);
                    newChannel.addMember(connection);
                    Channels.addGlobalChannel(channelName, newChannel);
                }
            } else { connection.sendNotice("This server requires channels to begin with #"); }
        }
    },

    MODE() {
        @Override
        public void run(Connection connection, String[] arguments) {
            String channel = arguments[1];
            if (Channels.globalChannelExists(channel))
                connection.sendGlobal("MODE " + Channels.getGlobalChannel(channel).getName() + " +nt");
            else
                connection.noSuchNickChannel();
        }
    },

    VERSION() {
        @Override
        public void run(Connection connection, String[] arguments) {
            connection.send("351 1.0" + Connection.globalServerName + " :MiniD 1.0");
        }
    },

    TOPIC() {
        @Override
        public void run(Connection connection, String[] arguments) {
            String channelName = arguments[1];
            if (Channels.globalChannelExists(channelName)) {
                Channel channel = Channels.getGlobalChannel(channelName);
                if (arguments.length <= 2) { // Return the topic
                    channel.sendTopic(connection);
                } else if (arguments.length > 2) { // Set the topic
                    String topic = Helper.join(Arrays.copyOfRange(arguments, 2, arguments.length), " ");
                    if (topic.startsWith(":")) topic = topic.replaceFirst(":", "");
                    channel.setTopic(topic);
                    channel.msgMembers(":" + connection.getRepresentation() + " TOPIC " + channel.getName() + " :" + channel.getTopic());
                }
            } else {
                connection.noSuchNickChannel();
            }
        }
    },

    PONG() {
        @Override
        public void run(Connection connection, String[] arguments) { connection.isPlayingPingPong = true; }
    },

    NAMES() {
        @Override
        public void run(Connection connection, String[] arguments) {
            if (arguments.length == 2) {
                String channelName = arguments[1];
                if (Channels.getGlobalChannels().containsKey(channelName))
                    Channels.getGlobalChannels().get(channelName).doList(connection);
                else
                    connection.noSuchNickChannel();
            } else { connection.noSuchNickChannel(); }
        }
    }
}
