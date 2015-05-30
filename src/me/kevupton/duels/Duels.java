package me.kevupton.duels;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * 
 * @author Kevupton
 */
public class Duels extends JavaPlugin {
    private static Duels instance;
    public static final int TICKS_PER_SECOND = 20;
    
    @Override
    public void onEnable() {
        instance = this;
    }
    
    public static Duels getInstance() {
        return instance;
    }
    
    public void log(String message) {
        getLogger().info(message);
    }
}
