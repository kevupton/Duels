/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.kevupton.duels.utils;

import me.kevupton.duels.Duels;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author Kevin
 */
public enum DuelMessage {
    CANNOT_SEND_TO_SELF ("Messages.Player.SendToSelf"),
    DUEL_REQUEST_EXISTS ("Messages.Player.RequestExists"),
    NO_AVAILABLE_ARENAS ("Messages.Player.NoAvailableArenas"),
    DUEL_REQUEST_DOESNT_EXIST ("Messages.Player.RequestNotExists"),
    INVALID_COMMAND     ("Messages.Player.InvalidCommand"),
    NOT_EDITTING_ARENA  ("Messages.Admin.NotEdittingArena"),
    ARENA_NAME_EXISTS   ("Messages.Admin.ArenaNameExists"),
    NOT_FINISHED_EDITTING ("Messages.Admin.NotFinishedEditting"),
    ARENA_CREATE_SUCCESS ("Messages.Admin.ArenaCreateSuccess"),
    PLAYER_NOT_ONLINE ("Messages.Player.PlayerNotOnline"),
    SEND_DUEL_REQUEST ("Messages.Player.SendDuelRequest"),
    DUEL_LOADING_MSG ("Messages.Player.DuelLoading"),
    SEND_DUEL_SENT ("Messages.Player.SendDuelSent"),
    SEND_COUNTDOWN ("Messages.Title.SendCountdown"),
    DUEL_STARTED ("Messages.Title.DuelStarted"),
    DUEL_WON ("Messages.Title.DuelWon"),
    DUEL_LOST ("Messages.Title.DuelLost"),
    NOT_IN_ARENA ("Messages.Player.NotInArena"),
    RUN_OUT_OF_TIME  ("Messages.Title.OutOfTime"),
    PLAYER_CANCELED_DUEL ("Messages.Player.PlayerCanceledDuel"),
    ARENA_NOT_FOUND ("Messages.Admin.ArenaNotFound"),
    PLEASE_COMPLETE_ARENA ("Messages.Admin.PleaseCompleteArena"),
    ARENA_UPDATE_SUCCESS ("Messages.Admin.ArenaUpdateSuccess"),
    REMOVE_ARENA_SUCCESS ("Messages.Admin.SuccessRemoveArena"),
    CANCEL_ARENA_EDIT ("Messages.Admin.CancelArenaEdit"),
    SPAWN_1_SET ("Messages.Admin.Spawn1Set"),
    SPAWN_2_SET ("Messages.Admin.Spawn2Set"),
    STARTED_UPDATING_ARENA ("Messages.Admin.StartedUpdatingArena"),
    STARTED_CREATING_ARENA ("Messages.Admin.StartedCreatingArena"),
    SEND_LEAVE_EARLY ("Messages.Player.SendLeaveEarly"),
    UNABLE_TO_USE_COMMAND ("Messages.Player.UnableToCommand");
    
    private String config;
    
    DuelMessage(String config) {
        this.config = config;
    }
    
    public void sendTo(Player player) {
        String msg = Duels.getInstance().getConfig().getString(config);
        sendMessage(player, msg);
    }
    
    public void sendTo(Player player, String ... args) {
        String msg = Duels.getInstance().getConfig().getString(config);
        for (int i = 0; i < args.length; i++) {
            msg = msg.replaceAll("\\$" + (i + 1), args[i]);
        }
        sendMessage(player, msg);
    }

    public void sendTo(Player player, int ... args) {
        String msg = Duels.getInstance().getConfig().getString(config);
        for (int i = 0; i < args.length; i++) {
            msg = msg.replaceAll("\\$" + (i + 1), args[i] + "");
        }
        sendMessage(player, msg);
    }
    
    private void sendMessage(Player player, String msg) {
        msg = ChatColor.translateAlternateColorCodes('&', msg);
        player.sendMessage(msg);
    }
}
