package kr.blugon.playanimationserver.events

import kr.blugon.playanimationserver.Bukkit
import kr.blugon.playanimationserver.randomColor
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.MinecraftServer
import net.minestom.server.event.GlobalEventHandler
import net.minestom.server.event.server.ServerListPingEvent
import java.io.FileInputStream
import java.io.IOException
import java.util.*

class ServerList: Listener {
    private val favicon: String?
        get() {
            return try {
                val stream = FileInputStream("server-icon.png")
                "data:image/png;base64," + Base64.getEncoder().encodeToString(stream.readAllBytes())
            } catch (e: IOException) {
                null
            }
        }

    override fun register(eventHandler: GlobalEventHandler) {
        eventHandler.addListener(ServerListPingEvent::class.java) { event->
            val responseData = event.responseData
            responseData.description = text("블루곤").color(NamedTextColor.BLUE).append(text(" 서버").color(NamedTextColor.GOLD))
                .append(text("\n[").color(NamedTextColor.GOLD)).append(text(MinecraftServer.VERSION_NAME).color(randomColor())).append(text("]").color(NamedTextColor.GOLD))
            responseData.favicon = favicon
            responseData.maxPlayer = 1000
            responseData.addEntries(Bukkit.getOnlinePlayers())
        }
    }
}