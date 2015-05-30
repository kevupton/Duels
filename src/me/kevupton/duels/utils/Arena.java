/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.kevupton.duels.utils;

import java.util.ArrayList;
import me.kevupton.duels.Duels;
import me.kevupton.duels.exceptions.ArenaException;
import me.kevupton.duels.processmanager.processes.ActiveDuel;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

/**
 *
 * @author Kevin
 */
public class Arena {
    private static ArrayList<Arena> arenas = new ArrayList<Arena>();
    private int task_id;
    private Player player1;
    private Player player2;
    private boolean is_available = true;
    private String name;
    private Location spawn1;
    private Location spawn2;
    private Duels duels;
    
    private Location p1_prev_loc;
    private Location p2_prev_loc;
    
    private final String META_NAME = "duels_in_arena";
    
    private Arena(String name, Location spawn1, Location spawn2) throws ArenaException {
        if (name == "") {
            throw new ArenaException("Invalid name");
        }
        this.name = name;
        if (spawn1 == null || spawn2 == null) {
            throw new ArenaException("Invalid Arena Location - Cannot be null");
        }
        this.spawn1 = spawn1;
        this.spawn2 = spawn2;
        this.duels = Duels.getInstance();
    }
    
    public String getName() {
        return name;
    }
    
    public Player getPlayer1() {
        return player1;
    }
    
    public Player getPlayer2() {
        return player2;
    }
    
    public Location getSpawn1() {
        return spawn1;
    }
    
    public Location getSpawn2() {
        return spawn2;
    }
    
    private void clear() {
        player1 = null;
        player2 = null;
        is_available = true;
    }
    
    public void setUnavailable() {
        is_available = false;
    }
    
    public static  void registerNew(String name, Location spawn1, Location spawn2) throws ArenaException {
        arenas.add(new Arena(name, spawn1, spawn2));
    }
    
    public static void initialise() {
        /*
        Get all the arenas and add them to the arenas array
        */
    }
    
    public static Arena getPlayerArena(Player player) throws ArenaException {
        for (Arena a: arenas) {
            if (a.containsPlayer(player)) {
                return a;
            }
        }
        throw new ArenaException("No player found in arena");
    }
    
    public boolean containsPlayer(Player p) {
        return (player1.equals(p) || player2.equals(p));
    }
    
    public void addPlayer1(Player p) {
        this.player1 = p;
        this.p1_prev_loc = p.getLocation();
    }
    
    public void addPlayer2(Player p) {
        this.player2 = p;
        this.p2_prev_loc = p.getLocation();
    }
    
    public static Arena getRandomAvailable() throws ArenaException {
        ArrayList<Arena> available = new ArrayList<Arena>();
        for (Arena arena: arenas) {
            if (arena.isAvailable()) {
                available.add(arena);
            }
        }
        if (available.size() >  0) {
            int key = (int) Math.round(Math.random() * (available.size() - 1));
            return available.get(key);
        }
        throw new ArenaException("No available arenas");
    }

    public void setActiveTaskId(int task_id) {
        this.task_id = task_id;
    }

    public void runOutOfTime() {
        player1.teleport(this.p1_prev_loc);
        player2.teleport(this.p2_prev_loc);
        
        is_available = true;
    }

    public void startDuel() {
        ActiveDuel.register(this);
    }

    private boolean isAvailable() {
        return is_available;
    }

    public void sendCountdown(int i) {
        String format = duels.getConfig().getString("Title.Countdown");
        format = format.replace("%no%", i + "");
        player1.sendMessage(format);
        player2.sendMessage(format);
    }

    public void teleportPlayers() {
        player2.teleport(spawn2);
        player2.setMetadata(META_NAME, new FixedMetadataValue(duels, true));
        
        player1.teleport(spawn1);
        player1.setMetadata(META_NAME, new FixedMetadataValue(duels, true));
    }

    public void sendPleaseWaitMessage() {
        
    }

    public void sendCancelMessage(Player cause) {
        
    }
}
