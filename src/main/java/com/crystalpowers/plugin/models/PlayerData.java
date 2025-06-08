package com.crystalpowers.plugin.models;

import java.util.UUID;

public class PlayerData {
    private final UUID playerId;
    private String crystalPowerId;
    private boolean hasSelectedCrystalPower;
    private long lastCrystalPowerChange;
    
    public PlayerData(UUID playerId) {
        this.playerId = playerId;
        this.crystalPowerId = null;
        this.hasSelectedCrystalPower = false;
        this.lastCrystalPowerChange = 0;
    }
    
    public UUID getPlayerId() {
        return playerId;
    }
    
    public String getCrystalPowerId() {
        return crystalPowerId;
    }
    
    public void setCrystalPowerId(String crystalPowerId) {
        this.crystalPowerId = crystalPowerId;
        this.hasSelectedCrystalPower = true;
        this.lastCrystalPowerChange = System.currentTimeMillis();
    }
    
    public boolean hasSelectedCrystalPower() {
        return hasSelectedCrystalPower;
    }
    
    public long getLastCrystalPowerChange() {
        return lastCrystalPowerChange;
    }
    
    public boolean canChangeCrystalPower() {
        // Allow changing crystal power once every 24 hours (configurable)
        long cooldown = 24 * 60 * 60 * 1000; // 24 hours in milliseconds
        return System.currentTimeMillis() - lastCrystalPowerChange > cooldown;
    }
}
