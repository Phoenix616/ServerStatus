# ServerStatus
Small BungeeCord plugin to check the status of connected servers and block players from attempting to connect to offline servers. Builds can be found on the [Minebench.de Jenkins](http://ci.minebench.de/job/ServerStatus/).

### Commands:
/serverstatus, aliases: /ss and /status
- /serverstatus reload - Reloads the config
- /serverstatus refresh - Run a manual status refresh
- /serverstatus setonline <servername> - Manually sets the server's status to online
- /serverstatus setoffline <servername> - Manually sets the server's status to offline

The setoffline option will stop the plugin from checking the status of the server until it's set back to online via the command!

### Permissions:
- ServerStatus.command - Access to the command and all its options
- ServerStatus.info - Receive information messages about when a server is automatically detected as online or offline
- ServerStatus.preventswitch.bypass - Bypass the switch-to-offline-server-prevention, usefull for when you manually marked a as server for maintance

### Config:

```yaml
# Checkintervall in seconds
checkinterval: 10
messages:
  preventswitch: "&cYou can't join &e%server%&c! The server is offline!"
  info:
    online: "&e%server% &ais now online!"
    offline: "&e%server% &cis now offline!"
```
