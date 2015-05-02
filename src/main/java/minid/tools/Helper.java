package minid.tools;

/**
 * Created by Andrew Medeiros on 5/1/15.
 */
public class Helper {
    public static String join(String[] strings, String delimiter) {
        String joined = "";
        for(String string : strings)
            joined += string + delimiter;

        return joined;
    }
}
