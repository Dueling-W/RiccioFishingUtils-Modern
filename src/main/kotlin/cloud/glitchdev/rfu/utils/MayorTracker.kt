package cloud.glitchdev.rfu.utils

import cloud.glitchdev.rfu.constants.Mayors
import cloud.glitchdev.rfu.events.AutoRegister
import cloud.glitchdev.rfu.events.RegisteredEvent
import cloud.glitchdev.rfu.events.managers.ConnectionEvents.registerJoinEvent
import cloud.glitchdev.rfu.events.managers.TickEvents.registerTickEvent
import cloud.glitchdev.rfu.utils.network.Network
import cloud.glitchdev.rfu.utils.command.Command
import cloud.glitchdev.rfu.utils.command.AbstractCommand
import cloud.glitchdev.rfu.constants.text.TextColor
import cloud.glitchdev.rfu.constants.text.TextStyle
import com.google.gson.JsonParser
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource

@AutoRegister
object MayorTracker : RegisteredEvent {
    var currentMayor: Mayors = Mayors.UNKNOWN
        private set

    private var lastFetchedYear = -1L
    private var lastFetchedRealTime = 0L

    override fun register() {
        registerJoinEvent {
            checkAndFetch()
        }

        registerTickEvent(interval = 200L) {
            checkAndFetch()
        }
    }

    private fun checkAndFetch() {
        val year = World.getCurrentSkyBlockYear()
        val month = World.getCurrentSkyBlockMonth()
        val day = World.getCurrentSkyBlockDay()
        val hour = World.getCurrentSkyBlockHour()

        val isPastElection = month >= 3 && day >= 27 && hour >= 1

        if (lastFetchedYear < year && !isPastElection) {
            fetchMayor()
        }
    }

    @Command
    object MayorCommand : AbstractCommand("rfumayor") {
        override val description: String = "Check current SkyBlock mayor or refresh the data."

        override fun build(builder: LiteralArgumentBuilder<FabricClientCommandSource>) {
            builder
                .executes { context ->
                    val mayor = currentMayor.mayorName
                    context.source.sendFeedback(TextUtils.rfuLiteral("Current Mayor: ", TextStyle(TextColor.GOLD))
                        .append(TextUtils.rfuLiteral(mayor, TextStyle(TextColor.YELLOW))))
                    1
                }
                .then(literal("refresh").executes { context ->
                    fetchMayor()
                    context.source.sendFeedback(TextUtils.rfuLiteral("Refreshing mayor data...", TextStyle(TextColor.GRAY)))
                    1
                })
        }
    }

    private fun fetchMayor() {
        lastFetchedRealTime = System.currentTimeMillis()
        Network.getRequest("https://api.hypixel.net/v2/resources/skyblock/election") { response ->
            if (response.isSuccessful() && response.body != null) {
                try {
                    val json = JsonParser.parseString(response.body).asJsonObject
                    if (json.has("mayor")) {
                        val mayorJson = json.getAsJsonObject("mayor")
                        val name = mayorJson.get("name").asString
                        currentMayor = Mayors.fromName(name)
                        lastFetchedYear = World.getCurrentSkyBlockYear()
                        RFULogger.dev("Fetched current Mayor: ${currentMayor.mayorName}")
                    }
                } catch (e: Exception) {
                    RFULogger.error("Error parsing mayor API", e)
                }
            }
        }
    }
}
