package kr.blugon.playanimationserver.commands.basic

import kr.blugon.minestom_brigadier.BrigadierCommand
import kr.blugon.minestom_brigadier.getValue
import kr.blugon.playanimationserver.Bukkit
import net.minestom.server.MinecraftServer
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.entity.GameMode
import net.minestom.server.entity.Player
import kotlin.system.exitProcess

fun BrigadierCommand.stopCommand() {
    register("stop") {
        executes { _,_ ->
            Bukkit.getOnlinePlayers().forEach { player->
                player.kick("Server closed")
            }
            MinecraftServer.stopCleanly()
            exitProcess(0)
        }
    }
}