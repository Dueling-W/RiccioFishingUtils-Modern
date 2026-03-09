package cloud.glitchdev.rfu.achievement

import cloud.glitchdev.rfu.achievement.types.StageAchievement
import cloud.glitchdev.rfu.utils.command.AbstractCommand
import cloud.glitchdev.rfu.utils.command.Command
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.network.chat.Component

@Achievement
object StandaloneTestAchievement : BaseAchievement() {
    override val id = "test_standalone"
    override val name = "Standalone Explorer"
    override val description = "A simple achievement for testing purposes."
    override val type = AchievementType.NORMAL
    override val difficulty = AchievementDifficulty.EASY

    override fun setupListeners() {
        // In a real scenario, you'd register an event listener here
        // For testing, we'll trigger it via command
    }

    fun trigger() {
        complete()
    }
}

@Achievement
object StageTestAchievement : StageAchievement() {
    override val id = "test_stage"
    override val name = "Stage Master"
    override val description = "Complete all 3 stages to earn this achievement."
    override val type = AchievementType.NORMAL
    override val difficulty = AchievementDifficulty.MEDIUM
    override val targetStage = 3

    override fun setupListeners() {
        // Listeners for stage progression
    }
    
    fun progress() {
        advanceStage()
    }
}

@Command
object AchievementTestCommand : AbstractCommand("rfutest") {
    override val description = "Commands for testing the achievement system"

    override fun build(builder: LiteralArgumentBuilder<FabricClientCommandSource>) {
        builder.then(
            lit("achievements")
                .then(
                    lit("list")
                        .executes { context ->
                            val achievements = AchievementProvider.getVisibleAchievements()
                            context.source.sendFeedback(Component.literal("§6--- Registered Achievements ---"))
                            achievements.forEach { ach ->
                                val status = if (ach.isCompleted) "§a[COMPLETED]" else "§e[PROGRESS: ${(ach.progress * 100).toInt()}%]"
                                context.source.sendFeedback(Component.literal("§7- §f${ach.name} §7(${ach.id}) $status §8| Difficulty: ${ach.difficulty}"))
                            }
                            1
                        }
                )
                .then(
                    lit("complete_standalone")
                        .executes { context ->
                            StandaloneTestAchievement.trigger()
                            context.source.sendFeedback(Component.literal("§aTriggered completion for Standalone achievement!"))
                            1
                        }
                )
                .then(
                    lit("progress_stage")
                        .executes { context ->
                            StageTestAchievement.progress()
                            context.source.sendFeedback(Component.literal("§aProgressed Stage achievement! Current stage: ${StageTestAchievement.currentStage}/${StageTestAchievement.targetStage}"))
                            1
                        }
                )
                .then(
                    lit("save")
                        .executes { context ->
                            AchievementManager.saveAll()
                            context.source.sendFeedback(Component.literal("§aAll achievements saved to disk!"))
                            1
                        }
                )
        )
    }
}
