package cloud.glitchdev.rfu.data.collections

import cloud.glitchdev.rfu.events.AutoRegister
import cloud.glitchdev.rfu.events.RegisteredEvent
import cloud.glitchdev.rfu.events.managers.CollectionEvents
import cloud.glitchdev.rfu.events.managers.ConnectionEvents.registerJoinEvent
import cloud.glitchdev.rfu.events.managers.ShutdownEvents.registerShutdownEvent
import cloud.glitchdev.rfu.utils.JsonFile

@AutoRegister
object CollectionsHandler : RegisteredEvent {
    private val jsonFile = JsonFile(
        filename = "collections.json",
        type = CollectionsData::class.java,
        defaultFactory = { CollectionsData() }
    )

    override fun register() {
        registerJoinEvent {
            jsonFile.save()
        }

        registerShutdownEvent {
            jsonFile.save()
        }
    }

    fun get(item: CollectionItem): Long {
        return jsonFile.data.collectionMap[item.name] ?: 0L
    }

    fun set(item: CollectionItem, value: Long, isSync: Boolean = true) {
        val oldTotal = get(item)
        val diff = value - oldTotal
        if (diff == 0L) return

        jsonFile.data.collectionMap[item.name] = value
        
        // Only trigger if something changed
        CollectionEvents.trigger(item, diff, value, isSync)
    }

    fun add(item: CollectionItem, amount: Long, isSync: Boolean = false) {
        if (amount == 0L) return
        val newTotal = get(item) + amount
        set(item, newTotal, isSync)
    }
}
