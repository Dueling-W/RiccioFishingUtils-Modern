package cloud.glitchdev.rfu.achievement.interfaces

interface IStageAchievement : IAchievement {
    val currentStage: Int
    val targetStage: Int
}
