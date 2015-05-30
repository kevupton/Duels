/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.kevupton.duels.utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import me.kevupton.duels.Duels;
import me.kevupton.duels.exceptions.ArenaException;
import me.kevupton.duels.exceptions.DatabaseException;
import me.kevupton.duels.processmanager.processes.ActiveDuel;
import me.kevupton.duels.processmanager.processes.EndDuel;
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
    private Player winner;
    private boolean is_end_phase = false;
    
    private Location p1_prev_loc;
    private Location p2_prev_loc;
    
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
    
    public static void closeAllDuels() {
        ArrayList<Arena> unavailable = getUnavailableArenas();
        Duels.logInfo("Not yet supported");
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
    
    private void reset() {
        player1 = null;
        player2 = null;
        is_available = true;
        winner = null;
        is_end_phase = false;
    }
    
    public void setUnavailable() {
        is_available = false;
    }
    
    public static  void registerNew(String name, Location spawn1, Location spawn2) throws ArenaException, DatabaseException {
        Arena a = new Arena(name, spawn1, spawn2);
        Duels.theDatabase().registerArena(name, spawn1, spawn2);
        arenas.add(a);
    }
    
    public static void initialise() {
        ResultSet rs = Duels.theDatabase().getAllArenas();
        if (rs == null) return;
        try {
            while (rs.next()) {
                Arena a = parseResultSet(rs);
                if (a != null) {
                    arenas.add(a);
                }
            }
        } catch (SQLException ex) {
            Duels.getInstance().log(ex.toString());
        }
    }
    
    private static Arena parseResultSet(ResultSet rs) {
        try {
            Location spawn1 = new Location(
                    Duels.getInstance().getServer().getWorld(rs.getString("spawn1_world")), 
                    rs.getInt("spawn1_x"),
                    rs.getInt("spawn1_y"),
                    rs.getInt("spawn1_z")
            );
            Location spawn2 = new Location(
                    Duels.getInstance().getServer().getWorld(rs.getString("spawn2_world")), 
                    rs.getInt("spawn2_x"),
                    rs.getInt("spawn2_y"),
                    rs.getInt("spawn2_z")
            );
            String name = rs.getString("name");
            return new Arena(name, spawn1, spawn2);
        } catch (SQLException ex) {
            Duels.logInfo(ex.toString());
        } catch (ArenaException ex) {
            Duels.logInfo("Invalid data supplied from database.");
        }
        return null;
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
    
    public static ArrayList<Arena> getAvailableArenas() {
        ArrayList<Arena> available = new ArrayList<Arena>();
        for (Arena arena: arenas) {
            if (arena.isAvailable()) {
                available.add(arena);
            }
        }
        return available;
    }
    
    public static ArrayList<Arena> getUnavailableArenas() {
        ArrayList<Arena> unavailable = new ArrayList<Arena>();
        for (Arena arena: arenas) {
            if (!arena.isAvailable()) {
                unavailable.add(arena);
            }
        }
        return unavailable;
    }
    
    public static Arena getRandomAvailable() throws ArenaException {
        ArrayList<Arena> available = getAvailableArenas();
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
        reset();
    }

    public void startDuel() {
        DuelMessage.DUEL_STARTED.sendTo(player1);
        DuelMessage.DUEL_STARTED.sendTo(player2);
        ActiveDuel.register(this);
    }

    private boolean isAvailable() {
        return is_available;
    }

    public void sendCountdown(int i) {
        String format = duels.getConfig().getString("Title.Countdown");
        DuelMessage.SEND_COUNTDOWN.sendTo(player1, i + "");
        DuelMessage.SEND_COUNTDOWN.sendTo(player2, i + "");
    }

    public void teleportPlayers() {
        player2.teleport(spawn2);
        player2.setMetadata(DuelMetaData.IN_ARENA.val(), new FixedMetadataValue(duels, true));
        
        player1.teleport(spawn1);
        player1.setMetadata(DuelMetaData.IN_ARENA.val(), new FixedMetadataValue(duels, true));
    }

    public void sendPleaseWaitMessage() {
        
    }

    public void sendCancelMessage(Player cause) {
        
    }

    public void setLoser(Player player) {
        if (this.containsPlayer(player)) {
            DuelMessage.DUEL_LOST.sendTo(player);
            if (player1.equals(player)) {
                setWinner(player2);
            } else if (player2.equals(player)) {
                setWinner(player1);
            }
        } else {
            Duels.logInfo("None of the players match the players associated to the arena");
        }
    }
    
    public void setWinner(Player player) {
        is_end_phase = true;
        DuelMessage.DUEL_WON.sendTo(player);
        ActiveDuel.closeDuel(task_id);
        EndDuel.register(this);
        winner = player;
    }

    public void returnWinner() {
        if (winner.equals(player1)) {
            winner.teleport(p1_prev_loc);
        } else {
            winner.teleport(p2_prev_loc);
        }
        reset();
    }
    
    public void endEarly() {
        if (is_end_phase) {
            EndDuel.closeEnd(task_id);
            returnWinner();
        }
    }
}
