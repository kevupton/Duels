/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.kevupton.duels.processes;

import me.kevupton.duels.utils.Arena;

/**
 *
 * @author Kevin
 */
public class TeleportPlayers implements Runnable {
    private Arena arena;
    
    public TeleportPlayers(Arena a) {
        arena = a;
    }
    
    @Override
    public void run() {
        arena.teleportPlayers();
    }
}
