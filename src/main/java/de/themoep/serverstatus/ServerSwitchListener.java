package de.themoep.serverstatus;

import com.google.common.collect.ImmutableMap;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

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
public class ServerSwitchListener implements Listener {

    private final ServerStatus plugin;

    public ServerSwitchListener(ServerStatus plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onServerSwitch(ServerConnectEvent event) {
        // Player is not currently connected to a server -> he joins and doesn't want to switch
        if(event.getPlayer().getServer() == null || event.getPlayer().hasPermission(plugin.getDescription().getName() + ".preventswitch.bypass")) {
            return;
        }

        Boolean status = plugin.getChecker().getStatus(event.getTarget());
        if(status != null && !status) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(plugin.getMessage("preventswitch", ImmutableMap.of("server", event.getTarget().getName())));
        }
    }
}
