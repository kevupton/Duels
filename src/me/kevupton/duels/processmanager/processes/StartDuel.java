/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.kevupton.duels.processmanager.processes;

import me.kevupton.duels.Duels;
import me.kevupton.duels.exceptions.ArenaException;
import me.kevupton.duels.utils.Arena;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

/**
 *
 * @author Kevin
 */
public class StartDuel implements Runnable {
    private Arena arena;   
    private boolean started = false;
    public static final int LOAD_TIME = 3;
    
    public StartDuel(Arena a) {
        arena = a;
    }
    
    @Override
    public void run() {
        long start_time = System.currentTimeMillis();
        Player p1 = arena.getPlayer1();
        Player p2 = arena.getPlayer2();
        Vector loc_check_p1 = p1.getLocation().toVector();
        Vector loc_check_p2 = p2.getLocation().toVector();
        boolean entered_arena = false;
        boolean msg1 = false;
        boolean msg2 = false;
        boolean msg3 = false;
        
        long time = 0;
        while (true) {
            time = getCurrentTimeInSeconds(start_time);
            if (arena.hasWinner()) break;
            
            if (!entered_arena) {
                if (time >= LOAD_TIME) {
                    arena.teleportPlayers();
                    start_time = System.currentTimeMillis();
                    entered_arena = true;
                } else {
                    if (!p1.getLocation().toVector().equals(loc_check_p1)) {
                        arena.sendCancelMessage(p1);
                        arena.reset();
                        break;
                    } else if (!p2.getLocation().toVector().equals(loc_check_p2)) {
                        arena.sendCancelMessage(p2);
                        arena.reset();
                        break;
                    }
                }
            } else {
                if (time >= 5) { //countdown
                    arena.startDuel();
                    break;
                } else if (time >= 4 && !msg1) {
                    msg1 = true;
                    arena.sendCountdown(1);
                } else if (time >= 3 && !msg2) {
                    msg2 = true;
                    arena.sendCountdown(2);
                } else if (time >= 2 && !msg3) {
                    msg3 = true;
                    arena.sendCountdown(3);
                } 
            }
        }
    }
    
    private long getCurrentTimeInSeconds(long start_time) {
        long cur_time = System.currentTimeMillis();
        return (long) ((cur_time - start_time) / 1000);
    }
    
    public static void register(Player p1, Player p2) throws ArenaException {
        Arena arena = Arena.getRandomAvailable();
        arena.setUnavailable();
        
        //add to the arena
        arena.addPlayer1(p1);
        arena.addPlayer2(p2);
        
        BukkitTask task = Duels.getInstance().getServer().getScheduler().runTaskAsynchronously(
            Duels.getInstance(), 
            new StartDuel(arena)
        );
    }
}
