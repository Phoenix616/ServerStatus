package de.themoep.serverstatus;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.config.Configuration;

import java.io.IOException;
import java.util.Map;

/**
 * Bungee Plugins
 * Copyright (C) 2016 Max Lee (https://github.com/Phoenix616/)
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Mozilla Public License as published by
 * the Mozilla Foundation, version 2.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * Mozilla Public License v2.0 for more details.
 * <p/>
 * You should have received a copy of the Mozilla Public License v2.0
 * along with this program. If not, see <http://mozilla.org/MPL/2.0/>.
 */
public class ServerStatus extends Plugin {

    private YamlConfig yamlConfig;
    private boolean enabled;
    private ServerStatusChecker statusChecker;

    public void onEnable() {
        statusChecker = new ServerStatusChecker(this);
        enabled = loadConfig();
        getProxy().getPluginManager().registerCommand(this, new ServerStatusCommand(this, "serverstatus", getDescription().getName() + ".command", "ss", "status"));
        getProxy().getPluginManager().registerListener(this, new ServerSwitchListener(this));
    }

    public Configuration getConfig() {
        return yamlConfig.getCfg();
    }

    public boolean loadConfig() {
        try {
            yamlConfig = new YamlConfig(this, "config.yml");
            statusChecker.start();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean reloadConfig() {
        enabled = loadConfig();
        return enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public ServerStatusChecker getChecker() {
        return statusChecker;
    }

    public void broadcast(String permission, String message) {
        getLogger().info(message);
        for(ProxiedPlayer player : getProxy().getPlayers()) {
            if(player.hasPermission(permission)) {
                player.sendMessage(message);
            }
        }
    }

    public String getMessage(String key) {
        String msg = getConfig().getString("messages." + key, "");
        if(msg.isEmpty()) {
            return ChatColor.RED + "Unknown language key: " + ChatColor.YELLOW + key;
        } else {
            return ChatColor.translateAlternateColorCodes('&', msg);
        }
    }

    public String getMessage(String key, Map<String, String> replacements) {
        String string = getMessage(key);
        // insert replacements
        if(replacements != null) {
            for(Map.Entry<String, String> entry : replacements.entrySet()) {
                string = string.replace("%" + entry.getKey() + "%", entry.getValue());
            }
        }
        return string;
    }
}
