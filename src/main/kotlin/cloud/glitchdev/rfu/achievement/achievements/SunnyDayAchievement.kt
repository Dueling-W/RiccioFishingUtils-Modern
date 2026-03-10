package cloud.glitchdev.rfu.achievement.achievements

import cloud.glitchdev.rfu.achievement.Achievement
import cloud.glitchdev.rfu.achievement.AchievementCategory
import cloud.glitchdev.rfu.achievement.AchievementDifficulty
import cloud.glitchdev.rfu.achievement.AchievementType
import cloud.glitchdev.rfu.achievement.types.StageAchievement
import cloud.glitchdev.rfu.constants.SeaCreatures
import cloud.glitchdev.rfu.data.catches.CatchTracker.catchHistory
import cloud.glitchdev.rfu.events.managers.SeaCreatureCatchEvents.registerSeaCreatureCatchEvent

@Achievement
object SunnyDayAchievement : StageAchievement() {
    override val id: String = "sunny_day"
    override val name: String = "Sunny Day"
    override val description: String = "Don't catch a thunder for 100/150/200/250/300 catches."
    override val type: AchievementType = AchievementType.NORMAL
    override val difficulty: AchievementDifficulty = AchievementDifficulty.MEDIUM
    override val category: AchievementCategory = AchievementCategory.ISLE

    override val targetStage: Int = 5

    init {
        addStageInfo(1, "Rainy Day", "Don't catch a thunder for 100 catches.", AchievementDifficulty.EASY)
        addStageInfo(2, "Overcast Day", "Don't catch a thunder for 150 catches.", AchievementDifficulty.EASY)
        addStageInfo(3, "Cloudy Day", "Don't catch a thunder for 200 catches.", AchievementDifficulty.MEDIUM)
        addStageInfo(4, "Clear Sky Day", "Don't catch a thunder for 250 catches.", AchievementDifficulty.MEDIUM)
        addStageInfo(5, "Sunny Day", "Don't catch a thunder for 300 catches.", AchievementDifficulty.MEDIUM)
    }

    override var currentStage: Int = 1
        set(value) {
            field = value

            if (field > targetStage) {
                complete()
            }
        }

    override val currentProgress: Int
        get() = count
    override val targetProgress: Int = getCurrentGoal(currentStage)
    override var _progress: Float = 0.0f
        get() = currentProgress.toFloat() / targetProgress.toFloat()
    private var count = catchHistory.getOrAdd(SeaCreatures.THUNDER).count

    override fun setupListeners() {
        activeListeners.add(registerSeaCreatureCatchEvent { _, _ ->
            count = catchHistory.getOrAdd(SeaCreatures.THUNDER).count

            if(count > getCurrentGoal(currentStage)) {
                advanceStage()
            }
        })
    }

    fun getCurrentGoal(stage : Int) : Int {
        return when(stage) {
            1 -> 100
            2 -> 150
            3 -> 200
            4 -> 250
            5 -> 300
            else -> 300
        }
    }
}