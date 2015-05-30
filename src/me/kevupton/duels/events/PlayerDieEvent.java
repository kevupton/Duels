/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.kevupton.duels.events;

import me.kevupton.duels.Duels;
import me.kevupton.duels.exceptions.ArenaException;
import me.kevupton.duels.utils.Arena;
import me.kevupton.duels.utils.DuelsMetaData;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

/**
 *
 * @author Kevin
 */
public class PlayerDieEvent implements Listener {
    @EventHandler
    public void onPlayerDie(EntityDeathEvent event) {
        if (event.getEntityType().equals(EntityType.PLAYER)) {
            Player player = (Player) event.getEntity();
            if (DuelsMetaData.IN_ARENA.isOn(player)) {
                try {
                    Arena arena = Arena.getPlayerArena(player);
                    arena.setLoser(player);
                } catch (ArenaException ex) {
                    Duels.logInfo("ERROR: Player expected in arena but not found");
                }
            }
        }
    }
}
