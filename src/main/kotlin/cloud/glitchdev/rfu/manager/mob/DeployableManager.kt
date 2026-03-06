package cloud.glitchdev.rfu.manager.mob

import cloud.glitchdev.rfu.events.managers.ConnectionEvents.registerJoinEvent
import cloud.glitchdev.rfu.events.AutoRegister
import cloud.glitchdev.rfu.events.RegisteredEvent
import cloud.glitchdev.rfu.events.managers.ChatEvents.registerGameEvent
import cloud.glitchdev.rfu.utils.dsl.toExactRegex
import gg.essential.universal.utils.toUnformattedString
import net.minecraft.core.component.DataComponents
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.decoration.ArmorStand
import net.minecraft.world.entity.projectile.FireworkRocketEntity
import net.minecraft.world.item.PlayerHeadItem

@AutoRegister
object DeployableManager : RegisteredEvent {
    private val seenFlares = HashSet<Int>()
    var activeFlareEndTime: Long? = null
        private set
    var activeFlareType: FlareType = FlareType.NONE
        private set
    var activeUmberellaTime : Long? = null

    enum class FlareType(val bonus: String, val texture: String) {
        SOS("+125%", "ewogICJ0aW1lc3RhbXAiIDogMTY2MjY4Mjc3NjUxNiwKICAicHJvZmlsZUlkIiA6ICI4YjgyM2E1YmU0Njk0YjhiOTE0NmE5MWRhMjk4ZTViNSIsCiAgInByb2ZpbGVOYW1lIiA6ICJTZXBoaXRpcyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9jMDA2MmNjOThlYmRhNzJhNmE0Yjg5NzgzYWRjZWYyODE1YjQ4M2EwMWQ3M2VhODdiM2RmNzYwNzJhODlkMTNiIiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0="),
        ALERT("+50%", "ewogICJ0aW1lc3RhbXAiIDogMTcxOTg1MDQzMTY4MywKICAicHJvZmlsZUlkIiA6ICJmODg2ZDI3YjhjNzU0NjAyODYyYTM1M2NlYmYwZTgwZiIsCiAgInByb2ZpbGVOYW1lIiA6ICJOb2JpbkdaIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzlkMmJmOTg2NDcyMGQ4N2ZkMDZiODRlZmE4MGI3OTVjNDhlZDUzOWIxNjUyM2MzYjFmMTk5MGI0MGMwMDNmNmIiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ=="),
        UNDEFINED("", ""),
        NONE("", "")
    }

    private val umberellaRegex = """Umberella (\d+)s""".toRegex()
    private val seenUmberellas = HashSet<Int>()

    override fun register() {
        registerJoinEvent {
            resetFlare()
        }

        registerGameEvent("Your flare disappeared because you were too far away!".toExactRegex()) { _, _, _ ->
            resetFlare()
        }
    }

    fun checkUmberella(entity : ArmorStand) : Long? {
        if(seenUmberellas.contains(entity.id)) return null
        if (!entity.hasCustomName()) return null
        val name = entity.name.toUnformattedString()
        val result = umberellaRegex.find(name) ?: return null
        val time = result.groupValues.getOrNull(1)?.toLongOrNull() ?: return null
        return time
    }

    fun checkFlare(entity: Entity): Boolean {
        if(entity !is ArmorStand) {
            if(entity is FireworkRocketEntity) {
                activeFlareType = FlareType.UNDEFINED
                activeFlareEndTime = System.currentTimeMillis() + 180_000 // 3 minutes
                return true
            }

            return false
        }

        val helmet = entity.getItemBySlot(EquipmentSlot.HEAD)

        if (helmet.item !is PlayerHeadItem) return false

        val component = helmet[DataComponents.PROFILE]

        if (component != null) {
            val textures = component.partialProfile().properties["textures"].map { it.value }
            val type = FlareType.entries.find { type -> textures.contains(type.texture) }
            if (type != null && type != FlareType.NONE) {
                if (!seenFlares.contains(entity.id)) {
                    seenFlares.add(entity.id)
                    activeFlareEndTime = System.currentTimeMillis() + 180_000 // 3 minutes
                    activeFlareType = type
                }
                return true
            }
        }
        return false
    }

    fun resetFlare() {
        seenFlares.clear()
        activeFlareEndTime = null
        activeFlareType = FlareType.NONE
    }

    fun resetUmberella() {
        seenUmberellas.clear()
        activeUmberellaTime = null
    }
}