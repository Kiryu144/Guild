package de.mevidia.kiryu144.guild.guild;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.*;

public class GuildInstance implements ConfigurationSerializable {
    protected String name;
    protected String shortname;
    protected GuildBalance guildBalance;
    protected List<GuildPlayer> players;

    public GuildInstance(String name, String shortname, GuildBalance guildBalance) {
        this.name = name;
        this.shortname = shortname.toLowerCase();
        this.guildBalance = guildBalance;
        this.players = new ArrayList<>();
    }

    public GuildInstance(Map<String, Object> serialization){
        this.name = (String) serialization.getOrDefault("name", "unnamed");
        this.shortname = ((String) serialization.getOrDefault("short", "")).toLowerCase();
        this.guildBalance = (GuildBalance) serialization.getOrDefault("guild_balance", new GuildBalance(0.0));
        this.players = (List<GuildPlayer>) serialization.getOrDefault("players", new ArrayList<GuildPlayer>());
    }

    public String getName() {
        return name;
    }

    public String getShortname() {
        return shortname;
    }

    public GuildBalance getGuildBalance() {
        return guildBalance;
    }

    public List<GuildPlayer> getPlayers() {
        return players;
    }

    /* Warning: High performance impact! */
    public GuildPlayer getPlayer(UUID player){
        for(GuildPlayer guildPlayer : players){
            if(guildPlayer.getPlayer().equals(player)){
                return guildPlayer;
            }
        }

        return null;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("short", shortname);
        data.put("guild_balance", guildBalance);
        data.put("players", players);
        return data;
    }
}
