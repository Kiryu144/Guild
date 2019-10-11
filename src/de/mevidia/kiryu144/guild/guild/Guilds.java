package de.mevidia.kiryu144.guild.guild;

import de.mevidia.kiryu144.guild.Guild;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class Guilds implements Listener {
    protected HashMap<String, GuildInstance> shortReference;
    protected HashMap<UUID, GuildInstance> playerGuilds;

    public Guilds() {
        shortReference = new HashMap<>();
        playerGuilds = new HashMap<>();
    }

    public File getGuildInstanceFolder(){
        return new File(Guild.instance.getDataFolder() + "/guilds/");
    }

    public File getGuildInstanceFile(GuildInstance instance){
        return new File(getGuildInstanceFolder() + "/" + ((instance != null) ? instance.getShortname() : "null") + ".yml");
    }

    public void addGuild(GuildInstance guild){
        shortReference.put(guild.getShortname(), guild);
        for(GuildPlayer guildPlayer : guild.getPlayers()){
            GuildInstance previous = playerGuilds.put(guildPlayer.getPlayer(), guild);
            if(previous != null){
                // If this point is reached, it means the player is stored in multiple guilds.
                // This should never happen.
                Guild.instance.getLogger().severe(String.format("Player '%s' is stored in multiple guilds. Please don't edit guild configs if not needed!", guildPlayer.getPlayer().toString()));
            }
        }
    }

    public void leaveGuild(UUID uuid){
        GuildInstance guild = Guild.guilds.getGuild(uuid);
        guild.getPlayers().remove(guild.getPlayer(uuid));
        playerGuilds.remove(uuid);
    }

    public void deleteGuild(GuildInstance guild) {
        shortReference.remove(guild.getShortname());
        for(GuildPlayer player : guild.getPlayers()){
            playerGuilds.remove(player.getPlayer());
            //TODO: Add message for players in guild
        }
        guild.getPlayers().clear();
        getGuildInstanceFile(guild).delete();
    }

    public void save(GuildInstance instance) throws IOException {
        File file = getGuildInstanceFile(instance);
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        yamlConfiguration.set("instance", instance);
        yamlConfiguration.save(file);
    }

    public GuildInstance load(File file) throws IOException, InvalidConfigurationException {
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        yamlConfiguration.load(file);
        return (GuildInstance) yamlConfiguration.get("instance");
    }

    public void loadAll() {
        getGuildInstanceFolder().mkdirs();
        for(File file : getGuildInstanceFolder().listFiles()){
            if(file.isFile() && file.getName().endsWith(".yml")) {
                try {
                    GuildInstance instance = load(file);
                    addGuild(instance);
                } catch (IOException | InvalidConfigurationException e) {
                    Guild.instance.getLogger().warning("Error while loading guild file " + file.getName());
                    e.printStackTrace();
                }
            }
        }
        Guild.instance.getLogger().info(String.format("Loaded %d guilds", shortReference.size()));
    }

    public GuildInstance getGuild(UUID uuid){
        return playerGuilds.get(uuid);
    }

    public GuildInstance getGuild(Player player){
        return playerGuilds.get(player.getUniqueId());
    }

    public GuildInstance getGuild(String shortname){
        return shortReference.get(shortname.toLowerCase());
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event){
        GuildInstance guild = getGuild(event.getPlayer());
        if(guild != null){
            event.setFormat("§7[§a" + guild.getShortname().toUpperCase() + "§7]§r" + event.getFormat());
        }
    }
}
