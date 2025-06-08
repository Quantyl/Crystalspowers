package com.crystalpowers.plugin.commands;

import com.crystalpowers.plugin.CrystalPowersPlugin;
import com.crystalpowers.plugin.gui.CrystalPowerBookGUI;
import com.crystalpowers.plugin.models.CrystalPower;
import com.crystalpowers.plugin.models.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CrystalPowerCommand implements CommandExecutor, TabCompleter {
    private final CrystalPowersPlugin plugin;
    
    public CrystalPowerCommand(CrystalPowersPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players!");
            return true;
        }
        
        Player player = (Player) sender;        

        if (args.length == 0) {
            // Open crystal power GUI
            new CrystalPowerBookGUI(plugin, player).openCrystalPowerBook();
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "select":
                handleSelect(player, args);
                break;
            case "random":
                handleRandom(player);
                break;
            case "info":
                handleInfo(player, args);
                break;
            case "details":
                handleDetails(player, args);
                break;
            case "list":
                handleList(player);
                break;            
            case "gui":
            case "menu":
                handleBook(player);
                break;            case "test":
                handleTest(player);
                break;
            case "debug":
                handleDebug(player, args);
                break;
            case "reload":
                handleReload(player);
                break;
            case "clear":
                handleClear(player);
                break;
            case "reset":
                handleReset(player);
                break;            
            default:
                player.sendMessage(ChatColor.RED + "Usage: /crystalpower [select|random|info|details|list|gui|clear|reset|reload]");
                player.sendMessage(ChatColor.GRAY + "• /crystalpower - Open the crystal power selection GUI");
                player.sendMessage(ChatColor.GRAY + "• /crystalpower gui - Open the crystal power selection GUI");
                player.sendMessage(ChatColor.GRAY + "• /crystalpower menu - Open the crystal power selection GUI"); 
                player.sendMessage(ChatColor.GRAY + "• /crystalpower list - List all available crystal powers");
                player.sendMessage(ChatColor.GRAY + "• /crystalpower info - View your current crystal power");
                break;
        }
        
        return true;
    }

    private void handleSelect(Player player, String[] args) {
        // Check if player already has a crystal power (prevent changing)
        PlayerData existingData = plugin.getPlayerDataManager().getExistingPlayerData(player);
        if (existingData != null && existingData.hasSelectedCrystalPower()) {
            player.sendMessage(ChatColor.RED + "You have already chosen your crystal power! You cannot change it.");
            player.sendMessage(ChatColor.GRAY + "Your current crystal power: " + ChatColor.GOLD + 
                              plugin.getCrystalPowerManager().getCrystalPower(existingData.getCrystalPowerId()).getName());
            return;
        }
          
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /crystalpower select <power>");
            player.sendMessage(ChatColor.YELLOW + "Tip: Use /crystalpower gui to see all available crystal powers!");
            return;
        }
          
        String crystalPowerId = args[1].toLowerCase();
        CrystalPower crystalPower = plugin.getCrystalPowerManager().getCrystalPower(crystalPowerId);
        
        if (crystalPower == null) {
            player.sendMessage(ChatColor.RED + "Crystal power '" + crystalPowerId + "' not found!");
            player.sendMessage(ChatColor.YELLOW + "Use /crystalpower gui to see all available crystal powers!");
            return;
        }
        
        plugin.getPlayerDataManager().setPlayerCrystalPower(player, crystalPowerId);
        player.sendMessage(ChatColor.GREEN + "Successfully selected crystal power: " + ChatColor.GOLD + crystalPower.getName());
        player.sendMessage(ChatColor.GRAY + crystalPower.getDescription());
    }

    private void handleRandom(Player player) {
        // Check if player already has a crystal power (prevent changing)
        PlayerData existingData = plugin.getPlayerDataManager().getExistingPlayerData(player);
        if (existingData != null && existingData.hasSelectedCrystalPower()) {
            player.sendMessage(ChatColor.RED + "You have already chosen your crystal power! You cannot change it.");
            player.sendMessage(ChatColor.GRAY + "Your current crystal power: " + ChatColor.GOLD + 
                              plugin.getCrystalPowerManager().getCrystalPower(existingData.getCrystalPowerId()).getName());
            return;
        }
        
        CrystalPower randomPower = plugin.getCrystalPowerManager().getRandomCrystalPower();
        if (randomPower == null) {
            player.sendMessage(ChatColor.RED + "No crystal powers available!");
            return;
        }
        
        plugin.getPlayerDataManager().setPlayerCrystalPower(player, randomPower.getId());
        player.sendMessage(ChatColor.GREEN + "Randomly selected crystal power: " + ChatColor.GOLD + randomPower.getName());
        player.sendMessage(ChatColor.GRAY + randomPower.getDescription());
    }

    private void handleInfo(Player player, String[] args) {
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);
        
        if (!playerData.hasSelectedCrystalPower()) {
            player.sendMessage(ChatColor.RED + "You haven't selected a crystal power yet!");
            player.sendMessage(ChatColor.YELLOW + "Use /crystalpower gui to choose one!");
            return;
        }
        
        CrystalPower crystalPower = plugin.getCrystalPowerManager().getCrystalPower(playerData.getCrystalPowerId());
        if (crystalPower == null) {
            player.sendMessage(ChatColor.RED + "Your crystal power data seems to be corrupted!");
            return;
        }
        
        player.sendMessage(ChatColor.GOLD + "=== Your Crystal Power ===");
        player.sendMessage(ChatColor.YELLOW + "Name: " + ChatColor.WHITE + crystalPower.getName());
        player.sendMessage(ChatColor.YELLOW + "Description: " + ChatColor.WHITE + crystalPower.getDescription());
        
        // Show abilities
        List<String> abilities = crystalPower.getAbilities();
        if (!abilities.isEmpty()) {
            player.sendMessage(ChatColor.YELLOW + "Abilities:");
            for (String ability : abilities) {
                player.sendMessage(ChatColor.GRAY + "• " + ability);
            }
        }
    }

    private void handleDetails(Player player, String[] args) {
        if (args.length < 2) {
            // Show current crystal power details if no argument provided
            handleInfo(player, args);
            return;
        }
        
        String crystalPowerId = args[1].toLowerCase();
        CrystalPower crystalPower = plugin.getCrystalPowerManager().getCrystalPower(crystalPowerId);
        
        if (crystalPower == null) {
            player.sendMessage(ChatColor.RED + "Crystal power '" + crystalPowerId + "' not found!");
            return;
        }
        
        player.sendMessage(ChatColor.GOLD + "=== " + crystalPower.getName() + " ===");
        player.sendMessage(ChatColor.YELLOW + "ID: " + ChatColor.WHITE + crystalPower.getId());
        player.sendMessage(ChatColor.YELLOW + "Description: " + ChatColor.WHITE + crystalPower.getDescription());
        
        // Show abilities
        List<String> abilities = crystalPower.getAbilities();
        if (!abilities.isEmpty()) {
            player.sendMessage(ChatColor.YELLOW + "Abilities:");
            for (String ability : abilities) {
                player.sendMessage(ChatColor.GRAY + "• " + ability);
            }
        }
    }

    private void handleList(Player player) {
        player.sendMessage(ChatColor.GOLD + "=== Available Crystal Powers ===");
        
        for (CrystalPower crystalPower : plugin.getCrystalPowerManager().getAllCrystalPowers()) {
            player.sendMessage(ChatColor.YELLOW + crystalPower.getName() + 
                             ChatColor.GRAY + " (" + crystalPower.getId() + ") - " + 
                             ChatColor.WHITE + crystalPower.getDescription());
        }
        
        player.sendMessage(ChatColor.YELLOW + "Use /crystalpower details <power> for more information!");
    }

    private void handleBook(Player player) {
        new CrystalPowerBookGUI(plugin, player).openCrystalPowerBook();
    }

    private void handleTest(Player player) {
        if (!player.hasPermission("crystalpowers.admin")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return;
        }
        
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);
        
        player.sendMessage(ChatColor.GOLD + "=== Crystal Power Test Debug ===");
        player.sendMessage(ChatColor.YELLOW + "Player: " + player.getName());
        player.sendMessage(ChatColor.YELLOW + "Has Selected Power: " + playerData.hasSelectedCrystalPower());
        
        if (playerData.hasSelectedCrystalPower()) {
            CrystalPower crystalPower = plugin.getCrystalPowerManager().getCrystalPower(playerData.getCrystalPowerId());
            player.sendMessage(ChatColor.YELLOW + "Crystal Power ID: " + playerData.getCrystalPowerId());
            player.sendMessage(ChatColor.YELLOW + "Crystal Power Name: " + (crystalPower != null ? crystalPower.getName() : "NULL"));
            
            // Test flight functionality for Avian
            if (crystalPower != null && crystalPower.getId().equals("avian")) {
                player.sendMessage(ChatColor.AQUA + "=== Flight Test (Avian) ===");
                player.sendMessage(ChatColor.YELLOW + "Current Flight Allowed: " + player.getAllowFlight());
                player.sendMessage(ChatColor.YELLOW + "Current Flying: " + player.isFlying());
                
                player.sendMessage(ChatColor.GREEN + "Force-reapplying Avian effects...");
                plugin.getPlayerDataManager().applyCrystalPowerEffects(player, playerData);
                
                player.sendMessage(ChatColor.YELLOW + "After Reapply - Flight Allowed: " + player.getAllowFlight());
                player.sendMessage(ChatColor.YELLOW + "After Reapply - Flying: " + player.isFlying());
            }
        }
    }

    private void handleReload(Player player) {
        if (!player.hasPermission("crystalpowers.admin")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return;
        }
        
        plugin.reloadPlugin();
        player.sendMessage(ChatColor.GREEN + "Crystal Powers plugin has been reloaded!");
    }

    private void handleClear(Player player) {
        if (!player.hasPermission("crystalpowers.admin")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return;
        }
        
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);
        if (!playerData.hasSelectedCrystalPower()) {
            player.sendMessage(ChatColor.RED + "You don't have a crystal power to clear!");
            return;
        }
        
        // Remove effects before clearing
        plugin.getPlayerDataManager().removeCrystalPowerEffects(player, playerData);
        
        // Clear the crystal power
        plugin.getPlayerDataManager().clearPlayerCrystalPower(player);
        player.sendMessage(ChatColor.GREEN + "Your crystal power has been cleared!");
    }

    private void handleReset(Player player) {
        if (!player.hasPermission("crystalpowers.admin")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return;
        }
        
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);
        if (!playerData.hasSelectedCrystalPower()) {
            player.sendMessage(ChatColor.RED + "You don't have a crystal power to reset!");
            return;
        }
        
        // Remove effects, clear, and allow re-selection
        plugin.getPlayerDataManager().removeCrystalPowerEffects(player, playerData);
        plugin.getPlayerDataManager().resetPlayerCrystalPower(player);
        player.sendMessage(ChatColor.GREEN + "Your crystal power has been reset! You can now choose a new one.");
    }

    private void handleDebug(Player player, String[] args) {
        if (!player.hasPermission("crystalpowers.admin")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return;
        }
        
        PlayerData playerData = plugin.getPlayerDataManager().getExistingPlayerData(player);
        
        player.sendMessage(ChatColor.GOLD + "=== Crystal Powers Debug Info ===");
        player.sendMessage(ChatColor.YELLOW + "Player: " + player.getName());
        player.sendMessage(ChatColor.YELLOW + "Has Data: " + (playerData != null));
        
        if (playerData != null) {
            player.sendMessage(ChatColor.YELLOW + "Has Crystal Power: " + playerData.hasSelectedCrystalPower());
            if (playerData.hasSelectedCrystalPower()) {
                String powerId = playerData.getCrystalPowerId();
                CrystalPower power = plugin.getCrystalPowerManager().getCrystalPower(powerId);
                player.sendMessage(ChatColor.YELLOW + "Crystal Power ID: " + powerId);
                player.sendMessage(ChatColor.YELLOW + "Crystal Power Name: " + (power != null ? power.getName() : "NULL"));
                
                if (power != null) {
                    var props = power.getProperties();
                    player.sendMessage(ChatColor.YELLOW + "Can Fly: " + props.canFly());
                    player.sendMessage(ChatColor.YELLOW + "Max Health: " + props.getMaxHealth());
                    player.sendMessage(ChatColor.YELLOW + "Current Health: " + player.getHealth());
                    player.sendMessage(ChatColor.YELLOW + "Allow Flight: " + player.getAllowFlight());
                    player.sendMessage(ChatColor.YELLOW + "Walk Speed: " + player.getWalkSpeed());
                    player.sendMessage(ChatColor.YELLOW + "Active Potion Effects: " + player.getActivePotionEffects().size());
                    
                    // List active effects
                    for (var effect : player.getActivePotionEffects()) {
                        player.sendMessage(ChatColor.GRAY + "  - " + effect.getType().getName() + " " + effect.getAmplifier());
                    }
                }
            }
        }
        
        player.sendMessage(ChatColor.YELLOW + "Game Mode: " + player.getGameMode());
        player.sendMessage(ChatColor.YELLOW + "Available Powers: " + plugin.getCrystalPowerManager().getAllCrystalPowers().size());
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            String[] subcommands = {"select", "random", "info", "details", "list", "gui", "menu", "test", "reload", "clear", "reset"};
            for (String sub : subcommands) {
                if (sub.toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(sub);
                }
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("select") || args[0].equalsIgnoreCase("details")) {
                // Tab complete crystal power names
                for (CrystalPower crystalPower : plugin.getCrystalPowerManager().getAllCrystalPowers()) {
                    if (crystalPower.getId().toLowerCase().startsWith(args[1].toLowerCase())) {
                        completions.add(crystalPower.getId());
                    }
                }
            }
        }
        
        return completions;
    }
}
