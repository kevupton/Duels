/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.kevupton.duels.processmanager.processes;

import me.kevupton.duels.Duels;
import me.kevupton.duels.utils.Arena;

/**
 *
 * @author Kevin
 */
public class ActiveDuel implements Runnable {
    private Arena arena;   
    public static final int DUEL_LENGTH = 120; //Seconds
    
    public ActiveDuel(Arena a) {
        arena = a;
    }
    
    private static int getDuelLength() {
        return DUEL_LENGTH * Duels.TICKS_PER_SECOND;
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
