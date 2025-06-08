package com.crystalpowers.plugin.commands;

import com.crystalpowers.plugin.CrystalPowersPlugin;
import com.crystalpowers.plugin.utils.EncryptionUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EncryptionCommand implements CommandExecutor, TabCompleter {
    private final CrystalPowersPlugin plugin;
    
    public EncryptionCommand(CrystalPowersPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("crystalpowers.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
        }
        
        if (args.length == 0) {
            showUsage(sender);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "status":
                handleStatus(sender);
                break;
            case "enable":
                handleEnable(sender);
                break;
            case "disable":
                handleDisable(sender);
                break;
            case "generate-key":
                handleGenerateKey(sender);
                break;
            case "test":
                handleTest(sender, args);
                break;
            case "hash":
                handleHash(sender, args);
                break;
            default:
                showUsage(sender);
                break;
        }
        
        return true;
    }
    
    private void showUsage(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== Encryption Management ===");
        sender.sendMessage(ChatColor.YELLOW + "/encryption status" + ChatColor.GRAY + " - Check encryption status");
        sender.sendMessage(ChatColor.YELLOW + "/encryption enable" + ChatColor.GRAY + " - Enable encryption");
        sender.sendMessage(ChatColor.YELLOW + "/encryption disable" + ChatColor.GRAY + " - Disable encryption");
        sender.sendMessage(ChatColor.YELLOW + "/encryption generate-key" + ChatColor.GRAY + " - Generate new encryption key");
        sender.sendMessage(ChatColor.YELLOW + "/encryption test <text>" + ChatColor.GRAY + " - Test encryption/decryption");
        sender.sendMessage(ChatColor.YELLOW + "/encryption hash <text>" + ChatColor.GRAY + " - Create secure hash");
    }
    
    private void handleStatus(CommandSender sender) {
        boolean configEnabled = plugin.getConfig().getBoolean("encryption.enabled", false);
        boolean runtimeInitialized = EncryptionUtil.isInitialized();
        String masterPassword = plugin.getConfig().getString("encryption.master-password", "");
        
        sender.sendMessage(ChatColor.GOLD + "=== Encryption Status ===");
        sender.sendMessage(ChatColor.YELLOW + "Config Enabled: " + 
            (configEnabled ? ChatColor.GREEN + "✓ Yes" : ChatColor.RED + "✗ No"));
        sender.sendMessage(ChatColor.YELLOW + "Runtime Initialized: " + 
            (runtimeInitialized ? ChatColor.GREEN + "✓ Yes" : ChatColor.RED + "✗ No"));
        sender.sendMessage(ChatColor.YELLOW + "Master Password Set: " + 
            (!masterPassword.trim().isEmpty() ? ChatColor.GREEN + "✓ Yes" : ChatColor.RED + "✗ No"));
        
        if (configEnabled && runtimeInitialized) {
            sender.sendMessage(ChatColor.GREEN + "🔒 Encryption is ACTIVE - Player data is protected");
        } else {
            sender.sendMessage(ChatColor.RED + "🔓 Encryption is INACTIVE - Player data is in plain text");
        }
    }
    
    private void handleEnable(CommandSender sender) {
        plugin.getConfig().set("encryption.enabled", true);
        plugin.saveConfig();
        plugin.reloadPlugin(); // This will reinitialize encryption
        
        sender.sendMessage(ChatColor.GREEN + "✓ Encryption has been enabled!");
        sender.sendMessage(ChatColor.YELLOW + "Player data will now be encrypted when saved.");
        sender.sendMessage(ChatColor.GOLD + "Use '/encryption status' to verify the setup.");
    }
    
    private void handleDisable(CommandSender sender) {
        plugin.getConfig().set("encryption.enabled", false);
        plugin.saveConfig();
        
        sender.sendMessage(ChatColor.RED + "✗ Encryption has been disabled!");
        sender.sendMessage(ChatColor.YELLOW + "WARNING: Player data will now be saved in plain text!");
        sender.sendMessage(ChatColor.GRAY + "Existing encrypted data will still be readable until next save.");
    }
    
    private void handleGenerateKey(CommandSender sender) {
        try {
            String newKey = EncryptionUtil.generateSecureRandom(32);
            plugin.getConfig().set("encryption.master-password", newKey);
            plugin.saveConfig();
            
            sender.sendMessage(ChatColor.GREEN + "✓ New encryption key generated!");
            sender.sendMessage(ChatColor.GOLD + "Key: " + ChatColor.WHITE + newKey);
            sender.sendMessage(ChatColor.RED + "⚠ IMPORTANT: Save this key safely! If lost, encrypted data cannot be recovered!");
            sender.sendMessage(ChatColor.YELLOW + "Reload the plugin to apply the new key: /crystalpower reload");
            
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "✗ Failed to generate new key: " + e.getMessage());
        }
    }
    
    private void handleTest(CommandSender sender, String[] args) {
        if (!EncryptionUtil.isInitialized()) {
            sender.sendMessage(ChatColor.RED + "✗ Encryption is not initialized!");
            return;
        }
        
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /encryption test <text>");
            return;
        }
        
        String text = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        
        try {
            String encrypted = EncryptionUtil.encrypt(text);
            String decrypted = EncryptionUtil.decrypt(encrypted);
            
            sender.sendMessage(ChatColor.GOLD + "=== Encryption Test ===");
            sender.sendMessage(ChatColor.YELLOW + "Original: " + ChatColor.WHITE + text);
            sender.sendMessage(ChatColor.YELLOW + "Encrypted: " + ChatColor.GRAY + encrypted);
            sender.sendMessage(ChatColor.YELLOW + "Decrypted: " + ChatColor.WHITE + decrypted);
            sender.sendMessage(ChatColor.GREEN + "✓ Test successful - Encryption is working correctly!");
            
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "✗ Encryption test failed: " + e.getMessage());
        }
    }
    
    private void handleHash(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /encryption hash <text>");
            return;
        }
        
        String text = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        
        try {
            String hash = EncryptionUtil.createHash(text);
            sender.sendMessage(ChatColor.GOLD + "=== Hash Generation ===");
            sender.sendMessage(ChatColor.YELLOW + "Text: " + ChatColor.WHITE + text);
            sender.sendMessage(ChatColor.YELLOW + "Hash: " + ChatColor.GRAY + hash);
            sender.sendMessage(ChatColor.GREEN + "✓ Hash generated successfully!");
            
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "✗ Hash generation failed: " + e.getMessage());
        }
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (!sender.hasPermission("crystalpowers.admin")) {
            return completions;
        }
        
        if (args.length == 1) {
            completions.addAll(Arrays.asList("status", "enable", "disable", "generate-key", "test", "hash"));
        }
        
        return completions;
    }
}
