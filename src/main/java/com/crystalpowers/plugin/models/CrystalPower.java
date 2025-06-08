package com.crystalpowers.plugin.models;

import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class CrystalPower {
    private final String id;
    private final String name;
    private final String description;
    private final Material icon;
    private final List<String> abilities;
    private final List<String> positives;
    private final List<String> negatives;
    private final List<PotionEffect> permanentEffects;
    private final CrystalPowerProperties properties;
    
    public CrystalPower(String id, String name, String description, Material icon) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.abilities = new ArrayList<>();
        this.positives = new ArrayList<>();
        this.negatives = new ArrayList<>();
        this.permanentEffects = new ArrayList<>();
        this.properties = new CrystalPowerProperties();
    }
    
    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Material getIcon() { return icon; }
    public List<String> getAbilities() { return abilities; }
    public List<String> getPositives() { return positives; }
    public List<String> getNegatives() { return negatives; }
    public List<PotionEffect> getPermanentEffects() { return permanentEffects; }
    public CrystalPowerProperties getProperties() { return properties; }
    
    // Builder methods
    public CrystalPower addAbility(String ability) {
        this.abilities.add(ability);
        return this;
    }
    
    public CrystalPower addPositive(String positive) {
        this.positives.add(positive);
        return this;
    }
    
    public CrystalPower addNegative(String negative) {
        this.negatives.add(negative);
        return this;
    }
    
    public CrystalPower addPermanentEffect(PotionEffectType type, int amplifier) {
        this.permanentEffects.add(new PotionEffect(type, Integer.MAX_VALUE, amplifier, false, false));
        return this;
    }
    
    public static class CrystalPowerProperties {
        private boolean canFly = false;
        private boolean canTeleport = false;
        private boolean canBreatheUnderwater = false;
        private boolean canPhase = false;
        private boolean hasBuiltInElytra = false;
        private boolean canWearChestplate = true;
        private boolean takesDamageFromWater = false;
        private boolean burnsInSunlight = false;
        private boolean invisibleInDarkness = false;
        private int maxHealth = 20;
        private float swimSpeed = 1.0f;
        private float landSpeed = 1.0f;
        private final List<Material> weakTo = new ArrayList<>();
        private final List<PotionEffect> potionEffects = new ArrayList<>();
          // Getters
        public boolean canFly() { return canFly; }
        public boolean canTeleport() { return canTeleport; }
        public boolean canBreatheUnderwater() { return canBreatheUnderwater; }
        public boolean canPhase() { return canPhase; }
        public boolean hasBuiltInElytra() { return hasBuiltInElytra; }
        public boolean canWearChestplate() { return canWearChestplate; }
        public boolean takesDamageFromWater() { return takesDamageFromWater; }
        public boolean takesWaterDamage() { return takesDamageFromWater; } // Alias for compatibility
        public boolean burnsInSunlight() { return burnsInSunlight; }
        public boolean takesSunDamage() { return burnsInSunlight; } // Alias for compatibility
        public boolean isInvisibleInDarkness() { return invisibleInDarkness; }
        public int getMaxHealth() { return maxHealth; }
        public float getSwimSpeed() { return swimSpeed; }
        public float getLandSpeed() { return landSpeed; }
        public float getDamageMultiplier() { return 1.0f; } // Default no damage modifier
        public float getSpeedMultiplier() { return landSpeed; } // Use land speed as speed multiplier
        public List<Material> getWeakTo() { return weakTo; }
        public List<PotionEffect> getPotionEffects() { return potionEffects; }
        
        // Setters (fluent interface)
        public CrystalPowerProperties setCanFly(boolean canFly) {
            this.canFly = canFly;
            return this;
        }
        
        public CrystalPowerProperties setCanTeleport(boolean canTeleport) {
            this.canTeleport = canTeleport;
            return this;
        }
        
        public CrystalPowerProperties setCanBreatheUnderwater(boolean canBreatheUnderwater) {
            this.canBreatheUnderwater = canBreatheUnderwater;
            return this;
        }
        
        public CrystalPowerProperties setCanPhase(boolean canPhase) {
            this.canPhase = canPhase;
            return this;
        }
        
        public CrystalPowerProperties setHasBuiltInElytra(boolean hasBuiltInElytra) {
            this.hasBuiltInElytra = hasBuiltInElytra;
            return this;
        }
        
        public CrystalPowerProperties setCanWearChestplate(boolean canWearChestplate) {
            this.canWearChestplate = canWearChestplate;
            return this;
        }
        
        public CrystalPowerProperties setTakesDamageFromWater(boolean takesDamageFromWater) {
            this.takesDamageFromWater = takesDamageFromWater;
            return this;
        }
        
        public CrystalPowerProperties setBurnsInSunlight(boolean burnsInSunlight) {
            this.burnsInSunlight = burnsInSunlight;
            return this;
        }
        
        public CrystalPowerProperties setInvisibleInDarkness(boolean invisibleInDarkness) {
            this.invisibleInDarkness = invisibleInDarkness;
            return this;
        }
        
        public CrystalPowerProperties setMaxHealth(int maxHealth) {
            this.maxHealth = maxHealth;
            return this;
        }
        
        public CrystalPowerProperties setSwimSpeed(float swimSpeed) {
            this.swimSpeed = swimSpeed;
            return this;
        }
        
        public CrystalPowerProperties setLandSpeed(float landSpeed) {
            this.landSpeed = landSpeed;
            return this;
        }
        
        public CrystalPowerProperties addWeakTo(Material material) {
            this.weakTo.add(material);
            return this;
        }
        
        public CrystalPowerProperties addPotionEffect(PotionEffectType type, int amplifier) {
            this.potionEffects.add(new PotionEffect(type, Integer.MAX_VALUE, amplifier, false, false));
            return this;
        }
    }
}
