/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.kevupton.duels.utils;

import me.kevupton.duels.Duels;
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
    NOT_IN_ARENA ("Messages.Player.NotInArena");
    
    private String config;
    
    DuelMessage(String config) {
        this.config = config;
    }
    
    public void sendTo(Player player) {
        String msg = Duels.getInstance().getConfig().getString(config);
        player.sendMessage(msg);
    }
    
    public void sendTo(Player player, String ... args) {
        String msg = Duels.getInstance().getConfig().getString(config);
        for (int i = 0; i < args.length; i++) {
            msg = msg.replaceAll("\\$" + (i + 1), args[i]);
        }
        player.sendMessage(msg);
    }
    
}
