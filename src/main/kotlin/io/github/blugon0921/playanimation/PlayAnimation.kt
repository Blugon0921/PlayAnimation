package io.github.blugon0921.playanimation

import io.github.blugon0921.playanimation.commands.AnimationCommand
import net.kyori.adventure.text.Component.text
import org.bukkit.Bukkit
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class PlayAnimation : JavaPlugin(),Listener {

    val videos = File("plugins/PlayAnimation")

    override fun onEnable() {
        logger.info("Plugin Enable")
        Bukkit.getPluginManager().registerEvents(this, this)

        getCommand("animation")!!.apply {
            setExecutor(AnimationCommand(this@PlayAnimation))
            tabCompleter = AnimationCommand(this@PlayAnimation)
        }

        videos.mkdirs()
    }

    override fun onDisable() {
        logger.info("Plugin Disable")
    }
}