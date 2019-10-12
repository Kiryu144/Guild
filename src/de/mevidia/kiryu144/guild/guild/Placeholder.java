package de.mevidia.kiryu144.guild.guild;

import de.mevidia.kiryu144.guild.Guild;
import de.mevidia.kiryu144.guild.guild.GuildInstance;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;

public class Placeholder extends PlaceholderExpansion {

    @Override
    public boolean canRegister(){
        return true;
    }

    @Override
    public String getIdentifier() {
        return "guild";
    }

    @Override
    public String getAuthor() {
        return "Kiryu144";
    }

    @Override
    public String getVersion() {
        return Guild.instance.getDescription().getVersion();
    }

    @Override
    public String onRequest(OfflinePlayer p, String identifier) {
        if(identifier.equalsIgnoreCase("prefix")){
            GuildInstance guild = Guild.guilds.getGuild(p.getUniqueId());
            if(guild != null){
                return guild.getShortname().toUpperCase();
            }else{
                return "";
            }
        }

        if(identifier.equalsIgnoreCase("name")){
            GuildInstance guild = Guild.guilds.getGuild(p.getUniqueId());
            if(guild != null){
                return guild.getName();
            }else{
                return "";
            }
        }

        if(identifier.equalsIgnoreCase("balance")){
            GuildInstance guild = Guild.guilds.getGuild(p.getUniqueId());
            if(guild != null){
                return guild.getGuildBalance().toString();
            }else{
                return "0";
            }
        }

        return null;
    }
}
