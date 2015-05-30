/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.kevupton.duels.processmanager;

import java.util.ArrayList;
import java.util.Dictionary;
import me.kevupton.duels.Duels;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.libs.joptsimple.util.KeyValuePair;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

/**
 *
 * @author Kevin
 */
public class ProcessManager {
    private int task_id;
    private final int NB_TICKS = 1;
    
    private ArrayList<Block> to_remove = new ArrayList<Block>();
    
    private static class Processor implements Runnable {        
        @Override
        public void run() {
            
        }
    }
    
    public void initialise() {
        task_id = Duels.getInstance().getServer().getScheduler().scheduleSyncRepeatingTask(
                Duels.getInstance(), new Processor(), NB_TICKS, NB_TICKS
        );
    }
    
    public void close() {
        Duels.getInstance().getServer().getScheduler().isCurrentlyRunning(task_id);
        Duels.getInstance().getServer().getScheduler().cancelTask(task_id);
    }
}
