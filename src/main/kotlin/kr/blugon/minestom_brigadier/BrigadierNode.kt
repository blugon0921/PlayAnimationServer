package kr.blugon.minestom_brigadier

import net.minestom.server.command.CommandSender
import net.minestom.server.command.builder.Command
import net.minestom.server.command.builder.CommandContext
import net.minestom.server.command.builder.arguments.Argument
import net.minestom.server.command.builder.arguments.ArgumentType
import net.minestom.server.command.builder.exception.ArgumentSyntaxException
import net.minestom.server.command.builder.suggestion.SuggestionEntry
import kotlin.reflect.KProperty


interface BrigadierNode {
    fun then(literal: String, node: LiteralBrigadierNode.() -> Unit = {})
    fun <T> then(argument: Argument<T>, node: RequiredBrigadierNode<T>.() -> Unit)
    //    fun require(require: (CommandSourceStack) -> Boolean)
//    fun requires(requires: (CommandSourceStack) -> List<Boolean>)
    fun executes(execute: (CommandSender, CommandContext) -> Unit)
    operator fun String.invoke(node: LiteralBrigadierNode.() -> Unit) = then(this, node)
}

class RootBrigadierNode(val command: Command) : BrigadierNode {
    override fun then(literal: String, node: LiteralBrigadierNode.() -> Unit) {
        node(LiteralBrigadierNode(command, ArgumentType.Literal(literal)))
    }

    override fun <T> then(argument: Argument<T>, node: RequiredBrigadierNode<T>.() -> Unit) {
        node(RequiredBrigadierNode(command, argument.id, argument))
    }

    override fun executes(execute: (CommandSender, CommandContext) -> Unit) {
        command.setDefaultExecutor { sender, context ->
            execute(sender, context)
        }
    }
}

class LiteralBrigadierNode(val command: Command, var suggestion: Argument<String>) {
    fun executes(execute: (CommandSender, CommandContext) -> Unit) {
        command.addSyntax({ sender, context ->
            execute(sender, context)
        }, suggestion)
    }
}
class RequiredBrigadierNode<T>(val command: Command, val name: String, val argument: Argument<T>) {
    fun catch(callback: (CommandSender, ArgumentSyntaxException) -> Unit) {
        argument.setCallback { sender, exception ->
            callback(sender, exception)
        }
    }

    fun suggests(suggestion: List<String>) {
        suggests { _, _ ->
            suggestion
        }
    }
    fun suggests(suggestion: (CommandSender, CommandContext) -> List<String>) {
        argument.setSuggestionCallback { sender, context, suggestion ->
            val suggest = suggestion(sender, context)
            suggest.forEach {
                suggestion.addEntry(SuggestionEntry(it))
            }
        }
    }

    fun executes(execute: (CommandSender, CommandContext) -> Unit) {
        command.addSyntax({ sender, context ->
            execute(sender, context)
        }, argument)
    }
}



inline operator fun <reified T> CommandContext.getValue(thisRef: Any?, property: KProperty<*>): T {
    return this.get(property.name)
}