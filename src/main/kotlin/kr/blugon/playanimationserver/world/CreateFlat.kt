package kr.blugon.playanimationserver.world

import net.minestom.server.instance.InstanceContainer
import net.minestom.server.instance.block.Block

fun InstanceContainer.createFlatWorld() {
    this.setGenerator {
        it.modifier().fillHeight(-64, -63, Block.BEDROCK)
        it.modifier().fillHeight(-63, -6, Block.STONE)
        it.modifier().fillHeight(-6, -1, Block.DIRT)
        it.modifier().fillHeight(-1, 0, Block.GRASS_BLOCK)
    }
//    this.setBlock(0, -1, 0, Block.SMOOTH_QUARTZ)
    for(x in -300..300) {
        for(y in 0..320) {
            this.setBlock(x, y, 1, Block.GRAY_CONCRETE)
        }
    }
}