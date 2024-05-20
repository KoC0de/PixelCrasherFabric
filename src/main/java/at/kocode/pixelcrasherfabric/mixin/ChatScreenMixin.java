package at.kocode.pixelcrasherfabric.mixin;

import de.pixelcrasher.PixelCrasher;
import net.minecraft.client.gui.screens.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin {
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/ChatScreen;normalizeChatMessage(Ljava/lang/String;)Ljava/lang/String;", shift = At.Shift.AFTER), method = "handleChatInput")
    public void handleChatInput(String string, boolean bl, CallbackInfoReturnable<Boolean> cir) {
        if(string.isEmpty()) return;

        if(string.startsWith("#")) {
            PixelCrasher.getInstance().getCommandMap().dispatch(PixelCrasher.getInstance().getClientPlayer(), string.substring(1));
        }
    }

}
