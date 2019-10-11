package de.mevidia.kiryu144.guild.guild;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GuildPlayer implements ConfigurationSerializable {
    protected UUID player;
    protected boolean isAdmin;

    public GuildPlayer(UUID player, boolean isAdmin) {
        this.player = player;
        this.isAdmin = isAdmin;
    }

    public GuildPlayer(Map<String, Object> serialize) {
        this.player = UUID.fromString((String) serialize.getOrDefault("player", UUID.randomUUID().toString()));
        this.isAdmin = (Boolean) serialize.getOrDefault("is_admin", false);
    }

    public UUID getPlayer() {
        return player;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> serialize = new HashMap<>();
        serialize.put("player", player.toString());
        serialize.put("is_admin", isAdmin);
        return serialize;
    }
}
