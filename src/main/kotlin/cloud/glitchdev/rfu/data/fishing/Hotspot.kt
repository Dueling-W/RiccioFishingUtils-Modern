package cloud.glitchdev.rfu.data.fishing

import net.minecraft.world.phys.Vec3
import java.awt.Color

data class Hotspot(
    val center: Vec3,
    val buff: String,
    var radius: Float = 0f,
    val color: Color,
    val lava: Boolean,
    val startTime: Long = System.currentTimeMillis()
) {
    private val particleDistances = mutableListOf<Double>()

    fun addParticleDistance(distance: Double) {
        if (particleDistances.size < 100) {
            particleDistances.add(distance)
            radius = particleDistances.maxOrNull()?.toFloat() ?: 0f
        }
    }

    fun isRadiusCalculated() : Boolean = particleDistances.size >= 10
}
