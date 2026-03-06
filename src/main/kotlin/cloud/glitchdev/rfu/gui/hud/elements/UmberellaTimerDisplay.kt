package cloud.glitchdev.rfu.gui.hud.elements

import cloud.glitchdev.rfu.config.categories.GeneralFishing
import cloud.glitchdev.rfu.constants.text.TextColor.LIGHT_BLUE
import cloud.glitchdev.rfu.constants.text.TextColor.YELLOW
import cloud.glitchdev.rfu.constants.text.TextEffects.BOLD
import cloud.glitchdev.rfu.gui.hud.AbstractTextHudElement
import cloud.glitchdev.rfu.gui.hud.HudElement

@HudElement
object UmberellaTimerDisplay : AbstractTextHudElement("umberellaTimerDisplay") {
    var remainingTime: Long? = null

    override val enabled: Boolean
        get() = GeneralFishing.umberellaTimerDisplay && (super.enabled || remainingTime != null)

    override fun onUpdateState() {
        super.onUpdateState()

        val time = remainingTime
        val finalText = if (time != null) {
            "$LIGHT_BLUE${BOLD}Umberella: ${YELLOW}${remainingTime}s"
        } else {
            "$LIGHT_BLUE${BOLD}Umberella: ${YELLOW}0s"
        }

        text.setText(finalText)
    }

    fun updateTime(remaining: Long?) {
        this.remainingTime = remaining
        updateState()
    }
}