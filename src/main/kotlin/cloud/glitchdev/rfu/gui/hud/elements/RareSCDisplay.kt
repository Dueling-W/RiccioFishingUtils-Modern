package cloud.glitchdev.rfu.gui.hud.elements

import cloud.glitchdev.rfu.config.categories.RareScSettings
import cloud.glitchdev.rfu.constants.text.TextColor.CYAN
import cloud.glitchdev.rfu.constants.text.TextColor.YELLOW
import cloud.glitchdev.rfu.constants.text.TextColor.WHITE
import cloud.glitchdev.rfu.constants.text.TextColor.GRAY
import cloud.glitchdev.rfu.constants.text.TextEffects.BOLD
import cloud.glitchdev.rfu.data.catches.CatchTracker
import cloud.glitchdev.rfu.feature.fishing.FishingXpTracker
import cloud.glitchdev.rfu.feature.mob.SeaCreatureHour
import cloud.glitchdev.rfu.gui.hud.AbstractTextHudElement
import cloud.glitchdev.rfu.gui.hud.HudElement
import cloud.glitchdev.rfu.utils.World
import cloud.glitchdev.rfu.utils.dsl.toReadableString
import cloud.glitchdev.rfu.events.managers.HypixelModApiEvents.registerLocationEvent
import net.minecraft.world.phys.Vec3
import kotlin.time.Clock
import kotlin.time.Instant

@HudElement
object RareSCDisplay : AbstractTextHudElement("rareSCDisplay") {

    private val isFishing: Boolean
        get() = SeaCreatureHour.startFishing != Instant.DISTANT_PAST || FishingXpTracker.startFishing != Instant.DISTANT_PAST

    override val enabled: Boolean
        get() = RareScSettings.rareScDisplay && (super.enabled || !RareScSettings.rareScOnlyWhenFishing || isFishing)

    override fun onInitialize() {
        super.onInitialize()
        registerLocationEvent {
            CatchTracker.catchHistory.lastHotspot = null
            CatchTracker.catchHistory.lastPos = Vec3.ZERO
        }
    }

    override fun onUpdateState() {
        super.onUpdateState()

        val lines = mutableListOf<String>()
        val selectedScs = RareScSettings.rareSC
        val currentIsland = World.island

        val catchHistory = CatchTracker.catchHistory
        val lastHotspot = catchHistory.lastHotspot
        val lastPos = catchHistory.lastPos

        selectedScs.forEach { sc ->
            if (currentIsland != null && !sc.category.islands.contains(currentIsland)) {
                return@forEach
            }

            if (lastPos != Vec3.ZERO && !sc.condition(lastHotspot, lastPos)) {
                return@forEach
            }

            val record = catchHistory.getOrAdd(sc)

            val average = if (record.history.isNotEmpty()) "%.1f".format(record.history.average()) else "0.0"
            val lastTime = if (record.total > 0) {
                (Clock.System.now() - record.time).toReadableString()
            } else {
                "Never"
            }

            val line = buildString {
                append("$CYAN${BOLD}${sc.scName}:")
                append(" $YELLOW${record.count}")
                append(" $GRAY($YELLOW$average$GRAY)")
                append(" $CYAN[$YELLOW${record.total}$CYAN]")
                append(" $WHITE$lastTime")
            }
            lines.add(line)
        }

        text.setText(if (lines.isEmpty()) {
            if (isEditing) "rareSCDisplay" else ""
        } else lines.joinToString("\n"))
    }
}
