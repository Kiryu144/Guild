package de.mevidia.kiryu144.guild.guild;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.*;

public class GuildInstance implements ConfigurationSerializable {
    protected String name;
    protected String shortname;
    protected GuildBalance guildBalance;
    protected List<GuildPlayer> players;
    protected List<UUID> invited;
    protected boolean isPublic;
    protected Location spawnLocation;

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
        this.isPublic = (boolean) serialization.getOrDefault("is_public", false);
        this.spawnLocation = (Location) serialization.getOrDefault("spawn_location", null);
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

    public List<UUID> getInvitedPlayers() {
        return invited;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public void setSpawnLocation(Location spawnLocation) {
        this.spawnLocation = spawnLocation;
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

    public List<GuildPlayer> getAdmins() {
        List<GuildPlayer> admins = new ArrayList<>();
        for(GuildPlayer player : players){
            if(player.isAdmin()){
                admins.add(player);
            }
        }
        return admins;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("short", shortname);
        data.put("guild_balance", guildBalance);
        data.put("players", players);
        data.put("is_public", isPublic);
        data.put("spawn_location", spawnLocation);
        return data;
    }
}
