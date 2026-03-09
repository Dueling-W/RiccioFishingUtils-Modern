package cloud.glitchdev.rfu.events.managers

import cloud.glitchdev.rfu.achievement.interfaces.IAchievement
import cloud.glitchdev.rfu.events.AbstractEventManager

object AchievementUnlockedEvents : AbstractEventManager<(IAchievement) -> Unit, AchievementUnlockedEvents.AchievementUnlockedEvent>() {
    override val runTasks: (IAchievement) -> Unit = { achievement ->
        safeExecution {
            tasks.forEach { task ->
                task.callback(achievement)
            }
        }
    }

    fun registerAchievementUnlockedEvent(priority: Int = 20, callback: (IAchievement) -> Unit): AchievementUnlockedEvent {
        return AchievementUnlockedEvent(priority, callback).register()
    }

    class AchievementUnlockedEvent(
        priority: Int = 20,
        callback: (IAchievement) -> Unit
    ) : ManagedTask<(IAchievement) -> Unit, AchievementUnlockedEvent>(priority, callback) {
        override fun register() = submitTask(this)
        override fun unregister() = removeTask(this)
    }
}
