/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.kevupton.duels.utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import me.kevupton.duels.Duels;
import me.kevupton.duels.exceptions.ArenaException;
import me.kevupton.duels.exceptions.DatabaseException;
import me.kevupton.duels.processmanager.processes.ActiveDuel;
import me.kevupton.duels.processmanager.processes.EndDuel;
import org.bukkit.Location;
import org.bukkit.entity.Player;

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
    private boolean duel_started = false;
    
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
    
    public static void remove(String name) throws ArenaException {
         Iterator<Arena> iter = arenas.iterator();
         while (iter.hasNext()) {
             Arena a = iter.next();
             if (a.getName().toLowerCase().equals(name.toLowerCase())) {
                 iter.remove();
                 Duels.theDatabase().removeArena(name.toLowerCase());
                 return;
             }
         }
         throw new ArenaException("Arena not found");
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setSpawn1(Location l) {
        this.spawn1 = l;
    }
    
    public void setSpawn2(Location l) {
        this.spawn2 = l;
    }
    
    public static void updateArena(Object[] data) {
        String old_name = (String) data[0];
        Arena a = getArena(old_name);
        if (a != null) {
            if (data[1] != null) a.setSpawn1((Location) data[1]);
            if (data[2] != null) a.setSpawn2((Location) data[2]);
            Duels.theDatabase().updateArena(data);
        }
    }
    
    public static void closeAllDuels() {
        ArrayList<Arena> unavailable = getUnavailableArenas();
        for (Arena a: unavailable) {
            a.resetPlayer(a.getPlayer1());
            a.resetPlayer(a.getPlayer2());
        }
    }
    
    public void resetPlayer(Player player) {
        if (player != null) {
            DuelMetaData.remove(player, DuelMetaData.IN_ARENA);
            DuelMetaData.remove(player, DuelMetaData.PREVENT_MOVING);
            DuelMetaData.remove(player, DuelMetaData.COMMAND_BAN);
        }
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
    
    public void reset() {
        player1 = null;
        player2 = null;
        is_available = true;
        winner = null;
        is_end_phase = false;
        duel_started = false;
    }
    
    public void setUnavailable() {
        is_available = false;
    }
    
    public static  void registerNew(String name, Location spawn1, Location spawn2) throws ArenaException, DatabaseException {
        if (!arenaNameExists(name)) {
            Arena a = new Arena(name, spawn1, spawn2);
            Duels.theDatabase().registerArena(name, spawn1, spawn2);
            arenas.add(a);
        } else {
            throw new ArenaException("Arena already exists");
        }
    }
    
    public static boolean arenaNameExists(String name) {
        return (getArena(name) != null);
    }
    
    public static Arena getArena(String name) {
        Iterator<Arena> iter = arenas.iterator();
        while (iter.hasNext()) {
            Arena a = iter.next();
            if (a.getName().toLowerCase().equals(name.toLowerCase())) {
                return a;
            }
        }
        return null;
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
        return ((player1 != null && player1.equals(p)) || (player2 != null && player2.equals(p)));
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
        DuelMessage.RUN_OUT_OF_TIME.sendTo(player2);
        DuelMessage.RUN_OUT_OF_TIME.sendTo(player1);
        
        player1.teleport(this.p1_prev_loc);
        DuelMetaData.remove(player1, DuelMetaData.IN_ARENA);
        
        
        player2.teleport(this.p2_prev_loc);
        DuelMetaData.remove(player2, DuelMetaData.IN_ARENA);
        
        
        DuelMetaData.remove(player1, DuelMetaData.COMMAND_BAN);
        DuelMetaData.remove(player2, DuelMetaData.COMMAND_BAN);
        reset();
    }
    
    public void startDuel() {
        if (player1 == null && player2 != null) {
            setWinner(player2);
        } else if (player2 == null && player1 != null) {
            setWinner(player1);
        } else if (player2 != null && player1 != null) {
            DuelMessage.DUEL_STARTED.sendTo(player1, ActiveDuel.getConfigVal());
            DuelMessage.DUEL_STARTED.sendTo(player2, ActiveDuel.getConfigVal());
            duel_started = true;
            ActiveDuel.register(this);
            DuelMetaData.remove(player1, DuelMetaData.PREVENT_MOVING);
            DuelMetaData.remove(player2, DuelMetaData.PREVENT_MOVING);
        }
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
        DuelMetaData.assignTo(player2, DuelMetaData.IN_ARENA);
        
        player1.teleport(spawn1);
        DuelMetaData.assignTo(player1, DuelMetaData.IN_ARENA);
        
        DuelMetaData.assignTo(player1, DuelMetaData.PREVENT_MOVING);
        DuelMetaData.assignTo(player2, DuelMetaData.PREVENT_MOVING);
        DuelMetaData.assignTo(player1, DuelMetaData.COMMAND_BAN);
        DuelMetaData.assignTo(player2, DuelMetaData.COMMAND_BAN);
    }

    public void setLoser(Player player) {
        if (this.winner != null) return;
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
        DuelMetaData.remove(player, DuelMetaData.COMMAND_BAN);
        DuelMetaData.remove(player, DuelMetaData.PREVENT_MOVING);
        is_end_phase = true;
        DuelMessage.DUEL_WON.sendTo(player, EndDuel.getConfigVal());
        ActiveDuel.closeDuel(task_id);
        EndDuel.register(this);
        winner = player;
    }

    public void returnWinner() {
        DuelMetaData.remove(winner, DuelMetaData.IN_ARENA);
        if (winner.equals(player1)) {
            winner.teleport(p1_prev_loc);
        } else  if (winner.equals(player2)) {
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

    public boolean hasStarted() {
        return duel_started;
    }

    public void sendCancelMessage(Player player) {
        DuelMessage.PLAYER_CANCELED_DUEL.sendTo(player1, player.getName());
        DuelMessage.PLAYER_CANCELED_DUEL.sendTo(player2, player.getName());
    }

    public void cancelTask() {
        if (duel_started) {
            Duels.getInstance().getServer().getScheduler().cancelTask(task_id);
        }
    }

    public boolean hasWinner() {
        return (winner != null);
    }
}
