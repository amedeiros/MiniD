package minid.tools

import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.Options

/**
 * Parse command line options.
 */
class CommandParser {
    private final CommandParser commandLineParser

    /**
     * @param options Custom options for the Commons-Cli
     */
    CommandParser(Options options) {
        this.commandLineParser = new CommandParser(options)
    }

    /**
     * Use our default options for the Commons-Cli
     */
    CommandParser() {
        this.commandLineParser = new CommandParser(CommandLineOptions.OPTIONS)
    }

    /**
     * @param args Arguments to parse
     * @return Command line object
     */
    CommandLine parse(String[] args) {
        commandLineParser.parse(args)
    }
}
