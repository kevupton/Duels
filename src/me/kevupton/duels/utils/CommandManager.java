/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.kevupton.duels.utils;

import java.util.logging.Level;
import java.util.logging.Logger;
import me.kevupton.duels.Duels;
import me.kevupton.duels.exceptions.ArenaException;
import me.kevupton.duels.exceptions.DatabaseException;
import me.kevupton.duels.exceptions.DuelCommandException;
import me.kevupton.duels.exceptions.DuelRequestException;
import me.kevupton.duels.processmanager.processes.DuelRequest;
import me.kevupton.duels.processmanager.processes.StartDuel;
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
        REMOVE_ARENA ("remove"),
        SET_SPAWN_1 ("setspawn1"),
        SET_SPAWN_2 ("setspawn2"),
        SAVE        ("save"),
        UPDATE_ARENA ("update"),
        LEAVE ("leave"),
        CANCEL ("cancel");
        
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
                    String cmd_arg = args[0];
                    if (cmd_arg.equals(DuelCommand.LEAVE.toString())) {
                        try {
                            Arena arena = Arena.getPlayerArena(player);
                            arena.endEarly();
                            DuelMessage.SEND_LEAVE_EARLY.sendTo(player);
                        } catch (ArenaException ex) {
                            DuelMessage.NOT_IN_ARENA.sendTo(player);
                        }
                    } else {
                        Player receiver = Duels.getInstance().getServer().getPlayer(args[0]);
                        if (receiver != null) {
                            if (player.equals(receiver)) {
                                DuelMessage.CANNOT_SEND_TO_SELF.sendTo(player);
                            } else {
                                try {
                                    DuelRequest.register(receiver, player);
                                    DuelMessage.SEND_DUEL_SENT.sendTo(player);
                                    DuelMessage.SEND_DUEL_REQUEST.sendTo(receiver, player.getName());
                                } catch (DuelRequestException ex) {
                                    DuelMessage.DUEL_REQUEST_EXISTS.sendTo(player);
                                }
                            }
                        } else {
                            DuelMessage.PLAYER_NOT_ONLINE.sendTo(player);
                        }
                    }
                } else if (args.length == 2 && 
                        args[0].toLowerCase().equals(DuelCommand.ACCEPT_DUEL.toString())) {
                    Player sender = Duels.getInstance().getServer().getPlayer(args[1]);
                    if (sender != null) {
                        try {
                            DuelRequest.acceptRequest(player, sender);
                            DuelMessage.DUEL_LOADING_MSG.sendTo(player, StartDuel.LOAD_TIME + "");
                            DuelMessage.DUEL_LOADING_MSG.sendTo(sender, StartDuel.LOAD_TIME + "");
                        } catch (DuelRequestException ex) {
                            DuelMessage.DUEL_REQUEST_DOESNT_EXIST.sendTo(player);
                        } catch (ArenaException ex) {
                            DuelMessage.NO_AVAILABLE_ARENAS.sendTo(player);
                            DuelMessage.NO_AVAILABLE_ARENAS.sendTo(sender);
                        }
                    } else {
                        DuelMessage.PLAYER_NOT_ONLINE.sendTo(player);
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
                                if (sub.equals(DuelCommand.UPDATE_ARENA)) {
                                    DuelMessage.STARTED_UPDATING_ARENA.sendTo(player, name);
                                } else {
                                    DuelMessage.STARTED_CREATING_ARENA.sendTo(player);
                                }
                            } else {
                                throw new DuelCommandException("Invalid Command");
                            }
                            break;
                        case CANCEL:
                            DuelMetaData.removeEdittingArena(player);
                            DuelMessage.CANCEL_ARENA_EDIT.sendTo(player);
                            break;
                        case REMOVE_ARENA:
                            if (args.length == 2) {
                                try {
                                    Arena.remove(args[1]);
                                    DuelMessage.REMOVE_ARENA_SUCCESS.sendTo(player);
                                } catch (ArenaException ex) {
                                    DuelMessage.ARENA_NOT_FOUND.sendTo(player);
                                }
                            } else {
                                throw new DuelCommandException("Invalid Command");
                            }
                            break;
                        case SET_SPAWN_1:
                            if (DuelMetaData.isEdittingArena(player)) {
                                DuelMetaData.updateArenaSpawn1(player, player.getLocation());
                                DuelMessage.SPAWN_1_SET.sendTo(player);
                            } else {
                                DuelMessage.NOT_EDITTING_ARENA.sendTo(player);
                            }
                            break;

                        case SET_SPAWN_2:
                            if (DuelMetaData.isEdittingArena(player)) {
                                DuelMetaData.updateArenaSpawn2(player, player.getLocation());
                                DuelMessage.SPAWN_2_SET.sendTo(player);
                            } else {
                                DuelMessage.NOT_EDITTING_ARENA.sendTo(player);
                            }
                            break;
                        
                        case SAVE:
                            Object[] data = DuelMetaData.getEdittingArenaData(player);
                            DuelCommand sub_cmd = (DuelCommand) data[3];
                            if (sub_cmd.equals(DuelCommand.CREATE_ARENA)) {
                                if (DuelMetaData.isCompletedEdittingArena(player)) {
                                    try {
                                        Arena.registerNew((String) data[0], (Location) data[1], (Location) data[2]);
                                        DuelMessage.ARENA_CREATE_SUCCESS.sendTo(player);
                                        DuelMetaData.removeEdittingArena(player);
                                    } catch (DatabaseException | ArenaException ex) {
                                        DuelMessage.ARENA_NAME_EXISTS.sendTo(player);
                                    }
                                } else {
                                    DuelMessage.PLEASE_COMPLETE_ARENA.sendTo(player);
                                }
                            } else {
                                Arena.updateArena(data);
                                DuelMessage.ARENA_UPDATE_SUCCESS.sendTo(player);
                                DuelMetaData.removeEdittingArena(player);
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
