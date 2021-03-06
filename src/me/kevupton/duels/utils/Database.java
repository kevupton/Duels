/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package me.kevupton.duels.utils;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import me.kevupton.duels.Duels;
import me.kevupton.duels.exceptions.DatabaseException;
import org.bukkit.Location;


public class Database {
    //static variables
    private Connection  conn = null;
    public  boolean     CONNECTED = false;
    private Duels plugin;
    private final String NAME;
    private final String VERSION;
    
    public Database(Duels pn) {
        plugin = pn;
        NAME = plugin.getDescription().getName().toLowerCase();
        VERSION = plugin.getDescription().getVersion();
    }
    
    public void closeConnection() {
        if (CONNECTED) {
            try {
                conn.close();
                CONNECTED = false;
                plugin.log("Shutting down Database");
            } catch (SQLException ex) {
                plugin.log(ex.toString());
            }
        }
    }
    
    public void setupConnection() {
        if (!CONNECTED) {
            boolean setup = false;
            
            String dbName = "plugins/Duels/duels.db";
            File file = new File (dbName);
                       
            try {
                //database does not exist then create one, and if that succeeds then do this
                if (file.createNewFile()) {
                    plugin.log("Database Created.");
                    setup = true;
                }
            } catch (IOException ex) {
                plugin.log(ex.toString());
            }

            //if database exists the continue
            if (file.exists()) {
                try {
                    Class.forName("org.sqlite.JDBC");
                    conn = DriverManager.getConnection("jdbc:sqlite:" + dbName);
                    CONNECTED = true;

                    if (setup == true) {
                        createTables();
                    } else {
                        updateScripts();
                    }
                    plugin.log("Connected to DB");
                } catch(Exception e) {
                    plugin.log(e.toString());
                }
            } else {
                plugin.log("Database not found!");
            }
        }
    }
    
    public void updateScripts() {
        String version = getVersion();
        String sql;
        ResultSet rs;
        
        plugin.log("Database up to date.");
    }
    
    public String getVersion() {
        String sql = "SELECT * FROM " + NAME;
        ResultSet rs = getResults(sql);
        try {
            return rs.getString("version");
        } catch(Exception e) {
            return "0";
        }
    }
    
    public void updateVersion(String version) {
        String sql = "UPDATE " + NAME + " SET version = '" + version + "'";
        query(sql);
    }
    
    public void createTables() {
        String sql = "CREATE TABLE IF NOT EXISTS \"main\".\"arenas\" (" + 
                "\"name\" VARCHAR(20) PRIMARY KEY, " + 
                "\"spawn1_x\" DOUBLE NOT NULL  DEFAULT 0, " +
                "\"spawn1_y\" DOUBLE NOT NULL  DEFAULT 0, " +
                "\"spawn1_z\" DOUBLE NOT NULL  DEFAULT 0, " +
                "\"spawn1_yaw\" FLOAT NOT NULL, " +
                "\"spawn1_pitch\" FLOAT NOT NULL, " + 
                "\"spawn1_world\" VARCHAR(100), " +
                "\"spawn2_x\" DOUBLE NOT NULL  DEFAULT 0, " +
                "\"spawn2_y\" DOUBLE NOT NULL  DEFAULT 0, " +
                "\"spawn2_z\" DOUBLE NOT NULL  DEFAULT 0, " +
                "\"spawn2_yaw\" FLOAT NOT NULL, " +
                "\"spawn2_pitch\" FLOAT NOT NULL, " + 
                "\"spawn2_world\" VARCHAR(100))";

        query(sql);

        sql = "CREATE TABLE IF NOT EXISTS \"main\".\"duels\" (" +
                "\"version\" VARCHAR (10) NOT NULL PRIMARY KEY " +
                ")";

        query(sql);
        
        sql = "INSERT INTO " + NAME + " VALUES('" + VERSION + "')";
        query(sql);
    }
    
    
    public ResultSet getAllArenas() {
        if (!CONNECTED) return null;
        
        String sql = "SELECT * FROM arenas";

        return getResults(sql);
    }
    
