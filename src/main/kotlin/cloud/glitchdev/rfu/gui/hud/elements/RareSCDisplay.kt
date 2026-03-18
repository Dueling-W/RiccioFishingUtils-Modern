package cloud.glitchdev.rfu.gui.hud.elements

import cloud.glitchdev.rfu.config.categories.RareScSettings
import cloud.glitchdev.rfu.constants.RareScDisplayDataType
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
import kotlin.math.ceil
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
            CatchTracker.catchHistory.lastBait = null
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
        val lastBait = catchHistory.lastBait

        if (lastPos == Vec3.ZERO && !isFishing && !isEditing) {
            text.setText("")
            return
        }

        val dataOrder = RareScSettings.rareScDisplayDataOrder

        selectedScs.forEach { sc ->
            if (currentIsland != null && !sc.category.islands.contains(currentIsland)) {
                return@forEach
            }

            if (lastPos != Vec3.ZERO && !sc.condition(lastHotspot, lastPos, lastBait)) {
                return@forEach
            }

            val record = catchHistory.getOrAdd(sc)

            // Building the customized line
            val line = buildString {
                append("$CYAN${BOLD}${sc.scName}:")

                dataOrder.forEach { dataType ->
                    when (dataType) {
                        RareScDisplayDataType.STREAK -> {
                            append(" $YELLOW${record.count}")
                        }
                        RareScDisplayDataType.AVERAGE -> {
                            val avg = if (record.history.isNotEmpty()) ceil(record.history.average()).toInt().toString() else "0"
                            append(" $GRAY($YELLOW$avg$GRAY)")
                        }
                        RareScDisplayDataType.TOTAL -> {
                            append(" $CYAN[$YELLOW${record.total}$CYAN]")
                        }
                        RareScDisplayDataType.TIME_SINCE -> {
                            val lastTime = if (record.total > 0) {
                                (Clock.System.now() - record.time).toReadableString()
                            } else {
                                "Never"
                            }
                            append(" $WHITE$lastTime")
                        }
                    }
                }
            }
            lines.add(line)
        }

        text.setText(if (lines.isEmpty()) {
            if (isEditing) "rareSCDisplay" else ""
        } else lines.joinToString("\n"))
    }
}
