/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.kevupton.duels.utils;

import me.kevupton.duels.Duels;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

/**
 *
 * @author Kevin
 */
public enum DuelsMetaData {
    IN_ARENA        ("in_arena"), 
    IN_COUNTDOWN    ("in_countdown");
    
    private String meta_name;
    private static final String PREFIX = "duels_";
    
    DuelsMetaData(String name) {
        meta_name = name;
    }
    
    @Override
    public String toString() {
        return PREFIX + meta_name;
    }
    
    public String val() {
        return PREFIX + meta_name;
    }
    
    public static void assignTo(Player player, DuelsMetaData value) {
        player.setMetadata(value.val(), new FixedMetadataValue(Duels.getInstance(), true));
    }
    
    public boolean isOn(Player player) {
        return player.hasMetadata(meta_name);
    }
    
    public void removeFrom(Player player) {
        player.removeMetadata(meta_name, Duels.getInstance());
    }
    
    public static void remove(Player player, DuelsMetaData value) {
        player.removeMetadata(value.val(), Duels.getInstance());
    }
    
    public static boolean playerHasMeta(Player player, DuelsMetaData val) {
        return player.hasMetadata(val.val());
    }
}
