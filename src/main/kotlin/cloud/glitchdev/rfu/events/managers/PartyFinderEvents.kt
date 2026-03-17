package cloud.glitchdev.rfu.events.managers

import cloud.glitchdev.rfu.events.AbstractEventManager
import cloud.glitchdev.rfu.model.party.FishingParty

object PartyFinderEvents {
    object PartyCreated : AbstractEventManager<(FishingParty) -> Unit, PartyCreated.PartyCreatedEvent>() {
        override val runTasks: (FishingParty) -> Unit = { party ->
            safeExecution {
                tasks.forEach { task ->
                    task.callback(party)
                }
            }
        }

        fun register(priority: Int = 20, callback: (FishingParty) -> Unit): PartyCreatedEvent {
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

    object PartyJoined : AbstractEventManager<() -> Unit, PartyJoined.PartyJoinedEvent>() {
        override val runTasks: () -> Unit = {
            safeExecution {
                tasks.forEach { task ->
                    task.callback()
                }
            }
        }

        fun register(priority: Int = 20, callback: () -> Unit): PartyJoinedEvent {
            return PartyJoinedEvent(priority, callback).register()
        }

        class PartyJoinedEvent(
            priority: Int = 20,
            callback: () -> Unit
        ) : ManagedTask<() -> Unit, PartyJoinedEvent>(priority, callback) {
            override fun register() = submitTask(this)
            override fun unregister() = removeTask(this)
        }
    }

    fun registerPartyCreatedEvent(priority: Int = 20, callback: (FishingParty) -> Unit): PartyCreated.PartyCreatedEvent {
        return PartyCreated.register(priority, callback)
    }

    fun registerPartyJoinedEvent(priority: Int = 20, callback: () -> Unit): PartyJoined.PartyJoinedEvent {
        return PartyJoined.register(priority, callback)
    }

    fun runPartyCreatedTasks(party: FishingParty) {
        PartyCreated.runTasks(party)
    }

    fun runPartyJoinedTasks() {
        PartyJoined.runTasks()
    }
}
