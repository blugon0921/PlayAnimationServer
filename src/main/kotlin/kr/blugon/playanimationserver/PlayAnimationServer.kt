package kr.blugon.playanimationserver

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kr.blugon.minestom_brigadier.registerCommandHandler
import kr.blugon.playanimationserver.commands.basic.gamemodeCommand
import kr.blugon.playanimationserver.commands.basic.stopCommand
import kr.blugon.playanimationserver.commands.play.playAnimationCommand
import kr.blugon.playanimationserver.commands.play.sizeCommand
import kr.blugon.playanimationserver.commands.play.startDelayCommand
import kr.blugon.playanimationserver.commands.play.stopAnimationCommand
import kr.blugon.playanimationserver.commands.status.tpsCommand
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
import net.minestom.server.timer.TaskSchedule
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread
import kotlin.system.exitProcess

var showTpsActionbar = false
var tps = 0
val worldSpawnPoint = Pos(.5, .0, .5)
var tickRate: Int = 0
fun main(args: Array<String>) {
    var inputTickRate = 20
    if(args.isNotEmpty()) {
        try {
            inputTickRate = args[0].toInt()
        } catch (e: NumberFormatException) {
            println("Invalid arguments.")
            exitProcess(1)
        }
    }
    tickRate = inputTickRate
    println("TickRate: $tickRate")
    MinecraftServer.setCompressionThreshold(0)
    MinecraftServer.setTickrate(inputTickRate)
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
        startDelayCommand()

        gamemodeCommand()
        stopCommand()
        tpsCommand()
    }

    val schedule = MinecraftServer.getSchedulerManager()
    var before = SimpleDateFormat("ss").format(Date())
    var count = 0
    schedule.buildTask {
        val now = SimpleDateFormat("ss").format(Date())
        if(now != before) {
            if(showTpsActionbar) {
                for (player in Bukkit.getOnlinePlayers()) {
                    player.sendActionBar(text("TPS: $count"))
                }
            }
            tps = count
            count = 0
            before = now
        }
        count++
    }.repeat(TaskSchedule.millis(1)).schedule()

    minecraftServer.start("127.0.0.1", 25565)
    GlobalScope.launch {
        while (true) {
            val scanner = Scanner(System.`in`)
            val input = scanner.next()
            if(input == "stop") {
                Bukkit.getOnlinePlayers().forEach { player->
                    player.kick("Server closed")
                }
                MinecraftServer.stopCleanly()
                exitProcess(0)
            } else if(input == "tps") {
                println("TPS: $tps")
            } else {
                println("unknown command")
            }
        }
    }
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