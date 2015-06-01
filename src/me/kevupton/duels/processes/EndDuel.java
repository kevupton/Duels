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
public class EndDuel implements Runnable {
    private Arena arena;   
    private static final String CONFIG_SETTINGS = "Settings.LootTime"; //Seconds
    private static Integer CONF_VAL = null;
    
    public EndDuel(Arena a) {
        arena = a;
    }
    
    private static int getTimeLength() {
        return getConfigVal() * Duels.TICKS_PER_SECOND;
    }
    
    @Override
    public void run() {
        arena.returnWinner();
    }
    
    public static int getConfigVal() {
        if (CONF_VAL == null) {
            CONF_VAL = Duels.getInstance().getConfig().getInt(CONFIG_SETTINGS);
            return CONF_VAL;
        } else {
            return CONF_VAL;
        }
    }
    
    public static void register(Arena arena) {
        int task_id = Duels.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(
            Duels.getInstance(),
            new EndDuel(arena),
            getTimeLength()
        );
        arena.setActiveTaskId(task_id);
    }
    
    public static void closeEnd(int task_id) {
        Duels.getInstance().getServer().getScheduler().cancelTask(task_id);
    }
}
