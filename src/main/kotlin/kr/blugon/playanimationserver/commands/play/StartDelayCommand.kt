package kr.blugon.playanimationserver.commands.play

import kr.blugon.minestom_brigadier.BrigadierCommand
import kr.blugon.minestom_brigadier.getValue
import kr.blugon.playanimationserver.play.scale
import kr.blugon.playanimationserver.play.startDelay
import net.minestom.server.command.builder.arguments.ArgumentType

fun BrigadierCommand.startDelayCommand() {
    register("startdelay") {
        then(ArgumentType.Long("delay").min(0).max(10000)) {
            executes { sender, context ->
                val delay: Long by context
                startDelay = delay
                sender.sendMessage("동영상 시작 딜레이를 ${delay}ms로 설정했습니다")
            }
        }
    }
}