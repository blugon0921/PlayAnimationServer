package kr.blugon.playanimationserver.commands.status

import kr.blugon.minestom_brigadier.BrigadierCommand
import kr.blugon.minestom_brigadier.getValue
import kr.blugon.playanimationserver.Bukkit
import kr.blugon.playanimationserver.showTpsActionbar
import kr.blugon.playanimationserver.tps
import net.minestom.server.MinecraftServer
import net.minestom.server.command.builder.arguments.ArgumentType
import kotlin.system.exitProcess

fun BrigadierCommand.tpsCommand() {
    register("tps") {
        then(ArgumentType.Boolean("toggle")) {
            executes { sender,context ->
                val toggle: Boolean by context
                showTpsActionbar = toggle
                sender.sendMessage("TPS Actionbar를 ${toggle}로 설정했습니다")
            }
        }
        executes { sender,context ->
            sender.sendMessage("TPS: $tps")
        }
    }
}