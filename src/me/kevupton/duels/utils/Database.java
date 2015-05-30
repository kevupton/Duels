/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package me.kevupton.duels.utils;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
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
                "\"spawn1_x\" INTEGER NOT NULL  DEFAULT 0, " +
                "\"spawn1_y\" INTEGER NOT NULL  DEFAULT 0, " +
                "\"spawn1_z\" INTEGER NOT NULL  DEFAULT 0, " +
                "\"spawn1_world\" VARCHAR(100), " +
                "\"spawn2_x\" INTEGER NOT NULL  DEFAULT 0, " +
                "\"spawn2_y\" INTEGER NOT NULL  DEFAULT 0, " +
                "\"spawn2_z\" INTEGER NOT NULL  DEFAULT 0, " +
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
        int x1 = (int) spawn1.getX();
        int y1 = (int) spawn1.getY();
        int z1 = (int) spawn1.getZ();
        
        String world2 = spawn2.getWorld().getName();
        int x2 = (int) spawn2.getX();
        int y2 = (int) spawn2.getY();
        int z2 = (int) spawn2.getZ();
        
        String sql = "insert into arenas (name, spawn1_x, spawn1_y, spawn1_z, spawn1_world, "
                + "spawn2_x, spawn2_y, spawn2_z, spawn2_world)" +
                " values ('" + name + "'," + x1 + ", " + y1 + "," + z1 + ", '" + world1 + "'," 
                + x2 + ", " + y2 + "," + z2 + ", '" + world2 + "')";
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
}

