package de.mevidia.kiryu144.guild.guild;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;

public class GuildBalance implements ConfigurationSerializable {
    protected double balance;

    public GuildBalance(double balance) {
        this.balance = balance;
    }

    public GuildBalance(Map<String, Object> serialization){
        this.balance = (Double) serialization.getOrDefault("balance", 0D);
    }

    public double getBalance() {
        return balance;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        data.put("balance", balance);
        return data;
    }
}
