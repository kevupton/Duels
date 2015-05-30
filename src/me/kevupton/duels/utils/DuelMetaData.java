/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.kevupton.duels.utils;

import me.kevupton.duels.Duels;
import me.kevupton.duels.utils.CommandManager.DuelCommand;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

/**
 *
 * @author Kevin
 */
public enum DuelMetaData {
    IN_ARENA        ("in_arena"), 
    IN_COUNTDOWN    ("in_countdown"),
    EDITING_ARENA   ("edit_area");
    
    private String meta_name;
    private static final String PREFIX = "duels_";
    
    DuelMetaData(String name) {
        meta_name = name;
    }
    
    @Override
    public String toString() {
        return PREFIX + meta_name;
    }
    
    public String val() {
        return toString();
    }
    
    public static void assignTo(Player player, DuelMetaData value) {
        player.setMetadata(value.val(), new FixedMetadataValue(Duels.getInstance(), true));
    }
    
    public boolean isOn(Player player) {
        return player.hasMetadata(meta_name);
    }
    
    public void removeFrom(Player player) {
        player.removeMetadata(meta_name, Duels.getInstance());
    }
    
    public static void remove(Player player, DuelMetaData value) {
        player.removeMetadata(value.val(), Duels.getInstance());
    }
    
    public static boolean playerHasMeta(Player player, DuelMetaData val) {
        return player.hasMetadata(val.val());
    }
    
    public static boolean isEdittingArena(Player player) {
        return playerHasMeta(player, DuelMetaData.EDITING_ARENA);
    }
    
    public static Object getMetaValue(Player player, DuelMetaData data) {
        return player.getMetadata(data.val()).get(0);
    }
    
    public static void setMetaValue(Player player, DuelMetaData data, Object val) {
        player.removeMetadata(data.val(), Duels.getInstance());
        player.setMetadata(data.val(), new FixedMetadataValue(Duels.getInstance(), val));
    }
    
    public static boolean isCompletedEdittingArena(Player player) {
        if (!isEdittingArena(player)) return false;
        Object[] data = (Object[]) getMetaValue(player, DuelMetaData.EDITING_ARENA);
        return (data[0] != null && data[1] != null && data[2] != null);
    }
    
    public static Object[] getEdittingArenaData(Player player) {
        Object[] data;
        if (isEdittingArena(player)) {
            data = (Object[]) getMetaValue(player, DuelMetaData.EDITING_ARENA);
        } else {
            data = new Object[4];
        }
        return data;
    }
    
    public static void updateArenaName(Player player, String name, DuelCommand command) {
        Object[] data = getEdittingArenaData(player);
        data[0] = name;
        data[3] = command;
        setMetaValue(player, DuelMetaData.EDITING_ARENA, data);
    }
    
    public static void updateArenaSpawn1(Player player, Location spawn1) {
        Object[] data = getEdittingArenaData(player);
        data[1] = spawn1;
        setMetaValue(player, DuelMetaData.EDITING_ARENA, data);
    }
    
    public static void updateArenaSpawn2(Player player, Location spawn2) {
        Object[] data = getEdittingArenaData(player);
        data[2] = spawn2;
        setMetaValue(player, DuelMetaData.EDITING_ARENA, data);
    }
    
    public static void removeEdittingArena(Player player) {
        remove(player, DuelMetaData.EDITING_ARENA);
    }
}
