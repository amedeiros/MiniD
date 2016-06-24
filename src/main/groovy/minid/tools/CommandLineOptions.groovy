package minid.tools

import org.apache.commons.cli.Options

/**
 * Default options for the command line parser.
 */
class CommandLineOptions {
    private CommandLineOptions() { }

    static final Options OPTIONS

    static {
        OPTIONS = new Options()

        // Add the port option
        OPTIONS.addOption('port', true, 'Port to run the irc server on. Default: 6667')

        // Add the host option
        OPTIONS.addOption('host', true, 'Host to run the irc server on. Default: localhost')

        // Add the nick option
        OPTIONS.addOption('nick', true, 'Nickname you are connecting with')

        // Add the username option
        OPTIONS.addOption('username', true, 'Username you are connecting with')

        // Add ssl option
        OPTIONS.addOption('ssl', true, 'Should we run a SSL socket server. Default: true')
    }
}
