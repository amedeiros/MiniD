package minid.tools;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.ParseException;

/**
 * Created by Andrew Medeiros on 4/30/15.
 */
public class CommandParser {
    private  Options options;
    private BasicParser commandLineParser = new BasicParser();

    /**
     * @param options Custom options for the Commons-Cli
     */
    public CommandParser(Options options) { this.options = options; }

    /**
     * Use our default options for the Commons-Cli
     */
    public CommandParser() { this.options = CommandLineOptions.getOptions(); }

    /**
     * @param args Arguments to parse
     * @return Command line object
     */
    public CommandLine parse(String[] args) {
        CommandLine commandLine = null;
        try { commandLine = commandLineParser.parse(options, args); }
        catch (ParseException e) {
            e.printStackTrace();
        }

        return commandLine;
    }
}
