package cloud.glitchdev.rfu.achievement

import cloud.glitchdev.rfu.data.achievements.AchievementHandler
import cloud.glitchdev.rfu.data.achievements.AchievementsData
import cloud.glitchdev.rfu.events.managers.ShutdownEvents

object AchievementManager {
    private val registry = HashMap<String, BaseAchievement>()
    private var isInitialized = false

    fun register(achievement: BaseAchievement) {
        registry[achievement.id] = achievement
        initLifecycle()
    }
    
    fun getRegistry(): Map<String, BaseAchievement> = registry

    fun isCompleted(id: String): Boolean {
        return AchievementHandler.getAchievementData(id)?.isCompleted == true
    }

    fun saveAll() {
        val allData = registry.mapValues { (_, achievement) ->
            AchievementsData.AchievementSaveData(
                id = achievement.id,
                isCompleted = achievement.isCompleted,
                progressData = achievement.saveState()
            )
        }
        AchievementHandler.saveAll(allData)
    }
    
    private fun initLifecycle() {
        if (isInitialized) return
        isInitialized = true
        ShutdownEvents.registerShutdownEvent {
            saveAll()
        }
    }
}
