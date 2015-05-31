/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.kevupton.duels.events;

import me.kevupton.duels.Duels;
import me.kevupton.duels.exceptions.ArenaException;
import me.kevupton.duels.utils.Arena;
import me.kevupton.duels.utils.DuelMetaData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 *
 * @author Kevin
 */
public class PlayerLeaveEvent implements Listener {
    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        try {
            if (!DuelMetaData.COMMAND_BAN.isOn(player) && 
                    !DuelMetaData.IN_ARENA.isOn(player)) {
                Arena a = Arena.getPlayerArena(player);
                if (DuelMetaData.COMMAND_BAN.isOn(player)) {
                    a.resetPlayer(player);
                    player.setHealth(0);
                    a.setLoser(player);
                } else if (DuelMetaData.IN_ARENA.isOn(player)) {
                    DuelMetaData.remove(player, DuelMetaData.IN_ARENA);
                    a.endEarly();
                }
            }
        } catch (ArenaException ex) {
            Duels.logInfo("Arena not found");
        }
        
    }
}
