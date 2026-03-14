package cloud.glitchdev.rfu.events.managers

import cloud.glitchdev.rfu.events.AbstractEventManager
import cloud.glitchdev.rfu.events.wrappers.VoidCancelable
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket

object ParticleEvents : AbstractEventManager<(packet: ClientboundLevelParticlesPacket, cancelable: VoidCancelable) -> Unit, ParticleEvents.ParticleEvent>() {
    override val runTasks: (ClientboundLevelParticlesPacket, VoidCancelable) -> Unit = { packet, cancelable ->
        safeExecution {
            tasks.forEach { task -> task.callback(packet, cancelable) }
        }
    }

    fun registerParticleEvent(priority: Int = 20, callback: (packet: ClientboundLevelParticlesPacket, cancelable: VoidCancelable) -> Unit): ParticleEvent {
        return ParticleEvent(priority, callback).register()
    }

    class ParticleEvent(
        priority: Int = 20,
        callback: (packet: ClientboundLevelParticlesPacket, cancelable: VoidCancelable) -> Unit
    ) : ManagedTask<(ClientboundLevelParticlesPacket, VoidCancelable) -> Unit, ParticleEvent>(priority, callback) {
        override fun register() = submitTask(this)
        override fun unregister() = removeTask(this)
    }
}
