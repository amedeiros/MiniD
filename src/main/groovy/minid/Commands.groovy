package minid

import minid.commands.Command

/**
 * Class to handle different command requests.
 */
enum Commands implements Command {
    NICK() {
        @Override
        void run(Connection connection, String[] arguments) {
            checkAndSetNick(arguments[1], connection)
        }

        private void checkAndSetNick(String nick, Connection connection) {
            if (Connection.globalConnections.containsKey(nick) && Connection.globalConnections[nick].active()) {
                connection.send("433 ${nick} :Nickname is already in use")
            } else if (connection.userConfig != null && connection.userConfig.nick != null
                    && Connection.globalConnections.containsKey(connection.userConfig.nick)) {
                // Changing name
                String oldNick           = connection.userConfig.nick
                String oldRepresentation = connection.representation

                Connection.globalConnections.remove(connection.userConfig.nick)
                connection.getUserConfig().setNick(nick)
                Connection.getGlobalConnections().put(nick, connection)

                connection.userConfig.channelList.each {
                    it.changeMember(connection, oldNick, oldRepresentation)
                }

            }  else {
                // New user
                Connection.globalConnections[nick] = connection
                connection.userConfig.nick = nick

                // Auto Join #MiniD
                if (!Channels.GLOBAL_CHANNELS['MiniD'].containsMember(nick)) {
                    JOIN.run(connection, 'JOIN #MiniD'.split(' '))
                }
            }
        }
    },

    USER() {
        @Override
        void run(Connection connection, String[] arguments) {
            if (connection.userConfig.username == null) {
                connection.userConfig.username = arguments[arguments.length - 1]
                sendWelcome(connection)
            }
        }

        private void sendWelcome(Connection connection) {
            connection.send("001 ${connection.userConfig.nick} :Welcome to ${Connection.globalServerName}, a MiniD-powered IRC network.")
            connection.send("004 ${connection.userConfig.nick} ${Connection.globalServerName} MiniD")
            connection.send("375 ${connection.userConfig.nick} :- ${Connection.globalServerName} Message of the Day -")
            connection.send("372 ${connection.userConfig.nick} :- Hello. Welcome to ${Connection.globalServerName}, a MiniD-powered IRC network.")
            connection.send("372 ${connection.userConfig.nick} :- See http://something.com for more info on MiniD.")
            connection.send("376 ${connection.userConfig.nick} :End of /MOTD command.")
        }
    },

    QUIT() {
        @Override
        void run(Connection connection, String[] arguments) {
            connection.close()
        }
    },

    WHOIS() {
        @Override
        void run(Connection connection, String[] arguments) {
            String nick = arguments[1]

            if (Connection.globalConnections.containsKey(nick)) {
                Connection whoIsConnection = Connection.globalConnections[nick]
                connection.send("311 ${whoIsConnection.userConfig.nick} ${whoIsConnection.userConfig.nick} " +
                        "${whoIsConnection.userConfig.username} :${whoIsConnection.host}")
            } else {
                connection.noSuchNickChannel()
            }
        }
    },

    PRIVMSG() {
        @Override
        void run(Connection connection, String[] arguments) {
            String target = arguments[1]

            if (arguments.length >= 3) {
                if (Connection.globalConnections.containsKey(target)) {
                    Connection.globalConnections[target].send(":${connection.userConfig.nick} PRIVMSG " +
                            "${target} ${Arrays.copyOfRange(arguments, 2, arguments.length).join(' ')}")
                } else if (Channels.GLOBAL_CHANNELS.containsKey(target)) {
                    if (Channels.GLOBAL_CHANNELS[target].containsMember(connection.userConfig.nick)) {
                        Channels.GLOBAL_CHANNELS[target].msgMembers(":${connection.userConfig.nick} PRIVMSG " +
                                "${target} ${Arrays.copyOfRange(arguments, 2, arguments.length).join(' ')}", connection)
                    } else {
                        connection.sendNotice('You can not send messages to a channel you are not a member of.')
                    }
                } else {
                    connection.noSuchNickChannel()
                }
            } else {
                connection.send('412 :No text to send')
            }
        }
    },

    UNKNOWNCOMMAND() {
        @Override
        void run(Connection connection, String[] arguments) {
            connection.send("421 ${arguments[0]} :Unknown command")
        }
    },

    JOIN() {
        @Override
        void run(Connection connection, String[] arguments) {
            String channelName = arguments[1]
            if (channelName.startsWith('#')) {
                if (Channels.globalChannelExists(channelName)) {
                    Channel channel = Channels.GLOBAL_CHANNELS[channelName]
                    if (channel.containsMember(connection.userConfig.nick) &&
                            connection.userConfig.channelList.contains(channelName)) {
                        connection.sendNotice('You are already a member of that channel.')
                    } else {
                        channel.addMember(connection)
                        connection.userConfig.channelList.add(channel)
                    }
                } else {
                    Channels.GLOBAL_CHANNELS[channelName] = new Channel(name: channelName).withMember(connection)
                }
            } else {
                connection.sendNotice('This server requires channels to begin with #')
            }
        }
    },

    MODE() {
        @Override
        void run(Connection connection, String[] arguments) {
            String channel = arguments[1]
            if (Channels.globalChannelExists(channel)) {
                connection.sendGlobal("MODE ${Channels.GLOBAL_CHANNELS[channel].name} +nt")
            } else {
                connection.noSuchNickChannel()
            }
        }
    },

    VERSION() {
        @Override
        void run(Connection connection, String[] arguments) {
            connection.send("351 1.0${Connection.globalServerName} :MiniD 1.0")
        }
    },

    TOPIC() {
        @Override
        void run(Connection connection, String[] arguments) {
            String channelName = arguments[1]

            if (Channels.globalChannelExists(channelName)) {
                Channel channel = Channels.GLOBAL_CHANNELS[channelName]
                if (arguments.length <= 2) { // Return the topic
                    channel.sendTopic(connection)
                } else if (arguments.length > 2) { // Set the topic
                    String topic = Arrays.copyOfRange(arguments, 2, arguments.length).join(' ')
                    if (topic.startsWith(':')) {
                        topic = topic.replaceFirst(':', '')
                    }
                    channel.topic = topic
                    channel.msgMembers(":${connection.representation} TOPIC ${channel.name} :${channel.topic}")
                }
            } else {
                connection.noSuchNickChannel()
            }
        }
    },

    PONG() {
        @Override
        void run(Connection connection, String[] arguments) {
            connection.isPlayingPingPong = true
        }
    },

    NAMES() {
        @Override
        void run(Connection connection, String[] arguments) {
            if (arguments.length == 2) {
                String channelName = arguments[1]
                if (Channels.GLOBAL_CHANNELS.containsKey(channelName)) {
                    Channels.GLOBAL_CHANNELS[channelName].doList(connection)
                } else {
                    connection.noSuchNickChannel()
                }
            } else {
                connection.noSuchNickChannel()
            }
        }
    }

    void run(Connection connection, String[] arguments) {
    }
}
