package de.pixelcrasher.event.net;

import de.pixelcrasher.event.Event;
import de.pixelcrasher.event.HandlerList;

public class PlayerPositionSendEvent extends Event {

    private final static HandlerList handlers = new HandlerList();

    private double x, y, z;
    private float xRot, yRot;
    private final double xLast, yLast1, zLast;
    private final float xRotLast, yRotLast;
    private boolean onGround;
    private final boolean lastOnGround;
    private boolean shiftKeyDown;
    private final boolean wasShiftKeyDown;

    public PlayerPositionSendEvent(double x, double y, double z, float xRot, float yRot, double xLast, double yLast1, double zLast, float xRotLast, float yRotLast, boolean onGround, boolean shiftKeyDown, boolean lastOnGround, boolean wasShiftKeyDown) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.xRot = xRot;
        this.yRot = yRot;
        this.xLast = xLast;
        this.yLast1 = yLast1;
        this.zLast = zLast;
        this.xRotLast = xRotLast;
        this.yRotLast = yRotLast;
        this.onGround = onGround;
        this.shiftKeyDown = shiftKeyDown;
        this.lastOnGround = lastOnGround;
        this.wasShiftKeyDown = wasShiftKeyDown;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getXRot() {
        return xRot;
    }

    public float getYRot() {
        return yRot;
    }

    public double getLastX() {
        return xLast;
    }

    public double getLastY() {
        return yLast1;
    }

    public double getLastZ() {
        return zLast;
    }

    public float getLastXRot() {
        return xRotLast;
    }

    public float getLastYRot() {
        return yRotLast;
    }

    public boolean onGround() {
        return onGround;
    }

    public boolean isShiftKeyDown() {
        return shiftKeyDown;
    }

    public boolean wasOnGround() {
        return lastOnGround;
    }

    public boolean wasShiftKeyDown() {
        return wasShiftKeyDown;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public void setXRot(float xRot) {
        this.xRot = xRot;
    }

    public void setYRot(float yRot) {
        this.yRot = yRot;
    }

    public void setShiftKeyDown(boolean shiftKeyDown) {
        this.shiftKeyDown = shiftKeyDown;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
