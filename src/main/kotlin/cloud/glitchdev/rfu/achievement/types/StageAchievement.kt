package cloud.glitchdev.rfu.achievement.types

import cloud.glitchdev.rfu.achievement.BaseAchievement
import cloud.glitchdev.rfu.achievement.AchievementManager

abstract class StageAchievement : BaseAchievement() {
    abstract val targetStage: Int
    
    var currentStage: Int = 0
        protected set(value) {
            field = value
            _progress = if (targetStage > 0) value.toFloat() / targetStage.toFloat() else 1.0f
            
            if (field >= targetStage) {
                complete()
            } else {
                AchievementManager.saveAll()
            }
        }
        
    fun advanceStage() {
        if (isCompleted) return
        currentStage += 1
    }

    override fun loadState(progressData: Map<String, Any>) {
        super.loadState(progressData)
        val savedStage = (progressData["currentStage"] as? Number)?.toInt() ?: 0
        currentStage = savedStage
    }

    override fun saveState(): Map<String, Any> {
        val baseState = super.saveState().toMutableMap()
        baseState["currentStage"] = currentStage
        return baseState
    }
}
