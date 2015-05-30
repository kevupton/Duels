/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.kevupton.duels.events;

import me.kevupton.duels.utils.DuelMetaData;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 *
 * @author Kevin
 */
public class PlayerAttemptMoveEvent implements Listener {
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (DuelMetaData.PREVENT_MOVING.isOn(player)) {
            Location new_to = event.getFrom().clone();
            new_to.setDirection(event.getTo().getDirection());
            event.setTo(new_to);
        }
    }
}
