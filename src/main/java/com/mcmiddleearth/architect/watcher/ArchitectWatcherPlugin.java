/*
 * Copyright (C) 2019 MCME
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.mcmiddleearth.architect.watcher;

import com.mcmiddleearth.architect.additionalListeners.AdditionalProtectionListener;
import com.mcmiddleearth.architect.additionalListeners.GameMechanicsListener;
import com.mcmiddleearth.architect.noPhysicsEditor.NoPhysicsListener;
import com.mcmiddleearth.architect.specialBlockHandling.listener.DoorListener;
import com.mcmiddleearth.architect.specialBlockHandling.listener.FurnaceListener;
import com.mcmiddleearth.architect.specialBlockHandling.listener.SpecialBlockListener;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Eriol_Eandur
 */
public class ArchitectWatcherPlugin extends JavaPlugin implements Listener {
    
    private final String[] expectedConfirmations = new String[]{
              GameMechanicsListener.class.getSimpleName(),
              AdditionalProtectionListener.class.getSimpleName(),
              NoPhysicsListener.class.getSimpleName(),
              FurnaceListener.class.getSimpleName(),
              DoorListener.class.getSimpleName(),
              SpecialBlockListener.class.getSimpleName()
            };
    
    @Override
    public void onEnable() {
        saveDefaultConfig();
        try {
            Bukkit.getPluginManager().registerEvents(this, this);
            new BukkitRunnable() {
                @Override
                public void run() {
                    WatcherEvent event = new WatcherEvent();
                    //getLogger().info("polling confirmations");
                    Bukkit.getPluginManager().callEvent(event);
                }
            }.runTaskTimer(this, 10, getConfig().getInt("watcherTicks"));
            getLogger().info("MCME-Architect watcher enabled!");
        } catch(NoClassDefFoundError ex) {
            getLogger().warning("Architect Plugin not found. Stopping Server...");
            Bukkit.getServer().shutdown();
        }
    }
    
    @Override
    public void onDisable() {
        getLogger().info("MCME-Architect watcher disabled!");
    }
    
    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = false) 
    public void onWatcherEvent(WatcherEvent event) {
        //getLogger().info("Checking confirmations");
        Set<String> confirmations = event.getConfirmations();
        boolean shutdown = false;
        for(String search: expectedConfirmations) {
            //getLogger().info("checking "+search);
            if(!confirmations.contains(search)) {
                shutdown = true;
                getLogger().warning(search+" stopped responding.");
                if(getConfig().getBoolean("instantStopp",false)) {
                    break;
                }
            }
        }
        if(shutdown) {
            getLogger().warning("Stopping Server...");
            Bukkit.getServer().shutdown();
        }
    }
}
 