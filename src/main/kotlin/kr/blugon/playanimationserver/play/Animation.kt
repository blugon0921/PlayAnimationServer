package kr.blugon.playanimationserver.play

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kr.blugon.playanimationserver.Bukkit
import kr.blugon.playanimationserver.broadcast
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
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
import javax.imageio.ImageIO
import kotlin.collections.ArrayList

const val videoPath = "./animations"
var playTask: Task? = null
var scale = 2.5
@OptIn(DelicateCoroutinesApi::class)
class Animation(val name: String, val instanceContainer: InstanceContainer) {
//    val floorPos = Pos(4.0268, 2.875, -14.0)
    val floorPos = Pos(0.5, 2.875, 0.99, 180f, 0f)
    val fps = 60

    suspend fun play() {
        val readTask = GlobalScope.async {
            val frames = ArrayList<BufferedImage>()
            broadcast("이미지 읽기 시작")
            for (i in 0 until File("${videoPath}/${name}").listFiles()!!.size) {
                frames.add(ImageIO.read((File("${videoPath}/${name}/frame${i}.png"))))
            }
            broadcast("이미지 읽기 완료")
            return@async frames
        }
        val frames = readTask.await()

        val ratio = 5.0

        val heightMove = scale/ratio
        val videoSize = Dimension(frames[0].width, frames[0].height)
        val textDisplays = ArrayList<Entity>()

        for (y in (videoSize.height - 1) downTo 0) {
            Entity(EntityType.TEXT_DISPLAY).apply {
                val pos = floorPos.add(.0, heightMove*y, .0)
                this.setInstance(instanceContainer, pos)
                this.teleport(pos)
                this.setNoGravity(true)
                this.editEntityMeta(TextDisplayMeta::class.java) {
                    it.lineWidth = 100000
                    it.backgroundColor = 0
                    it.isUseDefaultBackground = false
                    it.scale = Vec(scale, scale, 1.0)
                }
                textDisplays.add(this)
            }
        }

        var frameNumber = 0

        Bukkit.getOnlinePlayers().forEach { player->
            player.playSound(Sound.sound(Key.key("minecraft", "pv.${name.lowercase()}"), Sound.Source.MASTER, 1f, 1f))
        }
        val schedule = MinecraftServer.getSchedulerManager()
        var before = SimpleDateFormat("ss").format(Date())
        var count = 0
        playTask = schedule.buildTask {
            if (frameNumber != frames.size - 1) {
                val frame = frames[frameNumber]

                for (y in 0 until videoSize.height) {
                    val text = text()
                    for (x in 0 until videoSize.width) {
                        val pixelColor = Color(frame.getRGB(x, y))
                        val pixelTextColor = TextColor.color(pixelColor.red, pixelColor.green, pixelColor.blue)
//                              text.append(text("■").color(pixelTextColor))
                        text.append(text("√").color(pixelTextColor))
                    }
                    textDisplays[y].editEntityMeta(TextDisplayMeta::class.java) {
                        it.text = text.build()
                    }
                }
                frameNumber++
                val now = SimpleDateFormat("ss").format(Date())
                if(now != before) {
//                    for (player in Bukkit.getOnlinePlayers()) {
//                        player.sendActionBar(text("TPS(FPS): $count"))
//                    }
                    if(count >= 30) frameNumber+=((60-count)*(60/fps))
                    count = 0
                    before = now
                }
                count++
            } else {
                playTask!!.cancel()
                playTask = null
            }
        }.repeat(TaskSchedule.millis(1)).schedule()
    }
}