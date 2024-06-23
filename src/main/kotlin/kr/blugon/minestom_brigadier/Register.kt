package kr.blugon.minestom_brigadier

import net.minestom.server.command.CommandManager
import net.minestom.server.command.builder.Command
import java.util.*


fun CommandManager.registerCommandHandler(command: BrigadierCommand.() -> Unit) {
    val commandHandler = BrigadierCommand(this)
    command(commandHandler)
}

class BrigadierCommand(val commandManager: CommandManager) {
    lateinit var command: Command

    fun register(name: String, vararg aliases: String, node: RootBrigadierNode.() -> Unit) {
        command = Command(name, *aliases)
        node(RootBrigadierNode(command))
        commandManager.register(command)
    }
}