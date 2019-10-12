package de.mevidia.kiryu144.guild.command;

import de.mevidia.kiryu144.guild.Guild;
import de.mevidia.kiryu144.guild.guild.GuildBalance;
import de.mevidia.kiryu144.guild.guild.GuildInstance;
import de.mevidia.kiryu144.guild.guild.GuildPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.UUID;

public class GuildCommands implements CommandExecutor {

    protected void save(GuildInstance guild, Player caller){
        try {
            Guild.guilds.save(guild);
        } catch (IOException e) {
            caller.sendMessage("§cCould not save to config. Please report to an administrator!");
            e.printStackTrace();
        }
    }

    public void displayCommands(Player caller){
        caller.sendMessage("§bVerfügbare Befehle:");
        caller.sendMessage("§7/guild §6create §7<kürzel> <name>");
        caller.sendMessage("§7/guild §6delete");
        caller.sendMessage("§7/guild §6leave");
        caller.sendMessage("§7/guild §6invite §7<spieler>");
        caller.sendMessage("§7/guild §6join §7<gilde>");
        caller.sendMessage("§7/guild §6setspawn");
        caller.sendMessage("§7/guild §6promote §7<spieler>");
        caller.sendMessage("§7/guild §6deposit §7<betrag>");
        caller.sendMessage("§7/guild §6withdraw §7<betrag>");
        caller.sendMessage("§7/guild §6balance");
        caller.sendMessage("§7/guild §6top");
    }

    public boolean isGuildAdmin(Player caller){
        GuildInstance guild = Guild.guilds.getGuild(caller);

        if(guild == null){
            caller.sendMessage("§cDu bist in keiner Gilde.");
            return false;
        }

        if(!guild.getPlayer(caller.getUniqueId()).isAdmin()){
            caller.sendMessage("§cDu bist kein Gildenadmin.");
            return false;
        }
        return true;
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

        if(guild.getPlayer(caller.getUniqueId()).isAdmin() && guild.getAdmins().size() == 1) {
            caller.sendMessage("§cDu bist der letzte Administrator in dieser Gilde. Um die Gilde zu löschen gib '/guild delete' ein.");
            return;
        }

        Guild.guilds.leaveGuild(caller.getUniqueId());
        save(guild, caller);
    }

    public void inviteToGuild(Player caller, Player invited){
        GuildInstance guild = Guild.guilds.getGuild(caller);

        if(guild == null){
            caller.sendMessage("§cDu bist in keiner Gilde.");
            return;
        }

        if(invited == null){
            caller.sendMessage("§cDer Spieler ist nicht online.");
            return;
        }

        UUID invitedUUID = invited.getUniqueId();
        if(guild.getInvitedPlayers().contains(invitedUUID)){
            caller.sendMessage("§cDer Spieler hat bereits eine Einladung zu deiner Gilde bekommen.");
            return;
        }

        invited.sendMessage(String.format("§bDu wurdest in die Gilde '%s' eingeladen. Um der Gilde beizutreten gib '/guilds join %s' ein!", guild.getName(), guild.getShortname()));
        caller.sendMessage("§bEinladung verschickt!");
    }

    public void joinGuild(Player caller, String guildShort){
        GuildInstance guild = Guild.guilds.getGuild(guildShort);

        if(guild == null){
            caller.sendMessage("§cDiese Gilde existiert nicht!");
            return;
        }

        if(Guild.guilds.getGuild(caller) != null){
            caller.sendMessage("§cDu bist bereits in einer Gilde!");
            return;
        }

        if(guild.getInvitedPlayers().remove(caller.getUniqueId())){
            // Player has invite
            Guild.guilds.joinGuild(guild, caller.getUniqueId());
        } else {
            if(guild.isPublic()){
                Guild.guilds.joinGuild(guild, caller.getUniqueId());
            }else{
                caller.sendMessage("§cDu brauchst eine Einladung um dieser Gilde beitreten zu können.");
                return;
            }
        }

        caller.sendMessage(String.format("§bDu bist '%s' beigetreten!", guild.getName()));
        save(guild, caller);
    }

    public void setSpawn(Player caller){
        GuildInstance guild = Guild.guilds.getGuild(caller);

        if(!isGuildAdmin(caller)) return;

        guild.setSpawnLocation(caller.getLocation());
        caller.sendMessage("§bGildenspawn wurde gesetzt.");
    }

    public void promote(Player caller, Player other){
        GuildInstance guild = Guild.guilds.getGuild(caller);

        if(!isGuildAdmin(caller)) return;

        if(other == null){
            caller.sendMessage("§cDer Spieler ist nicht online.");
            return;
        }

        if(guild != Guild.guilds.getGuild(other)){
            caller.sendMessage("§cDieser Spieler befindet sich nicht in deiner Gilde.");
            return;
        }

        guild.getPlayer(other.getUniqueId()).setAdmin(true);
        caller.sendMessage("§bSpieler wurde zum Gildenadministrator.");
        other.sendMessage("§bDu bist nun Gildenadministrator");
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

        if(args.length == 2 && args[0].equalsIgnoreCase("invite") && commandSender instanceof Player){
            Player invited = Bukkit.getPlayer(args[1]);
            inviteToGuild((Player) commandSender, invited);
            return true;
        }

        if(args.length == 2 && args[0].equalsIgnoreCase("join") && commandSender instanceof Player){
            String guildShort = args[1].toLowerCase();
            joinGuild((Player) commandSender, guildShort.toLowerCase());
            return true;
        }

        if(args.length == 1 && args[0].equalsIgnoreCase("setspawn") && commandSender instanceof Player){
            setSpawn((Player) commandSender);
            return true;
        }

        if(args.length == 2 && args[0].equalsIgnoreCase("promote") && commandSender instanceof Player){
            Player toPromote = Bukkit.getPlayer(args[1]);
            promote((Player) commandSender, toPromote);
            return true;
        }

        displayCommands((Player) commandSender);
        return true;
    }
}
