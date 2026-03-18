package cloud.glitchdev.rfu.mixin;

import cloud.glitchdev.rfu.access.PlhlegblastStateAccess;
import net.minecraft.client.renderer.entity.state.SquidRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(SquidRenderState.class)
public class SquidRenderStateMixin implements PlhlegblastStateAccess {
    @Unique
    private boolean rfu$isPlhlegblast;

    @Override
    public boolean rfu$isPlhlegblast() {
        return this.rfu$isPlhlegblast;
    }

    @Override
    public void rfu$setPlhlegblast(boolean isPlhlegblast) {
        this.rfu$isPlhlegblast = isPlhlegblast;
    }
}