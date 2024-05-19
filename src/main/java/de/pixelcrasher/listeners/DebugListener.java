package de.pixelcrasher.listeners;

import com.mojang.blaze3d.platform.InputConstants;
import de.pixelcrasher.PixelCrasher;
import de.pixelcrasher.event.EventHandler;
import de.pixelcrasher.event.Listener;
import de.pixelcrasher.event.gui.GuiScreenChangeEvent;
import de.pixelcrasher.event.gui.GuiScreenInitEvent;
import de.pixelcrasher.event.gui.GuiScreenRebuildEvent;
import de.pixelcrasher.event.input.CharacterTypeEvent;
import de.pixelcrasher.event.input.KeyEvent;
import de.pixelcrasher.event.net.PlayerPositionSendEvent;
import de.pixelcrasher.event.render.Render3DEvent;
import de.pixelcrasher.event.render.RenderIngameGUIEvent;
import de.pixelcrasher.event.timing.MinecraftTickEvent;
import de.pixelcrasher.event.world.CustomSpawnerTickEvent;
import net.minecraft.client.Minecraft;

public class DebugListener implements Listener {

    private final Minecraft minecraft = Minecraft.getInstance();

    @EventHandler
    public void onTick(MinecraftTickEvent e) {

    }

    @EventHandler
    public void onRender3D(Render3DEvent e) {

    }

    @EventHandler
    public void onRenderGui(RenderIngameGUIEvent e) {

    }

    @EventHandler
    public void onCharTyped(CharacterTypeEvent e) {

    }

    @EventHandler
    public void onKey(KeyEvent e) {
        if(e.getKey() == InputConstants.KEY_F9 && e.getEventType() == KeyEvent.KEY_PRESS) {
            PixelCrasher.getInstance().reload();
        }
    }

    @EventHandler
    public void onGuiChange(GuiScreenChangeEvent e) {

    }

    @EventHandler
    public void onInitGui(GuiScreenInitEvent e) {

    }

    @EventHandler
    public void onGuiRebuild(GuiScreenRebuildEvent e) {

    }

    @EventHandler
    public void onPlayerPositionSend(PlayerPositionSendEvent e) {

    }

    @EventHandler
    public void onCustomSpawn(CustomSpawnerTickEvent e) {

    }

}
