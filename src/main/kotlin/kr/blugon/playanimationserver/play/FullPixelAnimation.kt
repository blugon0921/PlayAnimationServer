package kr.blugon.playanimationserver.play

import kotlinx.coroutines.*
import kr.blugon.playanimationserver.Bukkit
import kr.blugon.playanimationserver.broadcast
import kr.blugon.playanimationserver.tickRate
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.TextColor
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.Entity
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.metadata.display.TextDisplayMeta
import net.minestom.server.instance.InstanceContainer
import net.minestom.server.timer.Task
import net.minestom.server.timer.TaskSchedule
import java.awt.Color
import java.awt.Dimension
import java.awt.image.BufferedImage
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import javax.imageio.ImageIO
import kotlin.collections.ArrayList
import kotlin.concurrent.thread
import kotlin.time.Duration

@OptIn(DelicateCoroutinesApi::class)
class FullPixelAnimation(val name: String, val instanceContainer: InstanceContainer) {
//    val floorPos = Pos(4.0268, 2.875, -14.0)
    val floorPos = Pos(0.5, 2.875, 0.99, 180f, 0f)

    suspend fun play() {
        broadcast("이미지 읽기 시작")
        val frames = GlobalScope.async(Dispatchers.IO) {
            ArrayList<BufferedImage>().apply {
                for (i in 0 until File("${videoPath}/${name}").listFiles()!!.size) {
                    this.add(ImageIO.read((File("${videoPath}/${name}/frame${i}.png"))))
                }
            }
        }.await()
        broadcast("이미지 읽기 완료")

        val videoSize = Dimension(frames[0].width, frames[0].height)

        val displays = (
                spawnDisplay(instanceContainer, floorPos, .0, 20.0) to
                spawnDisplay(instanceContainer, floorPos, .01, 20.0)
            ) to (
                spawnDisplay(instanceContainer, floorPos, .0, 20.0-0.0625) to
                spawnDisplay(instanceContainer, floorPos, .01, 20.0-0.0625)
            )

        var frameNumber = 0

        val schedule = MinecraftServer.getSchedulerManager()
        var before = SimpleDateFormat("ss").format(Date())
        var count = 0

        Bukkit.getOnlinePlayers().forEach { player->
            player.playSound(Sound.sound(Key.key("minecraft", "pv.${name.lowercase().split(".")[0]}"), Sound.Source.MASTER, 0.5f, 1f))
        }
        schedule.buildTask {
            broadcast("재생 시작")
            playTask = schedule.buildTask {
                thread {
                    if (frameNumber != frames.size - 1) {
                        val frame = frames[frameNumber]

                        val text = text()
                        val text2 = text()
                        for (y in 0 until videoSize.height step 2) {
                            for (x in 0 until videoSize.width) {
                                val pixelColor = Color(frame.getRGB(x, y))
                                val pixelTextColor = TextColor.color(pixelColor.red, pixelColor.green, pixelColor.blue)
                                text.append(text("■").color(pixelTextColor))
                            }
                            text.append(text("\n"))
                        }
                        for (y in 1 until videoSize.height step 2) {
                            for (x in 0 until videoSize.width) {
                                val pixelColor = Color(frame.getRGB(x, y))
                                val pixelTextColor = TextColor.color(pixelColor.red, pixelColor.green, pixelColor.blue)
                                text2.append(text("■").color(pixelTextColor))
                            }
                            text2.append(text("\n"))
                        }
                        displays.first.forEach { display ->
                            display.editEntityMeta(TextDisplayMeta::class.java) {
                                it.text = text.build()
                            }
                        }
                        displays.second.forEach { display ->
                            display.editEntityMeta(TextDisplayMeta::class.java) {
                                it.text = text2.build()
                            }
                        }
                        frameNumber++
                        val now = SimpleDateFormat("ss").format(Date())
                        if(now != before) {
                            if(count >= tickRate*0.5) frameNumber+=(tickRate-count)
                            count = 0
                            before = now
                        }
                        count++
                    } else {
                        playTask!!.cancel()
                        playTask = null
                    }
                }
            }.repeat(TaskSchedule.millis(1)).schedule()
        }.delay(TaskSchedule.millis(startDelay)).schedule()
    }
}

fun spawnDisplay(instanceContainer: InstanceContainer, floorPos: Pos, addX: Double, addY: Double): Entity {
    return Entity(EntityType.TEXT_DISPLAY).apply {
        val pos = floorPos.add(addX, addY, .0)
        this.setInstance(instanceContainer, pos)
        this.teleport(pos)
        this.setNoGravity(true)
        this.editEntityMeta(TextDisplayMeta::class.java) {
            it.lineWidth = Int.MAX_VALUE
            it.backgroundColor = 0
            it.isUseDefaultBackground = false
            it.scale = Vec(0.4, 0.5, 1.0)
        }
    }
}

inline fun <T> Pair<T, T>.forEach(action: (T) -> Unit) {
    action(this.first)
    action(this.second)
}