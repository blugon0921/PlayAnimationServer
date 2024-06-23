package kr.blugon.playanimationserver.commands

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kr.blugon.minestom_brigadier.BrigadierCommand
import kr.blugon.minestom_brigadier.getValue
import kr.blugon.nodefs.FileType
import kr.blugon.nodefs.Fs
import kr.blugon.playanimationserver.play.Animation
import kr.blugon.playanimationserver.play.scale
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.GameMode
import net.minestom.server.entity.Player
import net.minestom.server.instance.InstanceContainer


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
                val animation = Animation(name, instanceContainer)
                (sender as Player).gameMode = GameMode.SPECTATOR
                val yRatio = 7
                val zRatio = -7.6
                sender.teleport(Pos(0.5, scale*yRatio, scale*zRatio, 0f, 0f))
                GlobalScope.launch {
                    animation.play()
                }
            }
        }
    }
}