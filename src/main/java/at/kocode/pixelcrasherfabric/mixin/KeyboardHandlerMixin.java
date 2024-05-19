package at.kocode.pixelcrasherfabric.mixin;

import de.pixelcrasher.PixelCrasher;
import de.pixelcrasher.event.input.KeyEvent;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {

    @Inject(at = @At("HEAD"), method = "keyPress")
    public void keyPress(long p_90894_, int p_90895_, int p_90896_, int p_90897_, int p_90898_, CallbackInfo ci) {
        if (p_90894_ == Minecraft.getInstance().getWindow().getWindow()) {
            PixelCrasher.getInstance().getPluginManager().call(new KeyEvent(p_90894_, p_90895_, p_90896_, p_90897_, p_90898_));
        }
    }

}
