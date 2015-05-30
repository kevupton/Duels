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
    NOT_FINISHED_EDITTING ("Messages.Admin.NotFinishedEditting");
    
    private String config;
    
    DuelMessage(String config) {
        this.config = config;
    }
    
    public void sendTo(Player player) {
        String msg = Duels.getInstance().getConfig().getString(config);
        player.sendMessage(msg);
    }
}
