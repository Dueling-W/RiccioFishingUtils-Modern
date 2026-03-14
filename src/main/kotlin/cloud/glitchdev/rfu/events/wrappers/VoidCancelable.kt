package cloud.glitchdev.rfu.events.wrappers

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

class VoidCancelable(
    private val ci : CallbackInfo
) {
    fun cancel() {
        ci.cancel()
    }

    fun isCancelled() : Boolean {
        return ci.isCancelled
    }
}
