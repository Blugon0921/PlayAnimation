package io.github.blugon0921.playanimation.commands

import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.TextDisplay
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.Transformation
import org.joml.Vector3f
import java.awt.Dimension
import java.awt.image.BufferedImage
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.util.*
import javax.imageio.ImageIO
import kotlin.concurrent.thread

class AnimationCommand(val plugin : JavaPlugin) : CommandExecutor, TabCompleter {

    private val videoPath = "plugins/PlayAnimation"
    private val videoPathFile = File("plugins/PlayAnimation")


    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(command.name == "animation") {
            if(sender.isOp) {
                if (args[0] == "play" && args.size == 2) {
                    if(File("${videoPath}/${args[1]}").listFiles() == null) {
                        sender.sendMessage(text("/animation <play/stop> <animationFolderName>").color(NamedTextColor.RED))
                        return false
                    }

                    endVideo()
                    playVideo(args[1])
                    return true
                } else if(args[0] == "stop") {
                    endVideo()
                    return true
                } else {
                    sender.sendMessage(text("/animation <play/stop> <animationFolderName>").color(NamedTextColor.RED))
                    return false
                }
            }
        }
        return false
    }


    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String>? {
        if(command.name == "animation") {
            val index = args.size-1
            val responses = mutableListOf<String>()
            if(args.size == 1) {
                responses.add("play")
                responses.add("stop")
            } else if(args.size == 2 && args[0] == "play") {
                val files = videoPathFile.listFiles() ?: return Collections.emptyList()
                for (video in files) {
                    if (video.isDirectory && video.listFiles() != null && video.listFiles()!!.isNotEmpty()) {
                        responses.add(video.name)
                    }
                }
            }
            val returns = mutableListOf<String>()

            for (r in responses) {
                if (r.lowercase().startsWith(args[index].lowercase())) {
                    returns.add(r)
                }
            }
            return returns
        }
        return Collections.emptyList()
    }

    fun playVideo(name: String) {
        val world = Bukkit.getWorld("world")!!
//        val location = Location(world, -33.28125, 2.875, -14.0)
        val location = Location(world, 4.0268, 2.875, -14.0)

        val frames = ArrayList<BufferedImage>()
        val thread = thread {
            Bukkit.broadcast(text("이미지 읽기 시작"))
            for (i in 0 until File("${videoPath}/${name}").listFiles()!!.size) {
                frames.add(ImageIO.read((File("${videoPath}/${name}/frame${i}.png"))))
            }
            Bukkit.broadcast(text("이미지 읽기 완료"))
        }
        var checkId = 0
        checkId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, {
            if(!thread.isAlive) {
                Bukkit.getOnlinePlayers().forEach { player ->
                    player.playSound(player.location, "pv.${name.lowercase()}", 1f, 1f)
                }

                Bukkit.getScheduler().cancelTask(checkId)
                val widthMove = 0.5
                val heightMove = 0.52

                val videoSize = Dimension(frames[0].width, frames[0].height)

                val textDisplaies = ArrayList<TextDisplay>()

                for (y in videoSize.height - 1 downTo 0) {
                    val textDisplay = world.spawn(location.clone().add(.0, heightMove * y, .0), TextDisplay::class.java)
                    textDisplay.lineWidth = 100000
                    textDisplay.backgroundColor = Color.fromARGB(0, 0, 0, 0)
                    textDisplay.isDefaultBackground = false
                    textDisplay.transformation = Transformation(
                        textDisplay.transformation.translation,
                        textDisplay.transformation.leftRotation,
                        Vector3f(2.5f, 2.5f, 1f),
                        textDisplay.transformation.rightRotation
                    )
                    textDisplay.addScoreboardTag("animation")
                    textDisplaies.add(textDisplay)
                }

                var frameNumber = 0

                var id = 0
                id = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, {
                    if (frameNumber != frames.size - 1) {
                        val frame = frames[frameNumber]

                        for (y in 0 until videoSize.height) {
                            val text = text()
                            for (x in 0 until videoSize.width) {
                                val pixelColor = java.awt.Color(frame.getRGB(x, y))
                                val pixelTextColor = TextColor.color(pixelColor.red, pixelColor.green, pixelColor.blue)
//                              text.append(text("■").color(pixelTextColor))
                                text.append(text("√").color(pixelTextColor))
                            }
                            textDisplaies[y].text(text.build())
                        }
                        frameNumber++
                    } else {
                        Bukkit.getScheduler().cancelTask(id)
                    }
                }, 0, 1)
            }
        }, 0, 1)
    }

    fun endVideo() {
        Bukkit.getScheduler().cancelTasks(plugin)
        Bukkit.getWorlds().forEach { world ->
            for(entity in world.entities) {
                if(entity !is TextDisplay) continue
                if(!entity.scoreboardTags.contains("animation")) continue
                entity.remove()
            }
        }
        Bukkit.getOnlinePlayers().forEach { player ->
            val files = videoPathFile.listFiles()
            if(files != null) {
                for (folder in files) {
                    if (folder.isDirectory) {
                        player.stopSound("pv.${folder.name.lowercase()}")
                    }
                }
            }
        }
    }
}
