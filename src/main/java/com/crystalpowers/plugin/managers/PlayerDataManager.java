package com.crystalpowers.plugin.managers;

import com.crystalpowers.plugin.CrystalPowersPlugin;
import com.crystalpowers.plugin.models.PlayerData;
import com.crystalpowers.plugin.models.CrystalPower;
import com.crystalpowers.plugin.utils.EncryptionUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.attribute.Attribute;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataManager {
    private final CrystalPowersPlugin plugin;
    private final Map<UUID, PlayerData> playerDataMap;
    private final File dataFile;
    private FileConfiguration dataConfig;
    
    public PlayerDataManager(CrystalPowersPlugin plugin) {
        this.plugin = plugin;
        this.playerDataMap = new HashMap<>();
        this.dataFile = new File(plugin.getDataFolder(), "playerdata.yml");
        loadData();
    }
    
    private void loadData() {
        if (!dataFile.exists()) {
            try {
                dataFile.getParentFile().mkdirs();
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create playerdata.yml file!");
                return;
            }
        }
        
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        
        // Load existing player data
        if (dataConfig.getConfigurationSection("players") != null) {
            for (String uuidString : dataConfig.getConfigurationSection("players").getKeys(false)) {
                try {                    
                    UUID uuid = UUID.fromString(uuidString);
                    PlayerData data = new PlayerData(uuid);
                    
                    String crystalPowerId = dataConfig.getString("players." + uuidString + ".crystalpower");
                    if (crystalPowerId != null) {
                        // Try to decrypt crystal power ID if encryption is enabled
                        if (EncryptionUtil.isInitialized()) {
                            try {
                                crystalPowerId = EncryptionUtil.decrypt(crystalPowerId);
                            } catch (Exception e) {
                                plugin.getLogger().warning("Failed to decrypt crystal power for player " + uuid + ": " + e.getMessage());
                                continue;
                            }
                        }
                        data.setCrystalPowerId(crystalPowerId);
                    }
                    
                    playerDataMap.put(uuid, data);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid UUID in playerdata.yml: " + uuidString);
                }
            }
        }
        
        plugin.getLogger().info("Loaded " + playerDataMap.size() + " player data entries");
    }
    
    public void saveData() {
        for (PlayerData data : playerDataMap.values()) {
            String uuidString = data.getPlayerId().toString();
            
            if (data.hasSelectedCrystalPower()) {
                String crystalPowerId = data.getCrystalPowerId();
                
                // Encrypt crystal power ID if encryption is enabled
                if (EncryptionUtil.isInitialized()) {
                    try {
                        crystalPowerId = EncryptionUtil.encrypt(crystalPowerId);
                    } catch (Exception e) {
                        plugin.getLogger().severe("Failed to encrypt crystal power for player " + data.getPlayerId() + ": " + e.getMessage());
                        continue;
                    }
                }
                
                dataConfig.set("players." + uuidString + ".crystalpower", crystalPowerId);
            }
        }
        
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save playerdata.yml file!");
        }
    }
    
    public void saveAllData() {
        saveData();
    }
    
    public PlayerData getPlayerData(Player player) {
        return playerDataMap.computeIfAbsent(player.getUniqueId(), k -> new PlayerData(player.getUniqueId()));
    }
      public PlayerData getExistingPlayerData(Player player) {
        return playerDataMap.get(player.getUniqueId());
    }
    
    public boolean hasPlayerData(Player player) {
        return playerDataMap.containsKey(player.getUniqueId());
    }
    
    public void setPlayerCrystalPower(Player player, String crystalPowerId) {
        PlayerData data = getPlayerData(player);
        
        // Remove old effects first if player had a crystal power
        if (data.hasSelectedCrystalPower()) {
            removeCrystalPowerEffects(player, data);
        }
        
        data.setCrystalPowerId(crystalPowerId);
        applyCrystalPowerEffects(player, data);
        saveData();
        
        plugin.getLogger().info("Player " + player.getName() + " selected crystal power: " + crystalPowerId);
    }
    
    public void clearPlayerCrystalPower(Player player) {
        PlayerData data = getPlayerData(player);
        if (data.hasSelectedCrystalPower()) {
            removeCrystalPowerEffects(player, data);
            data.setCrystalPowerId(null);
            saveData();
        }
    }
    
    public void resetPlayerCrystalPower(Player player) {
        clearPlayerCrystalPower(player);
        // Allow re-selection by creating fresh data
        playerDataMap.put(player.getUniqueId(), new PlayerData(player.getUniqueId()));
    }
      public void applyCrystalPowerEffects(Player player, PlayerData data) {
        if (!data.hasSelectedCrystalPower()) {
            return;
        }
        
        CrystalPower crystalPower = plugin.getCrystalPowerManager().getCrystalPower(data.getCrystalPowerId());
        if (crystalPower == null) {
            plugin.getLogger().warning("Crystal power not found: " + data.getCrystalPowerId());
            return;
        }
        
        // Debug logging for flight issue
        plugin.getLogger().info("[DEBUG] Applying crystal power effects for " + player.getName() + 
                               " with power: " + crystalPower.getName());
        
        CrystalPower.CrystalPowerProperties props = crystalPower.getProperties();
        
        // Set max health
        if (props.getMaxHealth() != 20) {
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(props.getMaxHealth());
            plugin.getLogger().info("[DEBUG] Set max health to " + props.getMaxHealth() + " for " + player.getName());
        }
        
        // Apply flight - ensure player is in survival mode and can fly
        if (props.canFly()) {
            // Only apply flight if player is in survival mode
            if (player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE) {
                player.setAllowFlight(true);
                plugin.getLogger().info("[DEBUG] Enabled flight for " + player.getName() + " (Avian power)");
                plugin.getLogger().info("[DEBUG] Player.getAllowFlight() = " + player.getAllowFlight());
                plugin.getLogger().info("[DEBUG] Player.getGameMode() = " + player.getGameMode());
                
                // Schedule a delayed check to ensure flight persists
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                    if (!player.getAllowFlight()) {
                        player.setAllowFlight(true);
                        plugin.getLogger().info("[DEBUG] Re-enabled flight for " + player.getName() + " (was disabled)");
                    }
                }, 20L); // 1 second later
            } else {
                plugin.getLogger().info("[DEBUG] Skipping flight for " + player.getName() + " - not in survival/adventure mode");
            }
        }
        
        // Apply potion effects
        for (PotionEffect effect : props.getPotionEffects()) {
            player.addPotionEffect(effect, true);
            plugin.getLogger().info("[DEBUG] Applied potion effect " + effect.getType().getName() + " to " + player.getName());
        }
          // Apply permanent potion effects from crystal power
        for (PotionEffect effect : crystalPower.getPermanentEffects()) {
            player.addPotionEffect(effect, true);
            plugin.getLogger().info("[DEBUG] Applied permanent effect " + effect.getType().getName() + " to " + player.getName());
        }
          // Apply special abilities for specific crystal powers
        if (data.getCrystalPowerId().equals("elytrian")) {
            // Give Elytrian natural elytra
            if (player.getInventory().getChestplate() == null || 
                player.getInventory().getChestplate().getType() == Material.AIR) {
                player.getInventory().setChestplate(new ItemStack(Material.ELYTRA));
                player.sendMessage(ChatColor.YELLOW + "Your natural wings have manifested as elytra!");
            }
        }
        
        // Apply land speed changes
        if (props.getLandSpeed() != 1.0f) {
            float speed = 0.2f * props.getLandSpeed();
            player.setWalkSpeed(speed);
            plugin.getLogger().info("[DEBUG] Set walk speed to " + speed + " for " + player.getName());
        }
        
        plugin.getLogger().info("[DEBUG] Finished applying effects for " + player.getName());
    }
    
    public void removeCrystalPowerEffects(Player player, PlayerData data) {
        if (!data.hasSelectedCrystalPower()) {
            return;
        }
        
        CrystalPower crystalPower = plugin.getCrystalPowerManager().getCrystalPower(data.getCrystalPowerId());
        if (crystalPower == null) {
            return;
        }
        
        CrystalPower.CrystalPowerProperties props = crystalPower.getProperties();
        
        // Reset flight
        if (props.canFly() && player.getGameMode() != GameMode.CREATIVE && player.getGameMode() != GameMode.SPECTATOR) {
            player.setAllowFlight(false);
            player.setFlying(false);
        }
          // Reset max health
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20.0);
        
        // Reset walk speed
        player.setWalkSpeed(0.2f);
        
        // Remove potion effects
        for (PotionEffect effect : props.getPotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        
        for (PotionEffect effect : crystalPower.getPermanentEffects()) {
            player.removePotionEffect(effect.getType());
        }
        
        // Remove special items for specific crystal powers
        if (data.getCrystalPowerId().equals("elytrian")) {
            // Remove elytra if it was auto-equipped
            if (player.getInventory().getChestplate() != null && 
                player.getInventory().getChestplate().getType() == Material.ELYTRA) {
                player.getInventory().setChestplate(null);
            }
        }
    }
      public void onPlayerJoin(Player player) {
        PlayerData data = getPlayerData(player);
        if (data.hasSelectedCrystalPower()) {
            // Delay effect application slightly to ensure player is fully loaded
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                applyCrystalPowerEffects(player, data);
                
                // Additional check for Avian flight after a longer delay
                if ("avian".equals(data.getCrystalPowerId())) {
                    plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                        if (!player.getAllowFlight() && (player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE)) {
                            player.setAllowFlight(true);
                            plugin.getLogger().info("[DEBUG] Re-applied Avian flight for " + player.getName() + " after join delay");
                        }
                    }, 40L); // 2 second additional delay
                }
            }, 10L); // 0.5 second delay
        }
    }
    
    public void onPlayerQuit(Player player) {
        // Save data when player leaves
        saveData();
    }
    
    public void applyCrystalPowerToPlayer(Player player) {
        PlayerData data = getExistingPlayerData(player);
        if (data != null && data.hasSelectedCrystalPower()) {
            applyCrystalPowerEffects(player, data.getCrystalPowerId());
        }
    }

    private void applyCrystalPowerEffects(Player player, String crystalPowerId) {
        if (crystalPowerId == null) {
            plugin.getLogger().info("DEBUG: crystalPowerId is null for player " + player.getName());
            return;
        }

        var crystalPower = plugin.getCrystalPowerManager().getCrystalPower(crystalPowerId);
        if (crystalPower == null) {
            plugin.getLogger().info("DEBUG: Crystal Power not found for ID " + crystalPowerId + " for player " + player.getName());
            return;
        }

        plugin.getLogger().info("DEBUG: Applying crystal power effects for " + player.getName() + " with power " + crystalPower.getName());

        // Apply permanent effects
        for (var effect : crystalPower.getPermanentEffects()) {
            player.addPotionEffect(effect);
            plugin.getLogger().info("DEBUG: Applied potion effect " + effect.getType().getName() + " to " + player.getName());
        }        // Set max health
        var properties = crystalPower.getProperties();
        if (properties.getMaxHealth() != 20) {
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(properties.getMaxHealth());
            player.setHealth(Math.min(player.getHealth(), properties.getMaxHealth()));
            plugin.getLogger().info("DEBUG: Set max health to " + properties.getMaxHealth() + " for " + player.getName());
        }

        // Apply flight if available
        if (properties.canFly()) {
            player.setAllowFlight(true);
            plugin.getLogger().info("DEBUG: Flight enabled for " + player.getName() + " (crystal power: " + crystalPower.getName() + ")");
        } else {
            player.setAllowFlight(false);
            player.setFlying(false);
            plugin.getLogger().info("DEBUG: Flight disabled for " + player.getName() + " (crystal power: " + crystalPower.getName() + ")");
        }
    }
}