    public void registerArena(String name, Location spawn1, Location spawn2) throws DatabaseException {
        if (!CONNECTED) return;
        if (arenaNameExists(name)) throw new DatabaseException("Name Exists");
        
        String world1 = spawn1.getWorld().getName();
        double x1 = spawn1.getX();
        double y1 = spawn1.getY();
        double z1 = spawn1.getZ();
        float yaw1 = spawn1.getYaw();
        float pitch1 = spawn1.getPitch();
        
        String world2 = spawn2.getWorld().getName();
        double x2 = spawn2.getX();
        double y2 = spawn2.getY();
        double z2 = spawn2.getZ();
        float yaw2 = spawn2.getYaw();
        float pitch2 = spawn2.getPitch();
        
        String sql = "insert into arenas (name, spawn1_x, spawn1_y, spawn1_z, spawn1_yaw, spawn1_pitch, spawn1_world, "
                + "spawn2_x, spawn2_y, spawn2_z, spawn2_yaw, spawn2_pitch, spawn2_world)" +
                " values ('" + name + "'," + x1 + ", " + y1 + "," + z1 + "," + yaw1 + "," + pitch1 + ", '" + world1 + "'," 
                + x2 + ", " + y2 + "," + z2 + "," + yaw2 + "," + pitch2 + ", '" + world2 + "')";
        query(sql);
    }
        
    public void unregisterArena(String name) {
        if (!CONNECTED) return;
        String sql = "DELETE FROM arenas WHERE name = '" + name + "'";
        query(sql);
    }
    
    public boolean arenaNameExists(String name) {
        String sql = "SELECT * FROM arenas WHERE name = '" + name + "'";
        ResultSet results = getResults(sql);
        try {
            return results.next();
        } catch (SQLException ex) {
            plugin.log(ex.toString());
            return false;
        }
    }
    
    private void query(String sql) {
        try {
            Statement statement = conn.createStatement();
            statement.execute(sql);
        } catch(SQLException e) {
            plugin.log(e.toString());
        } catch(Exception e) {
            plugin.log(e.toString());
        }
    }
    
    public ResultSet getResults(String sql) {
        ResultSet rs = null;
        try {
            Statement statement = conn.createStatement();
            rs = statement.executeQuery(sql);
        } catch(SQLException e) {
            plugin.log(e.toString());
        } catch(Exception e) {
            plugin.log(e.toString());
        }
        
        return rs;
    }

    public void removeArena(String name) {
        String sql = "DELETE FROM arenas WHERE name = '" + name + "'";
        query(sql);
    }

    public void updateArena(Object[] data) {
        Duels.logInfo(data.toString());
        if (data[1] == null && data[2] == null) return;
        ArrayList<String> set = new ArrayList<String>();
        if (data[1] != null) {
            Location l = (Location) data[1];
            set.add("spawn1_x = " + l.getX());
            set.add("spawn1_y = " + l.getY());
            set.add("spawn1_z = " + l.getZ());
            set.add("spawn1_yaw = " + l.getYaw());
            set.add("spawn1_pitch = " + l.getPitch());
            set.add("spawn1_world = '" + l.getWorld().getName() + "'");
        }
        if (data[2] != null) {
            Location l = (Location) data[2];
            set.add("spawn2_x = " + l.getX());
            set.add("spawn2_y = " + l.getY());
            set.add("spawn2_z = " + l.getZ());
            set.add("spawn2_yaw = " + l.getYaw());
            set.add("spawn2_pitch = " + l.getPitch());
            set.add("spawn2_world = '" + l.getWorld().getName() + "'");
        }
        
        String sql = "UPDATE arenas SET " + String.join(", ", set) + " WHERE name = '" + (String) data[0] + "'";
        query(sql);
    }
}

