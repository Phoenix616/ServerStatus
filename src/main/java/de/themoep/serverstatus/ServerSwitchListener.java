package de.themoep.serverstatus;

import com.google.common.collect.ImmutableMap;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/*
 * ServerStatus
 * Copyright (C) 2021 Max Lee aka Phoenix616 (max@themoep.de)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
