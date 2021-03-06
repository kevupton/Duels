/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.kevupton.duels.processes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import me.kevupton.duels.Duels;
import me.kevupton.duels.exceptions.ArenaException;
import me.kevupton.duels.exceptions.DuelRequestException;
import org.bukkit.entity.Player;

/**
 *
 * @author Kevin
 */
public class DuelRequest implements Runnable {
    private static volatile Map<String, ArrayList<Object[]>> pending_requests = new HashMap<String, ArrayList<Object[]>> ();
    private Player receiver;
    private Player sender;
    
    private static final String CONFIG_SETTINGS = "Settings.RequestLife"; //seconds
    private static Integer CONF_VAL = null;
    
    private DuelRequest(Player r, Player s) {
        receiver = r;
        sender = s;
    }
    
    @Override
    public void run() {
        if (requestExists(receiver, sender)) {
            pending_requests.remove(receiver.getName());
        }
    }
    
    public static boolean requestExists(Player receiver, Player sender) {
        return (getTaskId(receiver, sender) != null);
    }
    
    public static int getConfigVal() {
        if (CONF_VAL == null) {
            CONF_VAL = Duels.getInstance().getConfig().getInt(CONFIG_SETTINGS);
            return CONF_VAL;
        } else {
            return CONF_VAL;
        }
    }
    
    private static long getRequestLength() {
        return Duels.TICKS_PER_SECOND * getConfigVal();
    }
    
    public static void register(Player receiver, Player sender) throws DuelRequestException {
        if (!requestExists(receiver, sender)) {
            int task_id = Duels.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(
                Duels.getInstance(),
                new DuelRequest(receiver, sender),
                getRequestLength()
            );
            appendRequest(receiver, sender, task_id);
        } else {
            throw new DuelRequestException("A duel request for this player already exists.");
        }
    }
    
    private static void appendRequest(Player receiver, Player sender, Integer task_id) {
        Object[] data = new Object[] {
            sender,
            task_id
        };
        ArrayList<Object[]> list;
        if (pending_requests.containsKey(receiver.getName())) {
            list = pending_requests.get(receiver.getName());
        } else {
            list = new ArrayList<Object[]>();
            pending_requests.put(receiver.getName(), list);
        }
        list.add(data);
        
    }
    
    public static void acceptRequest(Player receiver, Player sender) throws DuelRequestException, ArenaException {
        if (requestExists(receiver, sender)) {
            closeRequest(receiver, sender);
            StartDuel.register(receiver, sender);
        } else {
            throw new DuelRequestException("No pending requests found.");
        }
    }
    
    private static Integer getTaskId(Player receiver, Player sender) {
        ArrayList<Object[]> list = pending_requests.get(receiver.getName());
        if (list != null) {
            for (Object[] data: list) {
                if (data[0].equals(sender)) {
                    return (Integer) data[1];
                }
            }
        }
        return null;
    }
    
    private static void removeRequest(Player receiver, Player sender) {
        try {
            ArrayList<Object[]> list = pending_requests.get(receiver.getName());
            Iterator<Object[]> iter = list.iterator();
            while (iter.hasNext()) {
                Object[] data = iter.next();
                if (data[0].equals(sender)) {
                    iter.remove();
                }
            }
        } catch(Exception e) {
            Duels.logInfo(e.toString());
            e.printStackTrace();
        }
        
    }
    
    private static void closeRequest(Player receiver, Player sender) throws DuelRequestException {
        if (requestExists(receiver, sender)) {
            Integer task_id = getTaskId(receiver, sender);
            Duels.getInstance().getServer().getScheduler().cancelTask(task_id);
            removeRequest(receiver, sender);
        } else {
            throw new DuelRequestException("No pending requests found.");
        }
    }
}
