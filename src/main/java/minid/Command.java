package minid;

/**
 * Created by Andrew Medeiros on 4/30/15.
 */
public interface Command {
    void run(Connection connection, String[] arguments);
}
