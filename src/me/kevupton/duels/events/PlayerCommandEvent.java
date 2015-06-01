/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.kevupton.duels.events;

import me.kevupton.duels.Duels;
import me.kevupton.duels.utils.DuelMessage;
import me.kevupton.duels.utils.DuelMetaData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 *
 * @author Kevin
 */
public class PlayerCommandEvent implements Listener {
    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        Duels.logInfo(event.getMessage());
        if (DuelMetaData.COMMAND_BAN.isOn(player)) {
            DuelMessage.UNABLE_TO_USE_COMMAND.sendTo(player);
            event.setCancelled(true);
        }
    }
}
