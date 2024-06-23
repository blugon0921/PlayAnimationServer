package kr.blugon.playanimationserver.commands.play

import kr.blugon.minestom_brigadier.BrigadierCommand
import net.minestom.server.entity.Player
import net.minestom.server.entity.metadata.display.TextDisplayMeta
import net.minestom.server.instance.InstanceContainer

fun BrigadierCommand.stopAnimationCommand(instanceContainer: InstanceContainer) {
    register("stopanimation") {
        executes { sender, context->
            if(kr.blugon.playanimationserver.play.playTask == null) {
                sender.sendMessage("재생중인 애니메이션이 없습니다")
            } else {
                kr.blugon.playanimationserver.play.playTask!!.cancel()
                kr.blugon.playanimationserver.play.playTask = null
                sender.sendMessage("애니메이션 재생을 중지했습니다")
                sender.stopSound(net.kyori.adventure.sound.SoundStop.source(net.kyori.adventure.sound.Sound.Source.MASTER))
            }
            (sender as Player).gameMode = net.minestom.server.entity.GameMode.CREATIVE
            instanceContainer.entities.forEach { entity->
                if(entity.entityMeta is TextDisplayMeta) entity.remove()
            }
        }
    }
}