package cloud.glitchdev.rfu.data.achievements

import cloud.glitchdev.rfu.utils.JsonFile

object AchievementHandler {
    private val jsonFile = JsonFile(
        filename = "achievements.json",
        type = AchievementsData::class.java,
        defaultFactory = { AchievementsData() }
    )
    
    fun getAchievementData(id: String): AchievementsData.AchievementSaveData? {
        return jsonFile.data.achievements[id]
    }
    
    fun saveAll(data: Map<String, AchievementsData.AchievementSaveData>) {
        jsonFile.data.achievements.putAll(data)
        jsonFile.save()
    }
}
