package minid;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by amedeiros on 8/25/15.
 */
public class Channels {
    private static ConcurrentHashMap<String, Channel> globalChannels = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String, Channel> getGlobalChannels() { return globalChannels; }
    public static void addGlobalChannel(String name, Channel channel) { getGlobalChannels().put(name, channel); }
    public static boolean globalChannelExists(String name) { return getGlobalChannels().containsKey(name); }
    public static Channel getGlobalChannel(String name) { return getGlobalChannels().get(name); }

}
