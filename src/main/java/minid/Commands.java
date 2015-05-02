package minid;

import minid.tools.Helper;

import java.util.Arrays;

/**
 * Created by Andrew Medeiros on 4/30/15.
 */
public enum Commands implements Command {
    NICK() {
        @Override
        public void run(Connection connection, String[] arguments) {
            if (connection.getUserConfig().getNick() == null) {
                checkAndSetNick(arguments[1], connection);
            }
            // Auto Join #MiniD
            String[] joinArguments = "JOIN #MiniD".split(" ");
            Commands.JOIN.run(connection, joinArguments);
        }

        private void checkAndSetNick(String nick, Connection connection) {
            if (Connection.getGlobalConnections().containsKey(nick) && Connection.getGlobalConnections().get(nick).active()) {
                connection.send("433 " + nick + " :Nickname is already in use");
            }  else {
                Connection.getGlobalConnections().put(nick, connection);
                connection.getUserConfig().setNick(nick);
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
        public void run(Connection connection, String[] arguments) {
            connection.close();
        }
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
                connection.send("401 " + nick + " :No such nick/channel");
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
                } else if (Channel.getGlobalChannels().containsKey(target)) {
                    if (Channel.getGlobalChannels().get(target).containsMember(connection.getUserConfig().getNick())) {
                        Channel.getGlobalChannels().get(target).msgMembers(":" + connection.getUserConfig().getNick() + " PRIVMSG " + target + " " + Helper.join(Arrays.copyOfRange(arguments, 2, arguments.length), " "), connection);
                    } else {
                        connection.sendNotice("You can not send messages to a channel you are not a member of.");
                    }
                } else {
                    connection.send("401 " + target + " :No such nick/channel");
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
                if (Channel.getGlobalChannels().containsKey(channelName)) {
                    Channel channel = Channel.getGlobalChannels().get(channelName);
                    if (channel.containsMember(connection.getUserConfig().getNick())) {
                        connection.sendNotice("You are already a member of that channel.");
                    } else {
                        channel.addMember(connection);
                    }
                } else {
                    Channel newChannel = new Channel();
                    newChannel.setName(channelName);
                    Channel.getGlobalChannels().put(channelName, newChannel);
                    newChannel.addMember(connection);
                }
            } else { connection.sendNotice("This server requires channels to begin with #"); }
        }
    },

    MODE() {
        @Override
        public void run(Connection connection, String[] arguments) {
            String channel = arguments[1];
            if (Channel.getGlobalChannels().containsKey(channel))
                connection.sendGlobal("MODE " + Channel.getGlobalChannels().get(channel).getName() + " +nt");
            else
                connection.send("401 " + connection.getUserConfig().getNick() + " :No such nick/channel");
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
            if (Channel.getGlobalChannels().containsKey(channelName)) {
                Channel channel = Channel.getGlobalChannels().get(channelName);
                if (arguments.length <= 2) { // Return the topic
                    Channel.getGlobalChannels().get(channelName).sendTopic(connection);
                } else if (arguments.length > 2) { // Set the topic
                    String topic = Helper.join(Arrays.copyOfRange(arguments, 2, arguments.length), " ");
                    if (topic.startsWith(":")) topic = topic.replaceFirst(":", "");
                    channel.setTopic(topic);
                    channel.msgMembers(":" + connection.getRepresentation() + " TOPIC " + channel.getName() + " :" + channel.getTopic());
                }
            } else {
                connection.send("401 " + connection.getUserConfig().getNick() + " :No such nick/channel");
            }
        }
    },

    PONG() {
        @Override
        public void run(Connection connection, String[] arguments) {
            connection.isPlayingPingPong = true;
        }
    }
}
