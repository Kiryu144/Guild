package de.mevidia.kiryu144.guild;

import de.mevidia.kiryu144.guild.command.GuildCommands;
import de.mevidia.kiryu144.guild.guild.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

public class Guild extends JavaPlugin {
    public static Guild instance;
    public static Guilds guilds;

    @Override
    public void onEnable() {
        instance = this;
        guilds = new Guilds();

        ConfigurationSerialization.registerClass(GuildBalance.class);
        ConfigurationSerialization.registerClass(GuildPlayer.class);
        ConfigurationSerialization.registerClass(GuildInstance.class);
        Bukkit.getPluginManager().registerEvents(guilds, this);

        guilds.loadAll();

        getCommand("guild").setExecutor(new GuildCommands());

        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
            new Placeholder().register();
        }
    }
}
