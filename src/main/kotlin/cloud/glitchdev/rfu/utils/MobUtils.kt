package cloud.glitchdev.rfu.utils

import cloud.glitchdev.rfu.config.categories.LavaFishing
import cloud.glitchdev.rfu.data.mob.MobManager
import net.minecraft.world.entity.Entity

object MobUtils {
    fun isPlhlegblast(entity: Entity): Boolean {
        if (!LavaFishing.plhlegblastGlow) return false
        val sbEntity = MobManager.getSkyblockEntity(entity.id) ?: return false
        val name = sbEntity.getName() ?: return false
        return name.contains("Plhlegblast", ignoreCase = true)
    }
}
