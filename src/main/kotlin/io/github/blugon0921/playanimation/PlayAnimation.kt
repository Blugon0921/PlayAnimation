package io.github.blugon0921.playanimation

import io.github.blugon0921.playanimation.commands.Kommand
import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class PlayAnimation : JavaPlugin(),Listener {

    val goodbye = File("plugins/PlayAnimation/Videos")

    override fun onEnable() {
        logger.info("Plugin Enable")
        Bukkit.getPluginManager().registerEvents(this, this)

        getCommand("animation")!!.apply {
            setExecutor(Kommand())
            tabCompleter = Kommand()
        }

        goodbye.mkdirs()
    }

    override fun onDisable() {
        logger.info("Plugin Disable")
    }
}