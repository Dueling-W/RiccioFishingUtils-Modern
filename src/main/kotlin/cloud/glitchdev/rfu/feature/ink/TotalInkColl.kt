package cloud.glitchdev.rfu.feature.ink

import cloud.glitchdev.rfu.RiccioFishingUtils.mc
import cloud.glitchdev.rfu.config.categories.BackendSettings
import cloud.glitchdev.rfu.config.categories.GeneralFishing
import cloud.glitchdev.rfu.config.categories.InkFishing
import cloud.glitchdev.rfu.constants.text.TextColor
import cloud.glitchdev.rfu.constants.text.TextStyle
import cloud.glitchdev.rfu.events.AutoRegister
import cloud.glitchdev.rfu.events.RegisteredEvent
import cloud.glitchdev.rfu.events.managers.ChatEvents.registerGameEvent
import cloud.glitchdev.rfu.events.managers.ContainerEvents.registerContainerOpenEvent
import cloud.glitchdev.rfu.events.managers.SeaCreatureCatchEvents.registerSeaCreatureCatchEvent
import cloud.glitchdev.rfu.events.managers.TickEvents.registerTickEvent
import cloud.glitchdev.rfu.feature.Feature
import cloud.glitchdev.rfu.feature.RFUFeature
import cloud.glitchdev.rfu.feature.fishing.FishingSession
import cloud.glitchdev.rfu.model.dye.Dyes

//import cloud.glitchdev.rfu.feature.debug.Chat
import cloud.glitchdev.rfu.utils.Chat
import cloud.glitchdev.rfu.utils.TextUtils
import cloud.glitchdev.rfu.utils.command.Command
import cloud.glitchdev.rfu.utils.command.SimpleCommand
import cloud.glitchdev.rfu.utils.network.DyeHttp.createCurrentDyes
import com.mojang.brigadier.context.CommandContext
import gg.essential.universal.utils.toUnformattedString
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.HoverEvent
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.time.Clock.System.now
import kotlin.time.Duration.Companion.minutes

import net.minecraft.core.component.DataComponents

import kotlin.time.Duration


@AutoRegister
object TotalInkColl : RegisteredEvent {

    override fun register() {
        registerContainerOpenEvent { _, items ->

            // EMPTY FOR NOW
            // REPLACE WITH API STUFF LATER

//            if(mc.screen?.title?.string?.contains("Ink Sac Collection") == true ) {
//                for (item in items) {
//                    val name = item.hoverName.string
//                    val lore = item[DataComponents.LORE]?.lines?.map { it.toUnformattedString() } ?: emptyList()
//
//                    if (name.contains("Ink")) {
//                        Chat.sendMessage(Component.literal("[$name]${lore.joinToString(" | ")}"))
//                    }
//
//                }
//            }


        }
    }


}