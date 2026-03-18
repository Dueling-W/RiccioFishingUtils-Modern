package cloud.glitchdev.rfu.events.managers

import cloud.glitchdev.rfu.events.AbstractEventManager
import net.minecraft.world.entity.Entity

object EntityAddedEvents : AbstractEventManager<(entity : Entity) -> Unit, EntityAddedEvents.EntityAddedEvent>() {
    override val runTasks: (Entity) -> Unit = { entity ->
        safeExecution {
            tasks.forEach { task -> task.callback(entity) }
        }
    }

    fun registerEntityAddedEvent(
        priority: Int = 20,
        callback: (entity : Entity) -> Unit
    ): EntityAddedEvent {
        return EntityAddedEvent(priority, callback).register()
    }

    class EntityAddedEvent(
        priority: Int = 20,
        callback: (entity : Entity) -> Unit
    ) : ManagedTask<(entity : Entity) -> Unit, EntityAddedEvent>(priority, callback) {
        override fun register() = submitTask(this)
        override fun unregister() = removeTask(this)
    }
}
