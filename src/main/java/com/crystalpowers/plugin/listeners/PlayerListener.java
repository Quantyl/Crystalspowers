package com.crystalpowers.plugin.listeners;

import com.crystalpowers.plugin.CrystalPowersPlugin;
import com.crystalpowers.plugin.models.CrystalPower;
import com.crystalpowers.plugin.models.PlayerData;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.ChatColor;

public class PlayerListener implements Listener {
    private final CrystalPowersPlugin plugin;
    
    public PlayerListener(CrystalPowersPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Check if player has existing data without creating new entry
        boolean hasData = plugin.getPlayerDataManager().hasPlayerData(player);
        PlayerData data = plugin.getPlayerDataManager().getExistingPlayerData(player);
        boolean hasCrystalPower = hasData && data != null && data.hasSelectedCrystalPower();
        
        plugin.getLogger().info("Player " + player.getName() + " joined. Has data: " + hasData + ", Has crystal power: " + hasCrystalPower);
        
        // Apply crystal power effects on join only if they have one
        if (hasCrystalPower) {
            plugin.getPlayerDataManager().applyCrystalPowerToPlayer(player);
        }
        
        // Show crystal power selection if they haven't chosen one
        if (!hasCrystalPower) {
            plugin.getLogger().info("Opening crystal power book for " + player.getName());
            
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                try {
                    player.sendMessage(ChatColor.GOLD + "Welcome! You must choose your crystal power to begin your journey.");
                    player.sendMessage(ChatColor.YELLOW + "A mystical crystal tome has appeared in your hands...");
                      // Open the Crystal Power selection GUI
                    new com.crystalpowers.plugin.gui.CrystalPowerBookGUI(plugin, player).openCrystalPowerBook();
                    
                    plugin.getLogger().info("Successfully provided crystal power instructions for " + player.getName());
                    
                } catch (Exception e) {
                    plugin.getLogger().severe("Error providing crystal power instructions for " + player.getName() + ": " + e.getMessage());
                    e.printStackTrace();
                    
                    // Fallback: provide chat instructions
                    player.sendMessage(ChatColor.RED + "There was an issue setting up your Crystal Powers.");
                    player.sendMessage(ChatColor.YELLOW + "Please use one of these commands to select your crystal power:");
                    player.sendMessage(ChatColor.GREEN + "/crystalpower list " + ChatColor.GRAY + "- View all available powers");
                    player.sendMessage(ChatColor.GREEN + "/crystalpower select <n> " + ChatColor.GRAY + "- Select a specific power");
                    player.sendMessage(ChatColor.GREEN + "/crystalpower random " + ChatColor.GRAY + "- Get a random power");
                }
            }, 40L); // 2 seconds delay
        }
    }
    
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        
        Player player = (Player) event.getEntity();
        PlayerData data = plugin.getPlayerDataManager().getExistingPlayerData(player);
        
        if (data == null || !data.hasSelectedCrystalPower()) return;
        
        CrystalPower crystalPower = plugin.getCrystalPowerManager().getCrystalPower(data.getCrystalPowerId());
        if (crystalPower == null) return;
        
        var properties = crystalPower.getProperties();
        
        // Handle water damage for Enderian
        if (properties.takesWaterDamage() && 
            (event.getCause() == EntityDamageEvent.DamageCause.DROWNING || 
             player.getLocation().getBlock().getType() == Material.WATER)) {
            event.setDamage(event.getDamage() * 2); // Double water damage
        }
        
        // Handle sun damage for Phantom
        if (properties.takesSunDamage() && 
            event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK &&
            player.getWorld().getTime() > 0 && player.getWorld().getTime() < 12000 &&
            player.getLocation().getBlock().getLightFromSky() > 10) {
            // Burn in sunlight
            player.setFireTicks(60);
        }
          // Handle fall damage immunity for certain powers
        if ((crystalPower.getId().equals("avian") || crystalPower.getId().equals("phantom") || crystalPower.getId().equals("elytrian")) 
            && event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            event.setCancelled(true);
        }
        
        // Handle poison immunity for Arachnid
        if (crystalPower.getId().equals("arachnid") && 
            (event.getCause() == EntityDamageEvent.DamageCause.POISON ||
             event.getCause() == EntityDamageEvent.DamageCause.WITHER)) {
            event.setCancelled(true);
        }
        
        // Apply damage multiplier
        if (properties.getDamageMultiplier() != 1.0) {
            event.setDamage(event.getDamage() * properties.getDamageMultiplier());
        }
    }
    
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        
        Player player = (Player) event.getEntity();
        PlayerData data = plugin.getPlayerDataManager().getExistingPlayerData(player);
        
        if (data == null || !data.hasSelectedCrystalPower()) return;
        
        CrystalPower crystalPower = plugin.getCrystalPowerManager().getCrystalPower(data.getCrystalPowerId());
        if (crystalPower == null) return;
        
        // Handle weakness to specific materials/weapons
        var properties = crystalPower.getProperties();
        for (Material weakMaterial : properties.getWeakTo()) {
            if (event.getDamager() instanceof Player) {
                Player attacker = (Player) event.getDamager();
                ItemStack weapon = attacker.getInventory().getItemInMainHand();
                if (weapon.getType() == weakMaterial) {
                    event.setDamage(event.getDamage() * 1.5); // 50% more damage
                    break;
                }
            }
        }
    }
      @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        PlayerData data = plugin.getPlayerDataManager().getExistingPlayerData(player);
        
        if (data == null || !data.hasSelectedCrystalPower()) return;
        
        CrystalPower crystalPower = plugin.getCrystalPowerManager().getCrystalPower(data.getCrystalPowerId());
        if (crystalPower == null) return;
        
        var properties = crystalPower.getProperties();
        
        // Handle speed multiplier for land movement
        if (properties.getSpeedMultiplier() != 1.0 && !player.isFlying()) {
            float speed = (float) (0.2f * properties.getSpeedMultiplier());
            if (Math.abs(player.getWalkSpeed() - speed) > 0.01f) { // Only update if significantly different
                player.setWalkSpeed(speed);
            }
        }
        
        // Handle water breathing and swimming for Merling
        if (crystalPower.getId().equals("merling")) {
            if (player.isInWater()) {
                // Faster swimming and night vision underwater
                player.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 60, 1, false, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 60, 0, false, false));
            }
        }
        
        // Handle Arachnid wall climbing
        if (crystalPower.getId().equals("arachnid") && player.isSneaking()) {
            if (isAgainstWall(player)) {
                player.setVelocity(player.getVelocity().setY(0.2));
            }
        }
        
        // Handle Phantom invisibility in darkness
        if (crystalPower.getId().equals("phantom")) {
            if (player.getLocation().getBlock().getLightLevel() <= 4) {
                // Give invisibility in darkness
                if (!player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 100, 0, false, false));
                }
            } else {
                // Remove invisibility in light
                if (player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                    player.removePotionEffect(PotionEffectType.INVISIBILITY);
                }
            }
            
            // Phasing through blocks when sneaking
            if (player.isSneaking()) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 40, 0, false, false));
            }
        }
        
        // Handle Enderian water damage
        if (crystalPower.getId().equals("enderian")) {
            if (player.isInWater() || player.getWorld().hasStorm()) {
                // Deal water damage
                player.damage(1.0);
                player.sendMessage("§cYou take damage from water!");
            }
        }
        
        // Handle Phantom sun damage
        if (crystalPower.getId().equals("phantom")) {
            if (player.getWorld().getTime() > 0 && player.getWorld().getTime() < 12000 && 
                player.getLocation().getBlock().getLightFromSky() > 10) {
                // Burn in sunlight
                player.setFireTicks(60);
            }
        }
    }
    
    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        PlayerData data = plugin.getPlayerDataManager().getExistingPlayerData(player);
        
        if (data == null || !data.hasSelectedCrystalPower()) return;
        
        CrystalPower crystalPower = plugin.getCrystalPowerManager().getCrystalPower(data.getCrystalPowerId());
        if (crystalPower == null) return;
        
        // Handle Avian flight with slow falling
        if (crystalPower.getId().equals("avian")) {
            if (event.isFlying()) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, Integer.MAX_VALUE, 0, false, false));
            } else {
                player.removePotionEffect(PotionEffectType.SLOW_FALLING);
            }
        }
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        PlayerData data = plugin.getPlayerDataManager().getExistingPlayerData(player);
        
        if (data == null || !data.hasSelectedCrystalPower()) return;
        
        CrystalPower crystalPower = plugin.getCrystalPowerManager().getCrystalPower(data.getCrystalPowerId());
        if (crystalPower == null) return;
        
        // Handle Enderian teleportation
        if (crystalPower.getId().equals("enderian") && 
            event.getAction() == Action.RIGHT_CLICK_AIR && 
            player.getInventory().getItemInMainHand().getType() == Material.ENDER_PEARL) {
            
            event.setCancelled(true);
            
            // Custom teleportation logic
            var targetBlock = player.getTargetBlock(null, 50);
            if (targetBlock != null && targetBlock.getType() != Material.AIR) {
                var targetLocation = targetBlock.getLocation().add(0, 1, 0);
                player.teleport(targetLocation);
                player.sendMessage(ChatColor.DARK_PURPLE + "Teleported!");
                
                // Don't consume the ender pearl
                return;
            }
        }
          // Handle Phantom invisibility toggle
        if (crystalPower.getId().equals("phantom") &&
            event.getAction() == Action.RIGHT_CLICK_AIR &&
            player.isSneaking()) {
            
            if (player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                player.removePotionEffect(PotionEffectType.INVISIBILITY);
                player.sendMessage(ChatColor.GRAY + "Visibility restored");
            } else {
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 1200, 0, false, false)); // 1 minute
                player.sendMessage(ChatColor.GRAY + "Turned invisible");
            }
        }
        
        // Handle Elytrian launching
        if (crystalPower.getId().equals("elytrian") &&
            event.getAction() == Action.RIGHT_CLICK_AIR &&
            !player.isSneaking()) {
            
            // Launch player upward and forward
            var velocity = player.getLocation().getDirection().multiply(1.5);
            velocity.setY(velocity.getY() + 1.0);
            player.setVelocity(velocity);
            player.sendMessage(ChatColor.YELLOW + "Launched!");
        }
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        Player player = (Player) event.getWhoClicked();
        PlayerData data = plugin.getPlayerDataManager().getExistingPlayerData(player);
        
        if (data == null || !data.hasSelectedCrystalPower()) return;
        
        CrystalPower crystalPower = plugin.getCrystalPowerManager().getCrystalPower(data.getCrystalPowerId());
        if (crystalPower == null) return;
        
        // Handle Elytrian equipment restrictions
        if (crystalPower.getId().equals("elytrian")) {
            // Prevent wearing chestplate
            if (event.getSlot() == 38) { // Chestplate slot
                ItemStack item = event.getCursor();
                if (item != null && item.getType().name().contains("CHESTPLATE")) {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "Elytrians cannot wear chestplates due to their natural wings!");
                    return;
                }
            }
            
            // Auto-equip elytra if chestplate slot is empty
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                if (player.getInventory().getChestplate() == null || 
                    player.getInventory().getChestplate().getType() == Material.AIR) {
                    player.getInventory().setChestplate(new ItemStack(Material.ELYTRA));
                    player.sendMessage(ChatColor.YELLOW + "Your natural wings have manifested as elytra!");
                }
            }, 1L);
        }
    }
    
    private boolean isAgainstWall(Player player) {
        // Simple wall detection - check if there's a solid block adjacent to the player
        var location = player.getLocation();
        var world = location.getWorld();
        
        return world.getBlockAt(location.clone().add(1, 0, 0)).getType().isSolid() ||
               world.getBlockAt(location.clone().add(-1, 0, 0)).getType().isSolid() ||
               world.getBlockAt(location.clone().add(0, 0, 1)).getType().isSolid() ||
               world.getBlockAt(location.clone().add(0, 0, -1)).getType().isSolid();
    }
}
