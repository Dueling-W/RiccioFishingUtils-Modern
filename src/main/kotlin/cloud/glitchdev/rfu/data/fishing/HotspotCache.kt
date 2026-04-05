package cloud.glitchdev.rfu.data.fishing

import cloud.glitchdev.rfu.constants.FishingIslands
import net.minecraft.core.BlockPos
import java.util.Collections
import java.util.LinkedHashMap

object HotspotCache {
    private const val MAX_LOCATIONS = 100
    private const val MAX_MEASUREMENTS_PER_LOCATION = 250

    data class HotspotData(
        var liquid: cloud.glitchdev.rfu.constants.LiquidTypes,
        var buff: String,
        var island: FishingIslands?,
        var lastMetadataUpdate: Long = System.currentTimeMillis(),
        val distances: MutableList<Double> = Collections.synchronizedList(mutableListOf())
    )

    private val cache = Collections.synchronizedMap(object : LinkedHashMap<BlockPos, HotspotData>(MAX_LOCATIONS, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<BlockPos, HotspotData>): Boolean {
            return size > MAX_LOCATIONS
        }
    })

    fun addMeasurement(pos: BlockPos, distance: Double, liquid: cloud.glitchdev.rfu.constants.LiquidTypes, buff: String, island: FishingIslands?) {
        val data = cache.getOrPut(pos) { HotspotData(liquid, buff, island) }
        data.liquid = liquid
        data.buff = buff
        data.island = island
        data.lastMetadataUpdate = System.currentTimeMillis()
        val distances = data.distances
        synchronized(distances) {
            distances.add(distance)
            if (distances.size > MAX_MEASUREMENTS_PER_LOCATION) {
                distances.removeAt(0)
            }
        }
    }

    fun getMedian(pos: BlockPos): Float? {
        val data = cache[pos] ?: return null
        val distances = data.distances
        synchronized(distances) {
            if (distances.isEmpty()) return null
            val sorted = distances.sorted()
            return sorted[sorted.size / 2].toFloat()
        }
    }

    fun getCachedEntries() = cache.entries.toList()
}
