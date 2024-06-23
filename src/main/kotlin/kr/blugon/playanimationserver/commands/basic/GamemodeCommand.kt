package kr.blugon.playanimationserver.commands.basic

import kr.blugon.minestom_brigadier.BrigadierCommand
import kr.blugon.minestom_brigadier.getValue
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.entity.GameMode
import net.minestom.server.entity.Player

fun BrigadierCommand.gamemodeCommand() {
    register("gamemode") {
        then(ArgumentType.String("mode")) {
            suggests(listOf("creative", "survival", "adventure", "spectator"))
            executes { sender, context ->
                val mode: String by context
                val gameMode = GameMode.valueOf(mode.uppercase())
                (sender as Player).gameMode = gameMode
                sender.sendMessage("자신의 게임 모드를 ${GamemodeCommand.koName[mode]} 모드로 설정했습니다")
            }
        }
    }
}
object GamemodeCommand {
    val koName = hashMapOf(
        "creative" to "크리에이티브",
        "survival" to "서바이벌",
        "adventure" to "모험",
        "spectator" to "관전"
    )
}