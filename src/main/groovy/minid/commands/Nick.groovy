package minid.commands

import minid.Channels
import minid.Connection

/**
 * Created by amedeiros on 4/29/16.
 */
class Nick implements Command {
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
}
