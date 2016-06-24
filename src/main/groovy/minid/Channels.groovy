package minid

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

/**
 * Manage a list of channels.
 */
class Channels {
    static final ConcurrentMap<String, Channel> GLOBAL_CHANNELS = new ConcurrentHashMap<>()

    static void addGlobalChannel(String name, Channel channel) {
        GLOBAL_CHANNELS[name] = channel
    }

    static boolean globalChannelExists(String name) {
        GLOBAL_CHANNELS.containsKey(name)
    }

    static Channel getGlobalChannel(String name) {
        GLOBAL_CHANNELS[name]
    }
}
