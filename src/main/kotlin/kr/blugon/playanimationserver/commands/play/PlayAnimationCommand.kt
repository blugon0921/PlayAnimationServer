package kr.blugon.playanimationserver.commands.play

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kr.blugon.minestom_brigadier.BrigadierCommand
import kr.blugon.minestom_brigadier.getValue
import kr.blugon.nodefs.FileType
import kr.blugon.nodefs.Fs
import kr.blugon.playanimationserver.play.Animation
import kr.blugon.playanimationserver.play.scale
import kr.blugon.playanimationserver.play.videoPath
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.GameMode
import net.minestom.server.entity.Player
import net.minestom.server.instance.InstanceContainer
import java.io.File
import javax.imageio.ImageIO


@OptIn(DelicateCoroutinesApi::class)
fun BrigadierCommand.playAnimationCommand(instanceContainer: InstanceContainer) {
    register("playanimation") {
        then(ArgumentType.String("name")) {
            suggests { _,_ ->
                val animations = Fs.readdir("./animations", FileType.FOLDER)
                ArrayList<String>().apply {
                    animations.forEach { add(it.name) }
                }.toList()
            }
            executes { sender, context ->
                val name: String by context
                val images = File("$videoPath/${name}").listFiles()
                if(images == null || images.isEmpty()) {
                    sender.sendMessage("동영상이 존재하지 않습니다")
                    return@executes
                }
                val animation = Animation(name, instanceContainer)
                (sender as Player).gameMode = GameMode.SPECTATOR
                val height = ImageIO.read(images[0]).height
                val yRatio = height*(6.0/60.0)
                val zRatio = height*(-6/60.0)
                sender.teleport(Pos(0.5, scale*yRatio, scale*zRatio, 0f, 0f))
                GlobalScope.launch {
                    animation.play()
                }
            }
        }
    }
}