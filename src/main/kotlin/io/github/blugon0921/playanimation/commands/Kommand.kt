package io.github.blugon0921.playanimation.commands

import io.github.blugon0921.playanimation.PlayAnimation
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.util.*
import javax.imageio.ImageIO
import kotlin.collections.ArrayList


class Kommand : CommandExecutor, TabCompleter {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(command.name == "animation") {
            if(sender.isOp) {
                if (args[0] == "play" && args.size == 2) {
                    if(File("plugins/PlayAnimation/Videos/${args[1].replace(".mp4", "")}").listFiles() == null) {
                        sender.sendMessage("/animation <play/stop> <animationFolderName>")
                        return false
                    }

                    Bukkit.getScheduler().cancelTasks(JavaPlugin.getPlugin(PlayAnimation::class.java))

                    val frames = ArrayList<File>()
                    for (i in 0 until File("plugins/PlayAnimation/Videos/${args[1].replace(".mp4", "")}").listFiles()!!.size) {
                        frames.add(File("plugins/PlayAnimation/Videos/${args[1].replace(".mp4", "")}/frame${i}.png"))
                    }

                    for (i in frames.indices) {
                        println(frames[i])
                    }


                    var frame_number = 0

                    Bukkit.getScheduler().scheduleAsyncRepeatingTask(JavaPlugin.getPlugin(PlayAnimation::class.java), {
                        if (frame_number != frames.size - 1) {
                            val frame = frames[frame_number]
                            val frame_img = ImageIO.read(frame)

                            val world = Bukkit.getWorld("world")!!
                            var location = Location(world, 0.0 - frame_img.width / 10 / 2, (50.0/5)+10, 0.0)

                            for (y in 0 until frame_img.height) {
                                for (x in 0 until frame_img.width) {
                                    val kolor = java.awt.Color(frame_img.getRGB(x, y))
                                    val color = Particle.DustOptions(Color.fromRGB(kolor.red, kolor.green, kolor.blue), 1.5f)
                                    world.spawnParticle(Particle.REDSTONE, location, 1, 0.0, 0.0, 0.0, 0.0, color, true)
                                    location.add(0.1, 0.0, 0.0)
                                }
                                location = Location(world, 0.0 - frame_img.width / 10 / 2, 50.0 + (frame_img.height / 10)+10, 0.0)
                                location.subtract(0.0, (y / 10.0)+5, 0.0)
                            }
                            frame_number++
                        } else {
                            Bukkit.getScheduler().cancelTasks(JavaPlugin.getPlugin(PlayAnimation::class.java))
                        }
                    }, 0, 1)


                    return false
                } else if(args[0] == "stop") {
                    Bukkit.getScheduler().cancelTasks(JavaPlugin.getPlugin(PlayAnimation::class.java))
                    return false
                } else {
                    sender.sendMessage("/animation <play/stop> <animationFolderName>")
                    return false
                }
            }
        }
        return false
    }


    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String>? {
        if(command.name == "animation") {
            if(args.size == 1) {
                val returns1 = mutableListOf("play", "stop")
                val returns = mutableListOf<String>()

                for(returns2 in returns1) {
                    if(returns2.toLowerCase().startsWith(args[0].toLowerCase())) {
                        returns.add(returns2)
                    }
                }
                return returns

            } else if(args.size == 2 && args[0] == "play") {
                val files = File("plugins/PlayAnimation/Videos").listFiles()
                val returns1 = mutableListOf<String>()

                if (files != null) {
                    for (i in files) {
                        returns1.add(i.name)
                    }

                    val returns = mutableListOf<String>()

                    for (returns2 in returns1) {
                        if (returns2.toLowerCase().startsWith(args[1].toLowerCase())) {
                            if (!returns2.contains("mp4")) {
                                returns.add(returns2)
                            }
                        }
                    }
                    return returns

                }
            } else {
                return Collections.singletonList("")
            }
        }
        return null
    }
}
