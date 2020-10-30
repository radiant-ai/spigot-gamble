package com.github.radiant.gamble;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Gamble extends JavaPlugin implements CommandExecutor {
    private static Map<UUID, Long> cooldown = new HashMap<UUID, Long>();
    private boolean shulkerGamble = false;
    private int cooldownTime = 15;
    private double winChance = 0.5;

    public void onEnable() {
        this.getCommand("gamble").setExecutor(this);
        this.saveDefaultConfig();
        this.getConfig().options().copyDefaults(true);
        shulkerGamble = this.getConfig().getBoolean("shulker-gamble");
        cooldownTime = this.getConfig().getInt("cooldown");
        winChance = this.getConfig().getDouble("win-chance");
        Bukkit.getLogger().info("Gamble50 was enabled!");
    }

    public void onDisable() {

    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (!cooldown.containsKey(p.getUniqueId()) || System.currentTimeMillis() - cooldown.get(p.getUniqueId()) > cooldownTime*1000) {
                ItemStack is = p.getInventory().getItemInMainHand();
                if (is != null && is.getType() != Material.AIR && (shulkerGamble || !is.getType().name().contains("SHULKER_BOX"))) {
                    if (Math.random() < 0.5) {
                        Map<Integer, ItemStack> m = p.getInventory().addItem(is);
                        if (!m.isEmpty()) {
                            for (ItemStack item : m.values()) {
                                p.getLocation().getWorld().dropItemNaturally(p.getLocation().clone().add(0, 0.5, 0), item);
                            }
                        }
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aYou managed to double your items in hand!"));
                    }
                    else {
                        p.getInventory().setItemInMainHand(null);
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou were unlucky :("));
                    }
                    cooldown.put(p.getUniqueId(), System.currentTimeMillis());
                }
                else {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou cannot gamble with this :("));
                }
            }
            else {
                int secondsLeft = (int) (cooldownTime*1000 - (System.currentTimeMillis() - cooldown.get(p.getUniqueId())))/1000;
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e"+ secondsLeft +" seconds &cleft until you can try your luck again!"));
            }
        }
        return false;
    }
}
