package me.kevupton.duels;

import me.kevupton.duels.processmanager.processes.ActiveDuel;
import me.kevupton.duels.utils.Arena;
import me.kevupton.duels.utils.Database;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * 
 * @author Kevupton
 */
public class Duels extends JavaPlugin {
    private static Duels instance;
    public static final int TICKS_PER_SECOND = 20;
    private Database db = new Database(this);
    
    @Override
    public void onEnable() {
        instance = this;
        db.setupConnection();
        Arena.initialise();
    }
    
    @Override
    public void onDisable() {
        db.closeConnection();
        Arena.closeAllDuels();
    }
    
    public Database getLocalDatabase() {
        return db;
    }
    
    public static Database theDatabase() {
        return Duels.getInstance().getLocalDatabase();
    }
    
    public static Duels getInstance() {
        return instance;
    }
    
    public void log(String message) {
        getLogger().info(message);
    }
    
    public static void logInfo(String message) {
        Duels.getInstance().log(message);
    }
}
