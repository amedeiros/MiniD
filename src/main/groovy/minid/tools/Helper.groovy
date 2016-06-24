package minid.tools

/**
 * Utility class for helpers.
 */
class Helper {
    private Helper() { }

    /**
     * Join an array of strings with a delimiter.
     * This will be removed once 100% moved to groovy.
     * @param strings
     * @param delimiter
     * @return
     */
    @Deprecated
    static String join(String[] strings, String delimiter) {
        strings.join(delimiter)
    }
}
