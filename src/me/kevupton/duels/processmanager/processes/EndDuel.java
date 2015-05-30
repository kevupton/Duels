/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.kevupton.duels.processmanager.processes;

import me.kevupton.duels.Duels;
import me.kevupton.duels.exceptions.ArenaException;
import me.kevupton.duels.utils.Arena;
import org.bukkit.entity.Player;

/**
 *
 * @author Kevin
 */
public class EndDuel implements Runnable {
    private Arena arena;   
    public static final int END_TIME = 10; //Seconds
    
    public EndDuel(Arena a) {
        arena = a;
    }
    
    private static int getTimeLength() {
        return END_TIME * Duels.TICKS_PER_SECOND;
    }
    
    @Override
    public void run() {
        arena.returnWinner();
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
