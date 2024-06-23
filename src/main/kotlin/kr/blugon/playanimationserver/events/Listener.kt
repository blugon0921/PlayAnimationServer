package kr.blugon.playanimationserver.events

import net.minestom.server.event.GlobalEventHandler


fun GlobalEventHandler.registerEventHandler(registrar: RegisterEventHandler.() -> Unit) {
    registrar(RegisterEventHandler(this))
}

class RegisterEventHandler(val globalEventHandler: GlobalEventHandler) {
    fun register(listener: Listener) = listener.register(globalEventHandler)
}

interface Listener {
    fun register(eventHandler: GlobalEventHandler)
}