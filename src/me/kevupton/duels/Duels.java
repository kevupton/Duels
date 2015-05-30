package me.kevupton.duels;

import java.util.logging.Level;
import java.util.logging.Logger;
import me.kevupton.duels.utils.CommandManager;
import me.kevupton.duels.events.PlayerCommandEvent;
import me.kevupton.duels.events.PlayerDieEvent;
import me.kevupton.duels.exceptions.DuelCommandException;
import me.kevupton.duels.processmanager.processes.ActiveDuel;
import me.kevupton.duels.utils.Arena;
import me.kevupton.duels.utils.Database;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
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
        registerEvents();
        db.setupConnection();
        Arena.initialise();
    }
    
    @Override
    public void onDisable() {
        db.closeConnection();
        Arena.closeAllDuels();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        CommandManager command = new CommandManager(sender, cmd, commandLabel, args);
        try {
            return command.execute();
        } catch (DuelCommandException ex) {
            log(ex.toString());
        }
        return false;
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
    
    private void registerEvents() {
        this.getServer().getPluginManager().registerEvents(new PlayerCommandEvent(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerDieEvent(), this);
    }
}
