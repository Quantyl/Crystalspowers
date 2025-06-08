package com.crystalpowers.plugin;

import com.crystalpowers.plugin.commands.EncryptionCommand;
import com.crystalpowers.plugin.commands.CrystalPowerCommand;
import com.crystalpowers.plugin.listeners.PlayerListener;
import com.crystalpowers.plugin.managers.CrystalPowerManager;
import com.crystalpowers.plugin.managers.PlayerDataManager;
import com.crystalpowers.plugin.models.PlayerData;
import com.crystalpowers.plugin.utils.EncryptionUtil;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class CrystalPowersPlugin extends JavaPlugin {
    
    private static CrystalPowersPlugin instance;
    private CrystalPowerManager crystalPowerManager;
    private PlayerDataManager playerDataManager;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // Save default config
        saveDefaultConfig();
        
        // Initialize encryption if enabled in config
        initializeEncryption();
        
        // Initialize managers
        this.crystalPowerManager = new CrystalPowerManager(this);
        this.playerDataManager = new PlayerDataManager(this);
        
        // Register commands
        getCommand("crystalpower").setExecutor(new CrystalPowerCommand(this));
        getCommand("encryption").setExecutor(new EncryptionCommand(this));
          // Register listeners
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        // Note: CrystalPowerGUIListener is no longer needed as CrystalPowerBookGUI handles its own events
        // getServer().getPluginManager().registerEvents(new CrystalPowerGUIListener(this), this);
        
        // Start flight maintenance task for Avian players
        startFlightMaintenanceTask();
        
        getLogger().info("Crystal Powers plugin has been enabled!");
    }
    
    @Override
    public void onDisable() {
        if (playerDataManager != null) {
            playerDataManager.saveAllData();
        }
        
        getLogger().info("Crystal Powers plugin has been disabled!");
    }
    
    public static CrystalPowersPlugin getInstance() {
        return instance;
    }
    
    public CrystalPowerManager getCrystalPowerManager() {
        return crystalPowerManager;
    }
    
    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }
    
    public void reloadPlugin() {
        reloadConfig();
        initializeEncryption(); // Re-initialize encryption on reload
        crystalPowerManager.reloadCrystalPowers();
        getLogger().info("Crystal Powers plugin has been reloaded!");
    }
    
    /**
     * Initialize encryption based on configuration
     */
    private void initializeEncryption() {
        if (getConfig().getBoolean("encryption.enabled", false)) {
            String masterPassword = getConfig().getString("encryption.master-password");
            
            if (masterPassword == null || masterPassword.trim().isEmpty()) {
                // Generate a random master password if none is set
                masterPassword = EncryptionUtil.generateSecureRandom(32);
                getConfig().set("encryption.master-password", masterPassword);
                saveConfig();
                getLogger().warning("Generated new encryption master password. Keep this safe!");
                getLogger().warning("Master password: " + masterPassword);
            }
            
            try {
                EncryptionUtil.initialize(masterPassword);
                getLogger().info("🔒 Encryption enabled - Player data will be encrypted");
            } catch (Exception e) {
                getLogger().severe("Failed to initialize encryption: " + e.getMessage());
                getLogger().severe("Player data will NOT be encrypted!");
            }
        } else {
            getLogger().info("🔓 Encryption disabled - Player data will be stored in plain text");
        }
    }
    
    private void startFlightMaintenanceTask() {
        // Run every 5 seconds to check and maintain flight for Avian players
        getServer().getScheduler().runTaskTimer(this, () -> {
            for (Player player : getServer().getOnlinePlayers()) {
                PlayerData data = playerDataManager.getExistingPlayerData(player);
                if (data != null && data.hasSelectedCrystalPower() && "avian".equals(data.getCrystalPowerId())) {
                    // Check if Avian player should have flight but doesn't
                    if (!player.getAllowFlight() && 
                        (player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE)) {
                        player.setAllowFlight(true);
                        getLogger().info("[FLIGHT-MAINTENANCE] Re-enabled flight for Avian player: " + player.getName());
                    }
                }
            }
        }, 100L, 100L); // Start after 5 seconds, repeat every 5 seconds
    }
}
