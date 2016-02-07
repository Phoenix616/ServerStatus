package de.themoep.serverstatus;

import com.google.common.collect.ImmutableMap;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * ServerStatus
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
public class ServerStatusChecker {
    private final ServerStatus plugin;
    private List<ScheduledTask> pingTask = new ArrayList<ScheduledTask>();

    private Map<String, Boolean> statusMap = new ConcurrentHashMap<String, Boolean>();
    private Set<String> statusSetManually = new HashSet<String>();

    public ServerStatusChecker(ServerStatus plugin) {
        this.plugin = plugin;
    }

    public void start() {
        stop();
        int pingOnline = plugin.getConfig().getInt("checkinterval.online", 10);
        int pingOffline = plugin.getConfig().getInt("checkinterval.offline", 10);
        if(pingOnline == pingOffline) {
            if(pingOnline == 0)
                return;
            pingTask.add(plugin.getProxy().getScheduler().schedule(plugin, new Runnable() {
                public void run() {
                    if(plugin.isEnabled()) {
                        refreshStatusMap(plugin.getProxy().getServers().values());
                    } else {
                        stop();
                    }
                }
            }, 10, pingOnline, TimeUnit.SECONDS));
        } else {
            refreshStatusMap(plugin.getProxy().getServers().values());
            if(pingOnline != 0) {
                pingTask.add(plugin.getProxy().getScheduler().schedule(plugin, new Runnable() {
                    public void run() {
                        if(plugin.isEnabled()) {
                            refreshStatusMap(getOnlineServers());
                        } else {
                            stop();
                        }
                    }
                }, 10, pingOnline, TimeUnit.SECONDS));
            }
            if(pingOffline != 0) {
                pingTask.add(plugin.getProxy().getScheduler().schedule(plugin, new Runnable() {
                    public void run() {
                        if(plugin.isEnabled()) {
                            refreshStatusMap(getOfflineServers());
                        } else {
                            stop();
                        }
                    }
                }, 10, pingOffline, TimeUnit.SECONDS));
            }
        }
    }

    public Collection<ServerInfo> getOnlineServers() {
        List<ServerInfo> onlineServers = new ArrayList<ServerInfo>();
        for(Map.Entry<String, Boolean> entry : getStatusMap().entrySet()) {
            if(entry.getValue() != null && entry.getValue()) {
                ServerInfo server = plugin.getProxy().getServerInfo(entry.getKey());
                if(server != null) {
                    onlineServers.add(server);
                }
            }
        }
        return onlineServers;
    }

    public Collection<ServerInfo> getOfflineServers() {
        List<ServerInfo> offlineServers = new ArrayList<ServerInfo>();
        for(Map.Entry<String, Boolean> entry : getStatusMap().entrySet()) {
            if(entry.getValue() != null && !entry.getValue()) {
                ServerInfo server = plugin.getProxy().getServerInfo(entry.getKey());
                if(server != null) {
                    offlineServers.add(server);
                }
            }
        }
        return offlineServers;
    }

    public void refreshStatusMap(Collection<ServerInfo> servers) {
        for(final ServerInfo server : servers) {
            if(statusSetManually.contains(server.getName()))
                return;

            if(server.getPlayers().size() == 0) {
                plugin.getProxy().getScheduler().runAsync(plugin, new Runnable() {
                    public void run() {
                        setStatus(server, isReachable(server.getAddress()));
                    }
                });
            } else {
                setStatus(server, true);
            }
        }
    }

    private boolean isReachable(InetSocketAddress address) {
        Socket socket = new Socket();
        try {
            socket.connect(address, 250);
            socket.close();
            return true;
        } catch(IOException ignored) {
        }
        return false;
    }

    private void setStatus(ServerInfo server, boolean online) {
        Boolean oldStatus = statusMap.put(server.getName(), online);

        if(oldStatus != null && oldStatus != online) {
            Map<String, String> repl = ImmutableMap.of("server", server.getName());
            String msg = online ? plugin.getMessage("info.online", repl) : plugin.getMessage("info.offline", repl);

            plugin.getLogger().info(msg);
            for(ProxiedPlayer player : plugin.getProxy().getPlayers()) {
                if(player.hasPermission(plugin.getDescription().getName() + ".info") && server.canAccess(player)) {
                    player.sendMessage(msg);
                }
            }
        }
    }

    /**
     * Get the status of a server
     * @param server
     * @return <tt>true</tt> if it is online; <tt>false</tt> if it is offline; <tt>null</tt> if the status is unknown
     */
    public Boolean getStatus(ServerInfo server) {
        return statusMap.get(server.getName());
    }

    /**
     * hammertime
     */
    public void stop() {
        statusMap = new ConcurrentHashMap<String, Boolean>();
        statusSetManually = new HashSet<String>();
        Iterator<ScheduledTask> taskIt = pingTask.iterator();
        while(taskIt.hasNext()) {
            taskIt.next().cancel();
            taskIt.remove();
        }
    }

    public Map<String, Boolean> getStatusMap() {
        return statusMap;
    }

    public void setManualStatus(ServerInfo server, boolean online) {
        statusMap.put(server.getName(), online);
        if(online) {
            statusSetManually.remove(server.getName());
        } else {
            statusSetManually.add(server.getName());
        }
    }

    public boolean isManuallySet(String serverName) {
        return statusSetManually.contains(serverName);
    }
}
