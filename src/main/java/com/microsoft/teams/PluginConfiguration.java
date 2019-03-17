package com.microsoft.teams;

import com.atlassian.plugin.Plugin;
import com.microsoft.teams.utils.AppUtils;
import org.springframework.stereotype.Component;

@Component
public class PluginConfiguration {

    private static Plugin plugin;

    public static void setPlugin(Plugin plugin) {
        PluginConfiguration.plugin = plugin;
    }

    public static String getPluginKey() {
        return AppUtils.getKey();
    }
}
