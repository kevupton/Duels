/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.kevupton.duels.processes;

import me.kevupton.duels.Duels;
import me.kevupton.duels.utils.Arena;

/**
 *
 * @author Kevin
 */
public class ActiveDuel implements Runnable {
    private Arena arena;   
    
    private static final String CONFIG_SETTINGS = "Settings.DuelTime"; //seconds
    private static Integer CONF_VAL = null;
    
    public ActiveDuel(Arena a) {
        arena = a;
    }
    
    private static int getDuelLength() {
        return getConfigVal() * Duels.TICKS_PER_SECOND;
    }
    
    public static int getConfigVal() {
        if (CONF_VAL == null) {
            CONF_VAL = Duels.getInstance().getConfig().getInt(CONFIG_SETTINGS);
            return CONF_VAL;
        } else {
            return CONF_VAL;
        }
    }
    
    @Override
    public void run() {
        arena.runOutOfTime();
    }
    
    public static void register(Arena arena) {
        int task_id = Duels.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(
            Duels.getInstance(),
            new ActiveDuel(arena),
            getDuelLength()
        );
        arena.setActiveTaskId(task_id);
    }
    
    public static void closeDuel(int task_id) {
        Duels.getInstance().getServer().getScheduler().cancelTask(task_id);
    }
}
