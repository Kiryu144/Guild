package de.mevidia.kiryu144.guild.command;

import de.mevidia.kiryu144.guild.Guild;
import de.mevidia.kiryu144.guild.guild.GuildBalance;
import de.mevidia.kiryu144.guild.guild.GuildInstance;
import de.mevidia.kiryu144.guild.guild.GuildPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;

public class GuildCommands implements CommandExecutor {

    protected void save(GuildInstance guild, Player caller){
        try {
            Guild.guilds.save(guild);
        } catch (IOException e) {
            caller.sendMessage("§cCould not save to config. Please report to an administrator!");
            e.printStackTrace();
        }
    }

    public void createGuild(Player caller, String guildShort, String guildName){
        boolean shortExists = Guild.guilds.getGuild(guildShort) != null;
        boolean playerAlreadyInGuild =  Guild.guilds.getGuild(caller) != null;
        boolean isGuildNameGood = guildName.length() >= 4 && guildName.length() <= 16;
        boolean isGuildShortGood = guildShort.length() > 0 && guildShort.length() <= 4;

        if(!isGuildNameGood){
            caller.sendMessage("§cDer Gildenname muss 4-16 Zeichen beinhalten.");
            return;
        }

        if(!isGuildShortGood){
            caller.sendMessage("§cDer Gildenkürzel muss 1-4 Zeichen beinhalten.");
            return;
        }

        if(shortExists){
            caller.sendMessage(String.format("§cEs existiert schon eine Gilde mit dem kürzel '%s'.", guildShort.toUpperCase()));
            return;
        }

        if(playerAlreadyInGuild){
            caller.sendMessage("§cDu bist bereits in einer Gilde.");
            return;
        }

        GuildInstance guild = new GuildInstance(guildName, guildShort, new GuildBalance(0.0));
        guild.getPlayers().add(new GuildPlayer(caller.getUniqueId(), true));
        Guild.guilds.addGuild(guild);
        caller.sendMessage(String.format("§bGilde '%s' wurde erstellt!", guild.getName()));
        save(guild, caller);
    }

    public void deleteGuild(Player caller){
        GuildInstance guild = Guild.guilds.getGuild(caller);

        if(guild == null){
            caller.sendMessage("§cDu bist in keiner Gilde.");
            return;
        }

        if(!guild.getPlayer(caller.getUniqueId()).isAdmin()){
            caller.sendMessage("§cDu hast keine Rechte dafür.");
            return;
        }

        Guild.guilds.deleteGuild(guild);
        caller.sendMessage(String.format("§bGilde '%s' wurde gelöscht!", guild.getName()));
    }

    public void leaveGuild(Player caller){
        GuildInstance guild = Guild.guilds.getGuild(caller);

        if(guild == null){
            caller.sendMessage("§cDu bist in keiner Gilde.");
            return;
        }

        //TODO: Prevent leaving of last guild admin

        Guild.guilds.leaveGuild(caller.getUniqueId());
        save(guild, caller);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if(args.length >= 3 && args[0].equalsIgnoreCase("create") && commandSender instanceof Player){
            String        guildShort = args[1].toLowerCase();
            StringBuilder guildNameBuilder  = new StringBuilder();
            for(int i = 2; i < args.length; ++i){
                guildNameBuilder.append(args[i]).append(' ');
            }
            guildNameBuilder.deleteCharAt(guildNameBuilder.length() - 1); //< Remove trailing space
            String guildName = guildNameBuilder.toString();
            createGuild((Player) commandSender, guildShort, guildName);
            return true;
        }

        if(args.length == 1 && args[0].equalsIgnoreCase("delete") && commandSender instanceof Player){
            deleteGuild((Player) commandSender);
            return true;
        }

        if(args.length == 1 && args[0].equalsIgnoreCase("leave") && commandSender instanceof Player){
            leaveGuild((Player) commandSender);
            return true;
        }


        return false;
    }
}
