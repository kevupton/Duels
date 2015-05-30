/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.kevupton.duels.utils;

import me.kevupton.duels.Duels;
import me.kevupton.duels.exceptions.ArenaException;
import me.kevupton.duels.exceptions.DatabaseException;
import me.kevupton.duels.exceptions.DuelCommandException;
import me.kevupton.duels.exceptions.DuelRequestException;
import me.kevupton.duels.processmanager.processes.DuelRequest;
import org.bukkit.Location;
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
    
    public enum DuelCommand {
        DUEL ("duel"),
        DUEL_ADMIN ("dueladmin"),
        ACCEPT_DUEL ("accept"),
        CREATE_ARENA ("create"),
        SET_SPAWN_1 ("setspawn1"),
        SET_SPAWN_2 ("setspawn2"),
        SAVE        ("save"),
        UPDATE_ARENA ("update");
        
        private String command;
        
        DuelCommand(String command) {
            this.command = command;
        }
        
        public static DuelCommand getCommand(String cmd) throws DuelCommandException {
            cmd = cmd.toLowerCase();
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
                if (args.length == 1) {
                    Player receiver = Duels.getInstance().getServer().getPlayer(args[0]);
                    if (receiver != null) {
                        if (player.equals(receiver)) {
                            DuelMessage.CANNOT_SEND_TO_SELF.sendTo(player);
                        } else {
                            try {
                                DuelRequest.register(receiver, player);
                            } catch (DuelRequestException ex) {
                                DuelMessage.DUEL_REQUEST_EXISTS.sendTo(player);
                            }
                        }
                    }
                } else if (args.length == 2 && 
                        args[0].toLowerCase().equals(DuelCommand.ACCEPT_DUEL.toString())) {
                    Player sender = Duels.getInstance().getServer().getPlayer(args[1]);
                    if (sender != null) {
                        try {
                            DuelRequest.acceptRequest(player, sender);
                        } catch (DuelRequestException ex) {
                            DuelMessage.DUEL_REQUEST_DOESNT_EXIST.sendTo(player);
                        } catch (ArenaException ex) {
                            DuelMessage.NO_AVAILABLE_ARENAS.sendTo(player);
                            DuelMessage.NO_AVAILABLE_ARENAS.sendTo(sender);
                        }
                    }
                } else {
                    throw new DuelCommandException("Invalid Command");
                }
                return true;
            case DUEL_ADMIN:
                if (args.length >= 1) {
                    DuelCommand sub = DuelCommand.getCommand(args[0]);
                    switch (sub) {
                        case UPDATE_ARENA:
                        case CREATE_ARENA:
                            if (args.length == 2) {
                                String name = args[1];
                                DuelMetaData.updateArenaName(player, name, sub);
                            } else {
                                throw new DuelCommandException("Invalid Command");
                            }
                            break;
                        case SET_SPAWN_1:
                            if (DuelMetaData.isEdittingArena(player)) {
                                DuelMetaData.updateArenaSpawn1(player, player.getLocation());
                            } else {
                                DuelMessage.NOT_EDITTING_ARENA.sendTo(player);
                            }
                            break;

                        case SET_SPAWN_2:
                            if (DuelMetaData.isEdittingArena(player)) {
                                DuelMetaData.updateArenaSpawn2(player, player.getLocation());
                            } else {
                                DuelMessage.NOT_EDITTING_ARENA.sendTo(player);
                            }
                            break;
                        
                        case SAVE:
                            if (DuelMetaData.isCompletedEdittingArena(player)) {
                                Object[] data = DuelMetaData.getEdittingArenaData(player);
                                try {
                                    DuelCommand sub_cmd = (DuelCommand) data[3];
                                    if (sub_cmd.equals(DuelCommand.CREATE_ARENA)) {
                                        Duels.theDatabase().registerArena((String) data[0], (Location) data[1], (Location) data[2]);
                                    } else {
                                        
                                    }
                                    DuelMetaData.removeEdittingArena(player);
                                } catch (DatabaseException ex) {
                                    DuelMessage.ARENA_NAME_EXISTS.sendTo(player);
                                }
                            } else {
                                
                            }
                            break;
                    }
                } else {
                    throw new DuelCommandException("Invalid Command");
                }
                return true;
        }
        return false;
    }
}
