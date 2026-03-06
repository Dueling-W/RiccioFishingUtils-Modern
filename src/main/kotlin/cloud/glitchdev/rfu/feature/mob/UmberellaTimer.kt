package cloud.glitchdev.rfu.feature.mob

import cloud.glitchdev.rfu.config.categories.GeneralFishing
import cloud.glitchdev.rfu.events.managers.TickEvents.registerTickEvent
import cloud.glitchdev.rfu.feature.Feature
import cloud.glitchdev.rfu.feature.RFUFeature
import cloud.glitchdev.rfu.gui.hud.elements.UmberellaTimerDisplay
import cloud.glitchdev.rfu.manager.mob.DeployableManager
import cloud.glitchdev.rfu.utils.Title

@RFUFeature
object UmberellaTimer : Feature {
    var lastTime : Long? = null

    override fun onInitialize() {
        registerTickEvent(interval = 20) {
            val currentTime = DeployableManager.activeUmberellaTime
            if (currentTime != null) {
                updateTime(currentTime)
            } else {
                updateTime(null)
            }
        }
    }

    private fun updateTime(time : Long?) {
        UmberellaTimerDisplay.updateTime(time)

        if(
            GeneralFishing.umberellaAlert &&
            lastTime != null && time == null
        ) {
            Title.showTitle("§9§lUmberella Expired!")
        }

        lastTime = time
    }
}
