package cloud.glitchdev.rfu.achievement

import cloud.glitchdev.rfu.events.managers.AchievementUnlockedEvents

object AchievementProvider {
    fun getVisibleAchievements(): List<IAchievement> {
        val all = AchievementManager.getRegistry().values
        return all.filter { 
            when (it.type) {
                AchievementType.NORMAL -> true
                AchievementType.SECRET -> true // Description masked by UI if not completed
                AchievementType.HIDDEN -> it.isCompleted
            }
        }
    }
    
    fun fireAchievementUnlocked(achievement: IAchievement) {
        AchievementUnlockedEvents.runTasks(achievement)
    }
}
