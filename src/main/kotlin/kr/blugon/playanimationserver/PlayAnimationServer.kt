package kr.blugon.playanimationserver

import kr.blugon.minestom_brigadier.registerCommandHandler
import kr.blugon.playanimationserver.commands.basic.gamemodeCommand
import kr.blugon.playanimationserver.commands.basic.stopCommand
import kr.blugon.playanimationserver.commands.playAnimationCommand
import kr.blugon.playanimationserver.commands.sizeCommand
import kr.blugon.playanimationserver.commands.stopAnimationCommand
import kr.blugon.playanimationserver.events.PlayerJoin
import kr.blugon.playanimationserver.events.ServerList
import kr.blugon.playanimationserver.events.registerEventHandler
import kr.blugon.playanimationserver.world.createFlatWorld
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player

val worldSpawnPoint = Pos(.5, .0, .5)
fun main() {
    val minecraftServer = MinecraftServer.init()
    MinecraftServer.setBrandName("PlayAnimation")

    val instanceManager = MinecraftServer.getInstanceManager()
    val commandManager = MinecraftServer.getCommandManager()
    val eventHandler = MinecraftServer.getGlobalEventHandler()

    val instance = instanceManager.createInstanceContainer()
    instance.createFlatWorld()
    instance.time = 6000
    instance.timeRate = 0

    //Events
    eventHandler.registerEventHandler {
        register(PlayerJoin(instance))
        register(ServerList())
    }

    //Commands
    commandManager.setUnknownCommandCallback { sender, s -> sender.sendMessage(text("알 수 없거나 불완전한 명령어 입니다").color(NamedTextColor.RED)) }
    commandManager.registerCommandHandler {
        playAnimationCommand(instance)
        stopAnimationCommand(instance)
        sizeCommand()

        gamemodeCommand()
        stopCommand()
    }

    minecraftServer.start("127.0.0.1", 25565)
}

fun broadcast(text: String) = broadcast(text(text))
fun broadcast(text: Component) {
    Bukkit.getOnlinePlayers().forEach { player->
        player.sendMessage(text)
    }
}

object Bukkit {
    fun getOnlinePlayers(): Collection<Player> {
        return MinecraftServer.getConnectionManager().onlinePlayers
    }
}

fun randomColor(): TextColor = TextColor.color((Math.random()*255).toInt(), (Math.random()*255).toInt(), (Math.random()*255).toInt())