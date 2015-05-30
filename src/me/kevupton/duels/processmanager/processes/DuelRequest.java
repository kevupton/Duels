/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.kevupton.duels.processmanager.processes;

import java.util.HashMap;
import java.util.Map;
import me.kevupton.duels.Duels;
import me.kevupton.duels.exceptions.DuelRequestException;
import org.bukkit.entity.Player;

/**
 *
 * @author Kevin
 */
public class DuelRequest implements Runnable {
    private static Map<String, Object[]> pending_requests = new HashMap<String, Object[]> ();
    private Player receiver;
    private Player sender;
    
    private static final int REQUEST_LENGTH = 30; //seconds
    
    private DuelRequest(Player r, Player s) {
        receiver = r;
        sender = s;
    }
    
    @Override
    public void run() {
        if (requestExists(receiver)) {
            pending_requests.remove(receiver.getName());
        }
    }
    
    public static boolean requestExists(Player receiver) {
        return pending_requests.containsKey(receiver.getName());
    }
    
    private static long getRequestLength() {
        return Duels.TICKS_PER_SECOND * REQUEST_LENGTH;
    }
    
    public static void register(Player receiver, Player sender) throws Exception {
        if (!requestExists(receiver)) {
            int task_id = Duels.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(
                Duels.getInstance(),
                new DuelRequest(receiver, sender),
                getRequestLength()
            );
            
            pending_requests.put(receiver.getName(), new Object[] {
                sender,
                task_id
            });
        } else {
            throw new DuelRequestException("A duel request for this player already exists.");
        }
    }
    
    public static void acceptRequest(Player player) throws Exception {
        if (requestExists(player)) {
            closeRequest(player);
            StartDuel.register(player, getSender(player));
        } else {
            throw new DuelRequestException("No pending requests found.");
        }
    }
    
    private static Player getSender(Player receiver) throws Exception {
        if (requestExists(receiver)) {
            Object[] data = pending_requests.get(receiver.getName());
            return (Player) data[0];
        } else {
            throw new DuelRequestException("No pending requests found.");
        }
    }
    
    private static void closeRequest(Player player) throws Exception {
        if (requestExists(player)) {
            Object[] data = pending_requests.get(player.getName());
            Player sender = (Player) data[0];
            int task_id = (int) data[1];
            
            Duels.getInstance().getServer().getScheduler().cancelTask(task_id);
            pending_requests.remove(player.getName());
        } else {
            throw new DuelRequestException("No pending requests found.");
        }
    }
}
