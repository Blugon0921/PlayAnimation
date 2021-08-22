package io.github.blugon.meterpersecond

import net.md_5.bungee.api.ChatColor
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.plugin.java.JavaPlugin
import java.util.HashMap
import kotlin.math.floor


class MeterPerSecond : JavaPlugin(),Listener {

    val move_block = HashMap<Player, Float>()

    override fun onEnable() {
        println("${ChatColor.WHITE}[GetMS] plugin enabled")
        Bukkit.getPluginManager().registerEvents(this, this)
        for(players in Bukkit.getOnlinePlayers()) {
            move_block[players] = 0f
        }

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, {
            for(players in Bukkit.getOnlinePlayers()) {
                players.sendActionBar("${floor(move_block[players]!!*100)/100}m/s")
                move_block[players] = 0f
            }
        }, 0, 20)
    }

    override fun onDisable() {
        println("${ChatColor.WHITE}[MeterPerSecond] plugin disabled")
    }

    @EventHandler
    fun join(event : PlayerJoinEvent) {
        move_block[event.player] = 0f
    }

    @EventHandler
    fun ms(event : PlayerMoveEvent) {
        val player = event.player

        move_block[player] = move_block[player]!! + event.to.distance(event.from).toFloat()
    }
}