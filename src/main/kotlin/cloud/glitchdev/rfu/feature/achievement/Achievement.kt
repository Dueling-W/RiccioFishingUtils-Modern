package cloud.glitchdev.rfu.feature.achievement

import cloud.glitchdev.rfu.gui.window.AchievementWindow
import cloud.glitchdev.rfu.utils.command.Command
import cloud.glitchdev.rfu.utils.command.SimpleCommand
import cloud.glitchdev.rfu.utils.gui.Gui
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource

@Command
object Achievement : SimpleCommand("rfuachivements") {
    override val description: String = "Opens the achievements window"

    override fun execute(context: CommandContext<FabricClientCommandSource>): Int {
        Gui.openGui(AchievementWindow)
        return 1
    }
}