package minid.tools;

import org.apache.commons.cli.Options;

/**
 * Created by Andrew Medeiros on 4/30/15.
 */
public class CommandLineOptions {
    private static Options options = setOptions();

    public static Options getOptions() { return options; }

    private static Options setOptions() {
        options = new Options();

        // Add the port option
        options.addOption("port", true, "Port to run the irc server on. Default: 6667");

        // Add the host option
        options.addOption("host", true, "Host to run the irc server on. Default: localhost");

        // Add the nick option
        options.addOption("nick", true, "Nickname you are connecting with");

        // Add the username option
        options.addOption("username", true, "Username you are connecting with");

        return options;
    }
}
