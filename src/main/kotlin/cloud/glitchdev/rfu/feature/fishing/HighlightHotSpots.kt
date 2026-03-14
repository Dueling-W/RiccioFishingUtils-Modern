package cloud.glitchdev.rfu.feature.fishing

import cloud.glitchdev.rfu.RiccioFishingUtils.mc
import cloud.glitchdev.rfu.config.categories.HotSpotSettings
import cloud.glitchdev.rfu.data.fishing.Hotspot
import cloud.glitchdev.rfu.events.managers.ParticleEvents.registerParticleEvent
import cloud.glitchdev.rfu.events.managers.RenderEvents.registerRenderEvent
import cloud.glitchdev.rfu.events.managers.TickEvents.registerTickEvent
import cloud.glitchdev.rfu.feature.Feature
import cloud.glitchdev.rfu.feature.RFUFeature
import cloud.glitchdev.rfu.utils.rendering.Render3D
import gg.essential.universal.utils.toUnformattedString
import net.minecraft.world.entity.decoration.ArmorStand
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.phys.Vec3
import java.awt.Color
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.abs

@RFUFeature
object HighlightHotSpots : Feature {
    private val hotspots = ConcurrentHashMap<Vec3, Hotspot>()

    override fun onInitialize() {
        registerTickEvent(interval = 20) { client ->
            if (!HotSpotSettings.highlightHotSpots) {
                hotspots.clear()
                return@registerTickEvent
            }

            val world = client.level ?: return@registerTickEvent

            val entities = world.entitiesForRendering()
            val newHotspotPos = mutableSetOf<Vec3>()

            entities.forEach { entity ->
                if (entity is ArmorStand && entity.hasCustomName()) {
                    val name = entity.customName?.toUnformattedString() ?: ""
                    if (name.contains("HOTSPOT", ignoreCase = true)) {
                        val pos = entity.position()
                        newHotspotPos.add(pos)
                        
                        if (!hotspots.containsKey(pos)) {
                            val buff = findBuffNearby(pos, world)
                            val color = getColorForBuff(buff)
                            val isLava = checkIsLava(pos, world)
                            hotspots[pos] = Hotspot(pos, buff, 0f, color, isLava)
                        }
                    }
                }
            }

            hotspots.keys.removeIf { !newHotspotPos.contains(it) }
        }

        registerParticleEvent { packet, cancelable ->
            if (!HotSpotSettings.highlightHotSpots) return@registerParticleEvent

            val pos = Vec3(packet.x, packet.y, packet.z)

            val closestHotspot = hotspots.values.minByOrNull { it.center.distanceTo(pos) } ?: return@registerParticleEvent
            
            val distanceToCenter = pos.distanceTo(closestHotspot.center)
            val horizontalDistance = Vec3(pos.x, 0.0, pos.z).distanceTo(Vec3(closestHotspot.center.x, 0.0, closestHotspot.center.z))

            if (distanceToCenter < 10.0 && abs(pos.y - closestHotspot.center.y) < 3.0) {
                closestHotspot.addParticleDistance(horizontalDistance)

                cancelable.cancel()
            }
        }

        registerRenderEvent { context ->
            if (!HotSpotSettings.highlightHotSpots) return@registerRenderEvent

            val world = mc.level ?: return@registerRenderEvent

            for (hotspot in hotspots.values) {
                val radius = if (hotspot.radius > 0) hotspot.radius else 3.5f

                val surfaceY = findSurfaceY(hotspot.center, world, hotspot.lava)
                
                val renderPos = Vec3(hotspot.center.x, surfaceY + 0.05, hotspot.center.z)

                Render3D.renderDisk(
                    renderPos,
                    radius,
                    -1.0f,
                    hotspot.color,
                    context,
                    borderColor = hotspot.color.darker(),
                    lineWidth = 3.0f
                )
            }
        }
    }

    private fun findBuffNearby(pos: Vec3, world: net.minecraft.client.multiplayer.ClientLevel): String {
        val searchBox = net.minecraft.world.phys.AABB(pos.x - 0.1, pos.y - 2.0, pos.z - 0.1, pos.x + 0.1, pos.y + 2.0, pos.z + 0.1)
        val entities = world.getEntitiesOfClass(ArmorStand::class.java, searchBox).toList()
        
        for (entity in entities) {
            val name = entity.customName?.toUnformattedString() ?: ""
            if (name.contains("Chance", ignoreCase = true) || name.contains("Speed", ignoreCase = true)) {
                return name
            }
        }
        return ""
    }

    private fun getColorForBuff(buff: String): Color {
        return when {
            buff.contains("Treasure", ignoreCase = true) -> Color(255, 170, 0, 80) // Orange
            buff.contains("Speed", ignoreCase = true) -> Color(85, 255, 85, 80) // Green
            buff.contains("Sea Creature", ignoreCase = true) -> Color(255, 85, 255, 80) // Pink
            buff.contains("Double Hook", ignoreCase = true) -> Color(85, 255, 255, 80) // Cyan
            else -> Color(255, 255, 255, 80) // White default
        }
    }

    private fun checkIsLava(pos: Vec3, world: net.minecraft.client.multiplayer.ClientLevel): Boolean {
        for (y in -3..1) {
            val blockPos = net.minecraft.core.BlockPos.containing(pos.x, pos.y + y, pos.z)
            val state = world.getBlockState(blockPos)
            if (state.`is`(Blocks.LAVA)) return true
        }
        return false
    }

    private fun findSurfaceY(pos: Vec3, world: net.minecraft.client.multiplayer.ClientLevel, isLava: Boolean): Double {
        val blockType = if (isLava) Blocks.LAVA else Blocks.WATER
        var bestY = pos.y
        
        for (y in -5..2) {
            val blockPos = net.minecraft.core.BlockPos.containing(pos.x, pos.y + y, pos.z)
            val state = world.getBlockState(blockPos)
            if (state.`is`(blockType)) {
                val above = world.getBlockState(blockPos.above())
                if (!above.`is`(blockType)) {
                    return blockPos.y + 1.0
                }
                bestY = blockPos.y + 1.0
            }
        }
        return bestY
    }
}
