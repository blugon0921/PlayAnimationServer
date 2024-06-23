package kr.blugon.playanimationserver.commands.play

import kr.blugon.minestom_brigadier.BrigadierCommand
import kr.blugon.minestom_brigadier.getValue
import kr.blugon.playanimationserver.play.scale
import net.minestom.server.command.builder.arguments.ArgumentType

fun BrigadierCommand.sizeCommand() {
    register("size") {
        then(ArgumentType.Double("size").min(0.01).max(10.0)) {
            executes { sender, context ->
                val size: Double by context
                scale = size
                sender.sendMessage("동영상 크기를 ${size}(으)로 설정했습니다")
            }
        }
    }
}