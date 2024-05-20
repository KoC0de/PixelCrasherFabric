package at.kocode.pixelcrasherfabric.mixin;

import at.kocode.pixelcrasherfabric.PixelCrasherMod;
import de.pixelcrasher.PixelCrasher;
import de.pixelcrasher.event.gui.GuiScreenChangeEvent;
import de.pixelcrasher.event.timing.MinecraftTickEvent;
import de.pixelcrasher.event.world.MinecraftWorldLoadedEvent;
import de.pixelcrasher.event.world.PlayerStartAttackEvent;
import de.pixelcrasher.plugin.internal.CorePluginLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.main.GameConfig;
import net.minecraft.client.player.LocalPlayer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
	@Shadow @Nullable public LocalPlayer player;

	@Inject(at = @At("HEAD"), method = "close")
	private void close(CallbackInfo info) {
		PixelCrasher.getInstance().onDisable();
	}

	@Inject(at = @At(value = "HEAD"), method = "setScreen", cancellable = true)
	private void setScreen(@Nullable Screen screen, CallbackInfo callbackInfo) {
		GuiScreenChangeEvent event = PixelCrasher.getInstance().getPluginManager().call(new GuiScreenChangeEvent(Minecraft.getInstance().screen, screen));

		if(event.isCancelled()) {
			callbackInfo.cancel();
		}
	}

	@Inject(at = @At(value = "HEAD"), method = "runTick")
	private void runTick(CallbackInfo info) {
		PixelCrasher.getInstance().getPluginManager().call(new MinecraftTickEvent());
	}

	@Inject(at = @At(value = "TAIL"), method = "setLevel")
	private void setLevel(CallbackInfo info) {
		PixelCrasher.getInstance().getPluginManager().call(new MinecraftWorldLoadedEvent());
	}

	@Inject(at = @At(value = "HEAD"), method = "startAttack", cancellable = true)
	private void startAttack(CallbackInfoReturnable<Boolean> info) {
		PlayerStartAttackEvent event = PixelCrasher.getInstance().getPluginManager().call(new PlayerStartAttackEvent(Minecraft.getInstance().hitResult, Minecraft.getInstance().player, Minecraft.getInstance().missTime));
		if(event.isCancelled()) {
			info.setReturnValue(Minecraft.getInstance().missTime <= 0);
			info.cancel();
		}
	}

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/repository/FolderRepositorySource;<init>(Ljava/nio/file/Path;Lnet/minecraft/server/packs/PackType;Lnet/minecraft/server/packs/repository/PackSource;)V"), method = "<init>")
	public void MinecraftCreateRepositories(GameConfig gameConfig, CallbackInfo info) {

		CorePluginLoader loader = new CorePluginLoader(PixelCrasherMod.LOGGER);
		loader.loadCore();

		PixelCrasher.getInstance().getPluginManager().enablePlugin(PixelCrasher.getInstance());

		PixelCrasherMod.externalAssetSource = gameConfig.location.getExternalAssetSource();
    }

}