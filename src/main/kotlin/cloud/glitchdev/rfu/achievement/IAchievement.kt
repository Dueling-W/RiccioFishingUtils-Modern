package cloud.glitchdev.rfu.achievement

interface IAchievement {
    val id: String
    val name: String
    val description: String
    val type: AchievementType
    val difficulty: AchievementDifficulty
    val isCompleted: Boolean
    val progress: Float // 0.0 to 1.0
}
