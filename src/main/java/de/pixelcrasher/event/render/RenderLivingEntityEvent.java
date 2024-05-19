package de.pixelcrasher.event.render;

import com.mojang.blaze3d.vertex.PoseStack;
import de.pixelcrasher.event.Cancellable;
import de.pixelcrasher.event.Event;
import de.pixelcrasher.event.HandlerList;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;

public class RenderLivingEntityEvent extends Event implements Cancellable {

    private final static HandlerList handlers = new HandlerList();

    private boolean cancelled = false;

    private final LivingEntity entity;
    private final float renderYaw, partialTicks;
    private final PoseStack poseStack;
    private final MultiBufferSource bufferSource;

    private float attackAnim;

    private float xRotation, yHeadRotation, yBodyRotation;
    private float prevXRotation, prevYHeadRotation, prevYBodyRotation;

    private boolean passenger, baby;

    public RenderLivingEntityEvent(LivingEntity entity, float renderYaw, float partialTicks, float attackAnim, PoseStack stack, MultiBufferSource source) {
        this.entity = entity;
        this.renderYaw = renderYaw;
        this.partialTicks = partialTicks;
        this.poseStack = stack;
        this.bufferSource = source;

        this.attackAnim = attackAnim;

        this.xRotation = entity.getXRot();
        this.prevXRotation = entity.xRotO;

        this.yBodyRotation = entity.yBodyRot;
        this.prevYBodyRotation = entity.yBodyRotO;

        this.yHeadRotation = entity.yHeadRot;
        this.prevYHeadRotation = entity.yHeadRotO;

        this.passenger = entity.isPassenger();
        this.baby = entity.isBaby();
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public float getRenderYaw() {
        return renderYaw;
    }

    public float getPartialTicks() {
        return partialTicks;
    }

    public PoseStack getPoseStack() {
        return poseStack;
    }

    public MultiBufferSource getBufferSource() {
        return bufferSource;
    }

    public float getXRotation() {
        return xRotation;
    }

    public float getPrevXRotation() {
        return prevXRotation;
    }

    public float getYBodyRotation() {
        return yBodyRotation;
    }

    public float getPrevYBodyRotation() {
        return prevYBodyRotation;
    }

    public float getYHeadRotation() {
        return yHeadRotation;
    }

    public float getPrevYHeadRotation() {
        return prevYHeadRotation;
    }

    public boolean isBaby() {
        return baby;
    }

    public boolean isPassenger() {
        return passenger;
    }

    public float getAttackAnim() {
        return attackAnim;
    }

    public void setAttackAnim(float attackAnim) {
        this.attackAnim = attackAnim;
    }

    public void setBaby(boolean baby) {
        this.baby = baby;
    }

    public void setPassenger(boolean passenger) {
        this.passenger = passenger;
    }

    public void setPrevXRotation(float prevXRotation) {
        this.prevXRotation = prevXRotation;
    }

    public void setPrevYBodyRotation(float prevYBodyRotation) {
        this.prevYBodyRotation = prevYBodyRotation;
    }

    public void setPrevYHeadRotation(float prevYHeadRotation) {
        this.prevYHeadRotation = prevYHeadRotation;
    }

    public void setXRotation(float xRotation) {
        this.xRotation = xRotation;
    }

    public void setYBodyRotation(float yBodyRotation) {
        this.yBodyRotation = yBodyRotation;
    }

    public void setYHeadRotation(float yHeadRotation) {
        this.yHeadRotation = yHeadRotation;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
