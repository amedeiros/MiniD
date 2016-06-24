package minid

import minid.configuration.ServerConfig
import minid.tools.CommandParser
import org.apache.commons.cli.CommandLine

/**
 * MiniD main class.
 */
class MiniD {
    private final Server server
    private static final HOST_OPTION = 'host'
    private static final PORT_OPTION = 'port'

    MiniD(CommandLine commandLine) throws Exception {
        ServerConfig serverConfig = new ServerConfig()

        if (commandLine.hasOption(HOST_OPTION)) {
            serverConfig.setHost(commandLine.getOptionValue(HOST_OPTION))
        }

        if (commandLine.hasOption(PORT_OPTION)) {
            serverConfig.setPort(Integer.parseInt(commandLine.getOptionValue(PORT_OPTION)))
        }

        this.server = new Server(serverConfig)
    }

    /**
     * Start the server.
     */
    void start() {
        new Thread(server).start()
    }

    /**
     * @param args
     * @throws Exception
     */
    static void main(String[] args) throws Exception {
        CommandParser commandParser = new CommandParser()
        MiniD miniD = new MiniD(commandParser.parse(args))
        miniD.start()
    }
}
