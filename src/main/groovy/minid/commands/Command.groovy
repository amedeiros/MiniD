package minid.commands

import minid.Connection

/**
 * Command interface for implementing command requests.
 */
interface Command {
    void run(Connection connection, String[] arguments)
}
