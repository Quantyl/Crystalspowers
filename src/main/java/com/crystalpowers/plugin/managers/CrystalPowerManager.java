package com.crystalpowers.plugin.managers;

import com.crystalpowers.plugin.CrystalPowersPlugin;
import com.crystalpowers.plugin.models.CrystalPower;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CrystalPowerManager {
    private final CrystalPowersPlugin plugin;
    private final Map<String, CrystalPower> crystalPowers;
    private final Random random;
    
    public CrystalPowerManager(CrystalPowersPlugin plugin) {
        this.plugin = plugin;
        this.crystalPowers = new HashMap<>();
        this.random = new Random();
        loadDefaultCrystalPowers();
    }
      
    private void loadDefaultCrystalPowers() {
        // Human - Balanced crystal power
        CrystalPower human = new CrystalPower("human", "Human", "A balanced crystal power with no special abilities but also no weaknesses.", Material.PLAYER_HEAD)
                .addPositive("§aBalanced stats")
                .addPositive("§aNo weaknesses")
                .addAbility("§7Standard human capabilities");
        crystalPowers.put("human", human);
        
        // Avian - Flying crystal power
        CrystalPower avian = new CrystalPower("avian", "Avian", "Bird-like beings who can fly but are fragile.", Material.FEATHER)
                .addPositive("§aCan fly")
                .addPositive("§aNo fall damage")
                .addPositive("§aSlow falling")
                .addNegative("§cLess health")
                .addNegative("§cWeak to projectiles")
                .addAbility("§6Flight: Hold space to fly")
                .addAbility("§6Slow Falling: Never take fall damage");
        avian.getProperties()
                .setCanFly(true)
                .setMaxHealth(16)
                .addWeakTo(Material.ARROW);
        crystalPowers.put("avian", avian);
          
        // Enderian - Teleporting crystal power
        CrystalPower enderian = new CrystalPower("enderian", "Enderian", "Mysterious beings from the End with teleportation abilities.", Material.ENDER_PEARL)
                .addPositive("§aTeleportation")
                .addPositive("§aNight vision")
                .addPositive("§aImmune to ender pearls")
                .addNegative("§cTakes damage from water")
                .addNegative("§cTakes damage from rain")
                .addAbility("§6Teleport: Right-click with ender pearl")
                .addAbility("§6Night Vision: See in the dark");        enderian.getProperties()
                .setCanTeleport(true)
                .addPotionEffect(PotionEffectType.NIGHT_VISION, 0)
                .setTakesDamageFromWater(true);
        enderian.addPermanentEffect(PotionEffectType.NIGHT_VISION, 0);
        crystalPowers.put("enderian", enderian);
        
        // Arachnid - Spider-like crystal power
        CrystalPower arachnid = new CrystalPower("arachnid", "Arachnid", "Spider-like beings who can climb walls and see in the dark.", Material.SPIDER_EYE)
                .addPositive("§aWall climbing")
                .addPositive("§aNight vision")
                .addPositive("§aPoison immunity")
                .addNegative("§cLess health")
                .addNegative("§cWeak to bane of arthropods")
                .addAbility("§6Wall Climb: Sneak against walls to climb")
                .addAbility("§6Night Vision: See in the dark");
        arachnid.getProperties()
                .setMaxHealth(16)
                .addPotionEffect(PotionEffectType.NIGHT_VISION, 0)
                .addWeakTo(Material.IRON_SWORD); // Represents Bane of Arthropods weakness
        arachnid.addPermanentEffect(PotionEffectType.NIGHT_VISION, 0);
        crystalPowers.put("arachnid", arachnid);
        
        // Merling - Water-based crystal power
        CrystalPower merling = new CrystalPower("merling", "Merling", "Aquatic beings who thrive in water but struggle on land.", Material.TROPICAL_FISH)
                .addPositive("§aWater breathing")
                .addPositive("§aFaster swimming")
                .addPositive("§aNight vision underwater")
                .addNegative("§cSlower on land")
                .addNegative("§cNeed water regularly")
                .addAbility("§6Water Breathing: Never drown")
                .addAbility("§6Aqua Affinity: Mine faster underwater");        merling.getProperties()
                .setCanBreatheUnderwater(true)
                .setSwimSpeed(1.5f)
                .setLandSpeed(0.8f)
                .addPotionEffect(PotionEffectType.WATER_BREATHING, 0)
                .addPotionEffect(PotionEffectType.DOLPHINS_GRACE, 1); 
        merling.addPermanentEffect(PotionEffectType.WATER_BREATHING, 0);
        crystalPowers.put("merling", merling);
        
        // Elytrian - Advanced flying crystal power
        CrystalPower elytrian = new CrystalPower("elytrian", "Elytrian", "Masters of the sky with natural elytra abilities.", Material.ELYTRA)
                .addPositive("§aBuilt-in elytra")
                .addPositive("§aLaunching ability")
                .addPositive("§aNo fall damage")
                .addNegative("§cCannot wear chestplate")
                .addNegative("§cWeaker in combat")
                .addAbility("§6Natural Elytra: Always equipped")
                .addAbility("§6Launch: Right-click to boost upward");
        elytrian.getProperties()
                .setCanFly(true)
                .setHasBuiltInElytra(true)
                .setCanWearChestplate(false)
                .setMaxHealth(18);
        crystalPowers.put("elytrian", elytrian);
        
        // Phantom - Phasing crystal power
        CrystalPower phantom = new CrystalPower("phantom", "Phantom", "Ghostly beings who can phase through walls but are vulnerable to light.", Material.PHANTOM_MEMBRANE)
                .addPositive("§aPhasing ability")
                .addPositive("§aInvisibility in darkness")
                .addPositive("§aNo fall damage")
                .addNegative("§cBurns in sunlight")
                .addNegative("§cWeaker during day")
                .addAbility("§6Phase: Sneak to phase through walls")
                .addAbility("§6Shadow Form: Invisible in darkness");        phantom.getProperties()
                .setCanPhase(true)
                .setBurnsInSunlight(true)
                .setInvisibleInDarkness(true)
                .setMaxHealth(14);
        crystalPowers.put("phantom", phantom);
        
        plugin.getLogger().info("Loaded " + crystalPowers.size() + " crystal powers");
    }
    
    public CrystalPower getCrystalPower(String id) {
        return crystalPowers.get(id.toLowerCase());
    }
    
    public Collection<CrystalPower> getAllCrystalPowers() {
        return crystalPowers.values();
    }
    
    public CrystalPower getRandomCrystalPower() {
        if (crystalPowers.isEmpty()) {
            return null;
        }
        
        List<CrystalPower> powerList = new ArrayList<>(crystalPowers.values());
        return powerList.get(random.nextInt(powerList.size()));
    }
    
    public boolean crystalPowerExists(String id) {
        return crystalPowers.containsKey(id.toLowerCase());
    }
    
    public void reloadCrystalPowers() {
        crystalPowers.clear();
        loadDefaultCrystalPowers();
        plugin.getLogger().info("Reloaded crystal powers");
    }
}
