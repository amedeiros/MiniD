package minid;

import minid.configuration.ServerConfig;
import minid.tools.CommandParser;
import org.apache.commons.cli.CommandLine;

/**
 * Created by Andrew Medeiros on 4/30/15.
 */
public class MiniD {
    private ServerConfig serverConfig = new ServerConfig();
    private Server server;

    public MiniD(CommandLine commandLine) throws Exception {
        if (commandLine.hasOption("host"))
            serverConfig.setHost(commandLine.getOptionValue("host"));
        if (commandLine.hasOption("port"))
            serverConfig.setPort(Integer.parseInt(commandLine.getOptionValue("port")));

        server = new Server(serverConfig);
    }

    public void start() { new Thread(server).start(); }

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        CommandParser commandParser = new CommandParser();
        MiniD miniD = new MiniD(commandParser.parse(args));
        miniD.start();
    }
}
