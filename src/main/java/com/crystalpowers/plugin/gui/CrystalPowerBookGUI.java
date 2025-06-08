package com.crystalpowers.plugin.gui;

import com.crystalpowers.plugin.CrystalPowersPlugin;
import com.crystalpowers.plugin.models.CrystalPower;
import com.crystalpowers.plugin.models.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class CrystalPowerBookGUI implements Listener {
    private final CrystalPowersPlugin plugin;
    private final Player player;
    private Inventory currentInventory;
    
    public CrystalPowerBookGUI(CrystalPowersPlugin plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        
        // Register this as a listener
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void openCrystalPowerBook() {
        plugin.getLogger().info("Opening Crystal Powers chest GUI for player: " + player.getName());
        PlayerData data = plugin.getPlayerDataManager().getExistingPlayerData(player);
        
        // If player already has a crystal power, show info chest
        if (data != null && data.hasSelectedCrystalPower()) {
            plugin.getLogger().info("Player already has crystal power, opening info chest");
            openInfoChest(data);
            return;
        }
        
        // Create main crystal powers selection chest
        openMainCrystalPowerChest();
    }
            
    private void openMainCrystalPowerChest() {
        currentInventory = Bukkit.createInventory(null, 54, ChatColor.DARK_PURPLE + "✦ Choose Your Crystal Power ✦");
        
        List<CrystalPower> crystalPowers = new ArrayList<>(plugin.getCrystalPowerManager().getAllCrystalPowers());
        
        // Place crystal power items in the chest
        int slot = 10; // Start from slot 10 for better layout
        for (int i = 0; i < crystalPowers.size() && slot < 45; i++) {
            CrystalPower crystalPower = crystalPowers.get(i);
            
            // Skip middle column for better spacing
            if (slot % 9 == 4) slot++;
            if (slot >= 45) break;
            
            ItemStack crystalPowerItem = createCrystalPowerDisplayItem(crystalPower);
            currentInventory.setItem(slot, crystalPowerItem);
            
            slot++;
            // Skip to next row after 3 items per row
            if ((slot - 10) % 3 == 0) {
                slot += 6; // Move to next row, starting from column 1
            }
        }
        
        // Add special items
        addSpecialItems();
        
        player.openInventory(currentInventory);
    }
    
    private void openInfoChest(PlayerData data) {
        CrystalPower currentCrystalPower = plugin.getCrystalPowerManager().getCrystalPower(data.getCrystalPowerId());
        if (currentCrystalPower == null) return;
        
        currentInventory = Bukkit.createInventory(null, 54, ChatColor.GOLD + "✦ Your Crystal Power: " + currentCrystalPower.getName() + " ✦");
        
        // Center the crystal power info item
        ItemStack crystalPowerItem = createDetailedCrystalPowerItem(currentCrystalPower, true);
        currentInventory.setItem(22, crystalPowerItem);
        
        // Add decorative items
        addInfoDecorations();
        
        player.openInventory(currentInventory);
    }
    
    private ItemStack createCrystalPowerDisplayItem(CrystalPower crystalPower) {
        Material material = crystalPower.getIcon() != null ? crystalPower.getIcon() : Material.ENCHANTED_BOOK;
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        
        meta.setDisplayName(ChatColor.GOLD + ChatColor.BOLD.toString() + crystalPower.getName());
        
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + crystalPower.getDescription());
        lore.add("");
        
        // Show preview of abilities
        if (!crystalPower.getPositives().isEmpty()) {
            lore.add(ChatColor.GREEN + "✓ Key Abilities:");
            int count = 0;
            for (String positive : crystalPower.getPositives()) {
                if (count >= 2) {
                    lore.add(ChatColor.GREEN + "  + And " + (crystalPower.getPositives().size() - 2) + " more...");
                    break;
                }
                lore.add(ChatColor.GREEN + "  + " + positive);
                count++;
            }
            lore.add("");
        }
        
        // Show main drawback
        if (!crystalPower.getNegatives().isEmpty()) {
            lore.add(ChatColor.RED + "✗ Main Drawback:");
            lore.add(ChatColor.RED + "  - " + crystalPower.getNegatives().get(0));
            if (crystalPower.getNegatives().size() > 1) {
                lore.add(ChatColor.RED + "  - And " + (crystalPower.getNegatives().size() - 1) + " more...");
            }
            lore.add("");
        }
        
        lore.add(ChatColor.YELLOW + "► Click for detailed view ◄");
        lore.add(ChatColor.DARK_GRAY + "ID: " + crystalPower.getId());
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
    
    private ItemStack createDetailedCrystalPowerItem(CrystalPower crystalPower, boolean isOwned) {
        Material material = crystalPower.getIcon() != null ? crystalPower.getIcon() : Material.ENCHANTED_BOOK;
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        
        meta.setDisplayName(ChatColor.GOLD + ChatColor.BOLD.toString() + crystalPower.getName());
        
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + crystalPower.getDescription());
        lore.add("");
        
        // All abilities
        if (!crystalPower.getPositives().isEmpty()) {
            lore.add(ChatColor.GREEN + ChatColor.BOLD.toString() + "✓ Positive Abilities:");
            for (String positive : crystalPower.getPositives()) {
                lore.add(ChatColor.GREEN + "  + " + positive);
            }
            lore.add("");
        }
        
        // All negatives
        if (!crystalPower.getNegatives().isEmpty()) {
            lore.add(ChatColor.RED + ChatColor.BOLD.toString() + "✗ Negative Effects:");
            for (String negative : crystalPower.getNegatives()) {
                lore.add(ChatColor.RED + "  - " + negative);
            }
            lore.add("");
        }
        
        // Special abilities
        if (!crystalPower.getAbilities().isEmpty()) {
            lore.add(ChatColor.AQUA + ChatColor.BOLD.toString() + "⚡ Special Powers:");
            for (String ability : crystalPower.getAbilities()) {
                lore.add(ChatColor.AQUA + "  ⚡ " + ability);
            }
            lore.add("");
        }

        if (isOwned) {
            lore.add(ChatColor.GOLD + "► This is your chosen crystal power ◄");
        } else {
            lore.add(ChatColor.YELLOW + "► Click to select this crystal power ◄");
            lore.add(ChatColor.RED + "⚠ WARNING: This choice is PERMANENT! ⚠");
        }
        
        lore.add(ChatColor.DARK_GRAY + "ID: " + crystalPower.getId());
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
    
    private void addSpecialItems() {
        // Random crystal power button
        ItemStack randomItem = new ItemStack(Material.EMERALD);
        ItemMeta randomMeta = randomItem.getItemMeta();
        randomMeta.setDisplayName(ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() + "✨ Random Crystal Power ✨");
        List<String> randomLore = new ArrayList<>();
        randomLore.add(ChatColor.WHITE + "Let the crystals choose your destiny!");
        randomLore.add(ChatColor.GRAY + "This will randomly select one of");
        randomLore.add(ChatColor.GRAY + "the available crystal powers for you.");
        randomLore.add("");
        randomLore.add(ChatColor.GOLD + "► Click to get random crystal power ◄");
        randomMeta.setLore(randomLore);
        randomItem.setItemMeta(randomMeta);
        currentInventory.setItem(49, randomItem);
        
        // Info guide
        ItemStack infoItem = new ItemStack(Material.BOOK);
        ItemMeta infoMeta = infoItem.getItemMeta();
        infoMeta.setDisplayName(ChatColor.GOLD + ChatColor.BOLD.toString() + "ℹ Crystal Power Guide");
        List<String> infoLore = new ArrayList<>();
        infoLore.add(ChatColor.GRAY + "Welcome to the Crystal Powers selection!");
        infoLore.add("");
        infoLore.add(ChatColor.YELLOW + "• Click on any crystal power to view details");
        infoLore.add(ChatColor.YELLOW + "• Select carefully - choice is permanent!");
        infoLore.add(ChatColor.YELLOW + "• Each power has unique abilities & limits");
        infoLore.add("");
        infoLore.add(ChatColor.LIGHT_PURPLE + "Choose wisely, crystal bearer...");
        infoMeta.setLore(infoLore);
        infoItem.setItemMeta(infoMeta);
        currentInventory.setItem(53, infoItem);
        
        // Close button
        ItemStack closeItem = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = closeItem.getItemMeta();
        closeMeta.setDisplayName(ChatColor.RED + "✖ Close");
        List<String> closeLore = new ArrayList<>();
        closeLore.add(ChatColor.GRAY + "Close this menu");
        closeMeta.setLore(closeLore);
        closeItem.setItemMeta(closeMeta);
        currentInventory.setItem(45, closeItem);
    }
    
    private void addInfoDecorations() {
        // Close button
        ItemStack closeItem = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = closeItem.getItemMeta();
        closeMeta.setDisplayName(ChatColor.RED + "✖ Close");
        List<String> closeLore = new ArrayList<>();
        closeLore.add(ChatColor.GRAY + "Close this menu");
        closeMeta.setLore(closeLore);
        closeItem.setItemMeta(closeMeta);
        currentInventory.setItem(45, closeItem);
        
        // Decorative items
        ItemStack decorItem = new ItemStack(Material.PURPLE_STAINED_GLASS_PANE);
        ItemMeta decorMeta = decorItem.getItemMeta();
        decorMeta.setDisplayName(" ");
        decorItem.setItemMeta(decorMeta);
        
        // Add decorative border
        int[] decorSlots = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 44, 46, 47, 48, 50, 51, 52, 53};
        for (int slot : decorSlots) {
            currentInventory.setItem(slot, decorItem);
        }
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (currentInventory == null || !event.getInventory().equals(currentInventory)) return;
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        event.setCancelled(true);
        
        Player clickedPlayer = (Player) event.getWhoClicked();
        if (!clickedPlayer.equals(player)) return;
        
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;
        
        handleItemClick(clickedItem);
    }

    private void handleItemClick(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return;
        
        String displayName = meta.getDisplayName();
        
        // Handle special items
        if (displayName.contains("Random Crystal Power")) {
            handleRandomSelection();
            return;
        }
        
        if (displayName.contains("Close")) {
            player.closeInventory();
            return;
        }
        
        if (displayName.contains("Back")) {
            openMainCrystalPowerChest();
            return;
        }
        
        if (displayName.contains("SELECT") && item.getType() == Material.EMERALD) {
            // Extract crystal power ID from the confirm button context
            // Look for the crystal power info item in slot 13
            ItemStack crystalPowerInfoItem = currentInventory.getItem(13);
            if (crystalPowerInfoItem != null && crystalPowerInfoItem.hasItemMeta() && crystalPowerInfoItem.getItemMeta().hasLore()) {
                List<String> lore = crystalPowerInfoItem.getItemMeta().getLore();
                for (String line : lore) {
                    String strippedLine = ChatColor.stripColor(line);
                    if (strippedLine.startsWith("ID: ")) {
                        String crystalPowerId = strippedLine.substring(4);
                        selectCrystalPower(crystalPowerId);
                        return;
                    }
                }
            }
            return;
        }
        
        if (displayName.contains("Get Random Crystal Power Instead")) {
            handleRandomSelection();
            return;
        }
        
        // Handle crystal power selection
        if (meta.hasLore()) {
            List<String> lore = meta.getLore();
            String crystalPowerId = null;

            // Find crystal power ID from lore
            for (String line : lore) {
                String strippedLine = ChatColor.stripColor(line);
                if (strippedLine.startsWith("ID: ")) {
                    crystalPowerId = strippedLine.substring(4);
                    break;
                }
            }
            
            if (crystalPowerId != null) {
                openCrystalPowerDetailGUI(crystalPowerId);
            }
        }
    }
    
    private void handleRandomSelection() {
        List<CrystalPower> crystalPowers = new ArrayList<>(plugin.getCrystalPowerManager().getAllCrystalPowers());
        if (crystalPowers.isEmpty()) {
            player.sendMessage(ChatColor.RED + "No crystal powers available!");
            player.closeInventory();
            return;
        }
        
        // Check if player already has a crystal power
        PlayerData data = plugin.getPlayerDataManager().getExistingPlayerData(player);
        if (data != null && data.hasSelectedCrystalPower()) {
            player.sendMessage(ChatColor.RED + "You have already chosen your crystal power!");
            player.closeInventory();
            return;
        }
        
        // Select random crystal power
        java.util.Random random = new java.util.Random();
        CrystalPower randomCrystalPower = crystalPowers.get(random.nextInt(crystalPowers.size()));
        
        plugin.getPlayerDataManager().setPlayerCrystalPower(player, randomCrystalPower.getId());
        
        player.sendMessage(ChatColor.GOLD + "✨ The crystals have chosen for you! ✨");
        player.sendMessage(ChatColor.GREEN + "You have been randomly assigned the " + ChatColor.GOLD + 
                          randomCrystalPower.getName() + ChatColor.GREEN + " crystal power!");
        player.sendMessage(ChatColor.YELLOW + "This choice is permanent and cannot be changed!");
        player.closeInventory();
    }
      
    public void openCrystalPowerDetailGUI(String crystalPowerId) {
        CrystalPower crystalPower = plugin.getCrystalPowerManager().getCrystalPower(crystalPowerId);
        if (crystalPower == null) {
            player.sendMessage(ChatColor.RED + "Crystal power not found!");
            return;
        }
        
        // Check if player already has a crystal power
        PlayerData data = plugin.getPlayerDataManager().getExistingPlayerData(player);
        if (data != null && data.hasSelectedCrystalPower()) {
            player.sendMessage(ChatColor.RED + "You have already chosen your crystal power!");
            return;
        }
        
        // Create detailed selection inventory
        currentInventory = Bukkit.createInventory(null, 54, ChatColor.DARK_PURPLE + "✦ " + crystalPower.getName() + " Details ✦");
        
        // Crystal power info item (center top)
        ItemStack infoItem = createDetailedCrystalPowerItem(crystalPower, false);
        currentInventory.setItem(13, infoItem);
        
        // Confirm selection button (emerald)
        ItemStack confirmItem = new ItemStack(Material.EMERALD);
        ItemMeta confirmMeta = confirmItem.getItemMeta();
        confirmMeta.setDisplayName(ChatColor.GREEN + ChatColor.BOLD.toString() + "✓ SELECT " + crystalPower.getName().toUpperCase());
        List<String> confirmLore = new ArrayList<>();
        confirmLore.add(ChatColor.YELLOW + "Click to choose this crystal power!");
        confirmLore.add("");
        confirmLore.add(ChatColor.RED + ChatColor.BOLD.toString() + "⚠ WARNING: This is PERMANENT! ⚠");
        confirmLore.add(ChatColor.RED + "You cannot change your crystal power later!");
        confirmLore.add("");
        confirmLore.add(ChatColor.GOLD + "Think carefully before selecting!");
        confirmMeta.setLore(confirmLore);
        confirmItem.setItemMeta(confirmMeta);
        currentInventory.setItem(40, confirmItem);
        
        // Back button (arrow)
        ItemStack backItem = new ItemStack(Material.ARROW);
        ItemMeta backMeta = backItem.getItemMeta();
        backMeta.setDisplayName(ChatColor.GRAY + "← Back to Crystal Power Selection");
        List<String> backLore = new ArrayList<>();
        backLore.add(ChatColor.GRAY + "Return to the main crystal power menu");
        backMeta.setLore(backLore);
        backItem.setItemMeta(backMeta);
        currentInventory.setItem(36, backItem);
        
        // Random alternative button (diamond)
        ItemStack randomItem = new ItemStack(Material.DIAMOND);
        ItemMeta randomMeta = randomItem.getItemMeta();
        randomMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "✨ Get Random Crystal Power Instead");
        List<String> randomLore = new ArrayList<>();
        randomLore.add(ChatColor.GRAY + "Let the crystals choose your destiny!");
        randomLore.add(ChatColor.GRAY + "This will randomly assign you a crystal power.");
        randomMeta.setLore(randomLore);
        randomItem.setItemMeta(randomMeta);
        currentInventory.setItem(44, randomItem);
        
        // Add decorative border
        ItemStack borderItem = new ItemStack(Material.PURPLE_STAINED_GLASS_PANE);
        ItemMeta borderMeta = borderItem.getItemMeta();
        borderMeta.setDisplayName(" ");
        borderItem.setItemMeta(borderMeta);
        
        int[] borderSlots = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 45, 46, 47, 48, 50, 51, 52, 53};
        for (int slot : borderSlots) {
            currentInventory.setItem(slot, borderItem);
        }
        
        player.openInventory(currentInventory);
    }
    
    private void selectCrystalPower(String crystalPowerId) {
        CrystalPower crystalPower = plugin.getCrystalPowerManager().getCrystalPower(crystalPowerId);
        if (crystalPower == null) {
            player.sendMessage(ChatColor.RED + "Crystal power not found!");
            return;
        }
        
        plugin.getPlayerDataManager().setPlayerCrystalPower(player, crystalPowerId);
        player.sendMessage(ChatColor.GREEN + "✓ You have selected the " + ChatColor.GOLD + crystalPower.getName() + ChatColor.GREEN + " crystal power!");
        player.sendMessage(ChatColor.YELLOW + "This choice is permanent and cannot be changed!");
        player.closeInventory();
    }
}
