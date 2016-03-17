package de.themoep.serverstatus;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Licensed under the Nietzsche Public License v0.6
 *
 * Copyright 2016 Max Lee (https://github.com/Phoenix616/)
 *
 * Copyright, like God, is dead.  Let its corpse serve only to guard against its
 * resurrection.  You may do anything with this work that copyright law would
 * normally restrict so long as you retain the above notice(s), this license, and
 * the following misquote and disclaimer of warranty with all redistributed
 * copies, modified or verbatim.  You may also replace this license with the Open
 * Works License, available at the http://owl.apotheon.org website.
 *
 *    Copyright is dead.  Copyright remains dead, and we have killed it.  How
 *    shall we comfort ourselves, the murderers of all murderers?  What was
 *    holiest and mightiest of all that the world of censorship has yet owned has
 *    bled to death under our knives: who will wipe this blood off us?  What
 *    water is there for us to clean ourselves?  What festivals of atonement,
 *    what sacred games shall we have to invent?  Is not the greatness of this
 *    deed too great for us?  Must we ourselves not become authors simply to
 *    appear worthy of it?
 *                                     - apologies to Friedrich Wilhelm Nietzsche
 *
 * No warranty is implied by distribution under the terms of this license.
 */

public class FileConfiguration {
    protected final static ConfigurationProvider yml = ConfigurationProvider.getProvider(YamlConfiguration.class);

    private Plugin plugin;

    private Configuration config;
    private File configFile;

    /**
     * FileConfiguration represents a configuration saved in a yml file
     * @param plugin The bungee plugin of the config
     * @param path The path to the yml file with the plugin's datafolder as the parent
     * @throws IOException
     */
    public FileConfiguration(Plugin plugin, String path) throws IOException {
        this(plugin, new File(plugin.getDataFolder(), path));
    }

    /**
     * FileConfiguration represents a configuration saved in a yml file
     * @param plugin The bungee plugin of the config
     * @param configFile The yml file
     * @throws IOException
     */
    public FileConfiguration(Plugin plugin, File configFile) throws IOException {
        this.plugin = plugin;
        loadConfig(configFile);
    }

    /**
     * Load a file into this config
     * @param configFile The yml file
     * @return <tt>true</tt> if it was successfully loaded, <tt>false</tt> if not
     */
    public boolean loadConfig(File configFile) throws IOException {
        this.configFile = configFile;

        if(configFile.exists()) {
            config = yml.load(configFile);
            return true;
        } else if(configFile.getParentFile().exists() || configFile.getParentFile().mkdirs()) {
            return createDefaultConfig();
        }
        return false;
    }

    /**
     * Saves the config into the yml file on the disc
     * @return <tt>true</tt> if it was saved; <tt>false</tt> if an error occurred
     */
    public boolean saveConfig() {
        try {
            yml.save(config, configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save configuration to " + configFile.getAbsolutePath());
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Copy the default config from the plugin jar into its path
     * @return <tt>true</tt> if it was successfully created, <tt>false</tt> if it already existed
     */
    public boolean createDefaultConfig() throws IOException {
        if(configFile.createNewFile()) {
            config = yml.load(new InputStreamReader(plugin.getResourceAsStream(configFile.getName())));
            saveConfig();
            return true;
        }
        return false;
    }    
    
    /**
     * Delete the file of this config from the disc
     * @return <tt>true</tt> if it was successfully deleted; <tt>false</tt> otherwise
     */
    public boolean removeConfig() {
        return configFile.delete();
    }

    public Configuration getConfiguration() {
        return config;
    }
}