/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.kevupton.duels.utils;

import me.kevupton.duels.exceptions.DuelCommandException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Kevin
 */
public class CommandManager {
    private CommandSender sender;
    private Command command;
    private String command_label;
    private String[] args;
    
    private enum DuelCommand {
        DUEL ("duel"),
        DUEL_ADMIN ("dueladmin");
        
        private String command;
        
        DuelCommand(String command) {
            this.command = command;
        }
        
        public static DuelCommand getCommand(String cmd) throws DuelCommandException {
            for (DuelCommand c: DuelCommand.values()) {
                if (c.toString().equals(cmd)) {
                    return c;
                }
            }
            throw new DuelCommandException("No command found");
        }
        
        @Override 
        public String toString() {
            return command;
        }
    }
    
    public CommandManager(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        this.sender = sender;
        this.command = cmd;
        this.command_label = commandLabel;
        this.args = args;
    }

    public boolean execute() throws DuelCommandException {
        DuelCommand cmd = DuelCommand.getCommand(command.getName());
        Player player = (Player) sender;
        
        switch (cmd) {
            case DUEL:
                
                return true;
            case DUEL_ADMIN:
                
                return true;
        }
        return false;
    }
}
