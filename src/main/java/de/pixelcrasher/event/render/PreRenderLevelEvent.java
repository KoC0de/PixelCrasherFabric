package de.pixelcrasher.event.render;

import com.mojang.blaze3d.vertex.PoseStack;
import de.pixelcrasher.event.Event;
import de.pixelcrasher.event.HandlerList;
import net.minecraft.client.Camera;
import org.joml.Matrix4f;

public class PreRenderLevelEvent extends Event {

    private final static HandlerList handlers = new HandlerList();


    private final PoseStack poseStack;
    private final Matrix4f viewMatrix;
    private final Camera camera;
    private final float partialTicks;

    public PreRenderLevelEvent(PoseStack poseStack, Matrix4f viewMatrix, Camera camera, float partialTicks) {
        this.poseStack = poseStack;
        this.viewMatrix = viewMatrix;
        this.camera = camera;
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks() {
        return this.partialTicks;
    }

    public Camera getCamera() {
        return camera;
    }

    public PoseStack getPoseStack() {
        return poseStack;
    }

    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
