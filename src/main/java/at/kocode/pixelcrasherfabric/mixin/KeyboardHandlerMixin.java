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
    public void keyPress(long l, int i, int j, int k, int m, CallbackInfo ci) {
        if (l == Minecraft.getInstance().getWindow().getWindow()) {
            PixelCrasher.getInstance().getPluginManager().call(new KeyEvent(l, i, j, k, m));
        }
    }

}
