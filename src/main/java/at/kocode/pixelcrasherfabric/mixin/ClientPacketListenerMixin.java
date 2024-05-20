package at.kocode.pixelcrasherfabric.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import de.pixelcrasher.PixelCrasher;
import de.pixelcrasher.event.chat.ChatMessageSendEvent;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.LastSeenMessagesTracker;
import net.minecraft.network.chat.MessageSignature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.time.Instant;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;send(Lnet/minecraft/network/protocol/Packet;)V"), method = "sendChat", cancellable = true)
    public void sendChat(String string, CallbackInfo info,
                         @Local Instant instant, @Local long i,
                         @Local LastSeenMessagesTracker.Update lastseenmessagestracker$update,
                         @Local MessageSignature messagesignature) {
        ChatMessageSendEvent event = PixelCrasher.getInstance().getPluginManager().call(new ChatMessageSendEvent(string, instant, i, messagesignature, lastseenmessagestracker$update));
        if(event.isCancelled()) {
            info.cancel();
        }
    }
}
