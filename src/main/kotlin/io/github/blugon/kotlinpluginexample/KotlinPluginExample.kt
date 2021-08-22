package io.github.blugon.kotlinpluginexample

import org.bukkit.Bukkit
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

class KotlinPluginExample : JavaPlugin(),Listener {

    override fun onEnable() {
        logger.info("Plugin Enable")
        Bukkit.getPluginManager().registerEvents(this, this)
    }

    override fun onDisable() {
        logger.info("Plugin Disable")
    }
}