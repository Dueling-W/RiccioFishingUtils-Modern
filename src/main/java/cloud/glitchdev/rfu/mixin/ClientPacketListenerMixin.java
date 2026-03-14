package cloud.glitchdev.rfu.mixin;

import cloud.glitchdev.rfu.events.managers.ContainerEvents;
import cloud.glitchdev.rfu.events.managers.EntityRemovedEvents;
import cloud.glitchdev.rfu.events.managers.ParticleEvents;
import cloud.glitchdev.rfu.events.managers.SetSlotEvents;
import cloud.glitchdev.rfu.events.wrappers.VoidCancelable;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {
    @Inject(method = "handleContainerContent", at = @At("HEAD"))
    private void onContainerContentPacket(ClientboundContainerSetContentPacket packet, CallbackInfo ci) {
        ContainerEvents.INSTANCE.getRunTasks().invoke(packet.containerId(), packet.items());
    }

    @Inject(method = "handleRemoveEntities", at = @At("HEAD"))
    private void onEntitiesRemoved(ClientboundRemoveEntitiesPacket packet, CallbackInfo ci) {
        for (int entityId : packet.getEntityIds()) {
            EntityRemovedEvents.INSTANCE.getRunTasks().invoke(entityId);
        }
    }

    @Inject(method = "handleContainerSetSlot", at = @At("HEAD"))
    private void handleContainerSetSlot(ClientboundContainerSetSlotPacket packet, CallbackInfo ci) {
        SetSlotEvents.INSTANCE.getRunTasks().invoke(packet.getContainerId(), packet.getSlot(), packet.getItem());
    }

    @Inject(method = "handleParticleEvent", at = @At("HEAD"), cancellable = true)
    private void handleLevelParticles(ClientboundLevelParticlesPacket packet, CallbackInfo ci) {
        VoidCancelable cancelable = new VoidCancelable(ci);
        ParticleEvents.INSTANCE.getRunTasks().invoke(packet, cancelable);
    }
}
