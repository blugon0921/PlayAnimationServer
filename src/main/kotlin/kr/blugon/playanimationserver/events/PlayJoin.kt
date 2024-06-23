package kr.blugon.playanimationserver.events

import kr.blugon.playanimationserver.worldSpawnPoint
import net.minestom.server.MinecraftServer
import net.minestom.server.entity.GameMode
import net.minestom.server.entity.PlayerSkin
import net.minestom.server.event.GlobalEventHandler
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent
import net.minestom.server.instance.InstanceContainer
import net.minestom.server.timer.TaskSchedule


class PlayerJoin(val instanceContainer: InstanceContainer): Listener {

    override fun register(eventHandler: GlobalEventHandler) {
        eventHandler.addListener(AsyncPlayerConfigurationEvent::class.java) { event->
            val player = event.player
            event.spawningInstance = instanceContainer
            player.respawnPoint = worldSpawnPoint
            player.gameMode = GameMode.CREATIVE
            val schedule = MinecraftServer.getSchedulerManager()
            schedule.buildTask {
                player.skin = PlayerSkin.fromUsername(player.username)
            }.delay(TaskSchedule.millis(1000)).schedule()
        }
    }
}