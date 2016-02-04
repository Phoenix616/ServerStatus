package de.themoep.serverstatus;

/*
 * Based on zaiyers YamlConfig
 * https://github.com/zaiyers/Channels/blob/master/src/main/java/net/zaiyers/Channels/config/YamlConfig.java
 */

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class YamlConfig {
    private final Plugin plugin;
    protected Configuration cfg;
    protected final static ConfigurationProvider ymlCfg = ConfigurationProvider.getProvider( YamlConfiguration.class );

    protected File configFile;

    /**
     * read configuration into memory
     * @param configFileName
     * @throws IOException
     */
    public YamlConfig(Plugin plugin, String configFileName) throws IOException {
        this.plugin = plugin;
        configFile = new File(plugin.getDataFolder(), configFileName);

        if (!configFile.exists()) {
            if (!configFile.getParentFile().exists()) {
                configFile.getParentFile().mkdirs();
            }
            configFile.createNewFile();
            cfg = ymlCfg.load(configFile);

            createDefaultConfig();
        } else {
            cfg = ymlCfg.load(configFile);
        }
    }

    public void createDefaultConfig() {
        cfg = ymlCfg.load(new InputStreamReader(plugin.getResourceAsStream("config.yml")));

        save();
    }

    /**
     * save configuration to disk
     */
    public void save() {
        try {
            ymlCfg.save(cfg, configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Unable to save configuration at " + configFile.getAbsolutePath());
            e.printStackTrace();
        }
    }
    
    /**
     * deletes configuration file
     */
    public void removeConfig() {
        configFile.delete();
    }

    public Configuration getCfg() {
        return cfg;
    }
}