package cloud.glitchdev.rfu.events.managers

import cloud.glitchdev.rfu.events.AbstractEventManager
import cloud.glitchdev.rfu.events.AutoRegister
import cloud.glitchdev.rfu.events.RegisteredEvent
import cloud.glitchdev.rfu.model.party.FishingParty

@AutoRegister
object PartyFinderEvents : AbstractEventManager<(FishingParty) -> Unit, PartyFinderEvents.PartyCreatedEvent>(), RegisteredEvent {
    override fun register() {}

    override val runTasks: (FishingParty) -> Unit = { party ->
        safeExecution {
            tasks.forEach { task ->
                task.callback(party)
            }
        }
    }

    fun registerPartyCreatedEvent(priority: Int = 20, callback: (FishingParty) -> Unit): PartyCreatedEvent {
        return PartyCreatedEvent(priority, callback).register()
    }

    class PartyCreatedEvent(
        priority: Int = 20,
        callback: (FishingParty) -> Unit
    ) : ManagedTask<(FishingParty) -> Unit, PartyCreatedEvent>(priority, callback) {
        override fun register() = submitTask(this)
        override fun unregister() = removeTask(this)
    }
}
