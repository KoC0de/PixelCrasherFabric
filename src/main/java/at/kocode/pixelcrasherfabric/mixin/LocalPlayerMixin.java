package at.kocode.pixelcrasherfabric.mixin;

import de.pixelcrasher.PixelCrasher;
import de.pixelcrasher.event.net.PlayerPositionSendEvent;
import de.pixelcrasher.event.timing.LocalPlayerTickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin {
    @Shadow
    private double xLast;
    @Shadow
    private double yLast1;
    @Shadow
    private double zLast;
    @Shadow
    private float yRotLast;
    @Shadow
    private float xRotLast;
    @Shadow
    private boolean lastOnGround;
    @Shadow
    private boolean wasShiftKeyDown;

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;hasChunkAt(II)Z", shift = At.Shift.AFTER), method = "tick")
    public void tick(CallbackInfo info) {
        PixelCrasher.getInstance().getPluginManager().call(new LocalPlayerTickEvent());
    }

    @ModifyVariable(method = "sendPosition", at = @At(value = "STORE"), name = "bl")
    private boolean sendPosition(boolean bl) {
        LocalPlayer player = Minecraft.getInstance().player;

        PlayerPositionSendEvent e = PixelCrasher.getInstance().getPluginManager().call(new PlayerPositionSendEvent(player.getX(), player.getY(), player.getZ(),
                player.getXRot(), player.getYRot(), this.xLast, this.yLast1, this.zLast, this.xRotLast, this.yRotLast,
                player.onGround(), player.isShiftKeyDown(), this.lastOnGround, this.wasShiftKeyDown));

        player.setPos(new Vec3(e.getX(), e.getY(), e.getZ()));
        player.setXRot(e.getXRot());
        player.setYRot(e.getYRot());
        xLast = e.getLastX();
        yLast1 = e.getLastY();
        zLast = e.getLastZ();
        xRotLast = e.getLastXRot();
        yRotLast = e.getLastYRot();

        player.setOnGround(e.onGround());
        wasShiftKeyDown = e.wasShiftKeyDown();
        lastOnGround = e.wasOnGround();

        return e.isShiftKeyDown();
    }

}
