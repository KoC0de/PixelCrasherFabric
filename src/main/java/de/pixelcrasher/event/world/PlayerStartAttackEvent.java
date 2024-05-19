package de.pixelcrasher.event.world;

import de.pixelcrasher.event.Cancellable;
import de.pixelcrasher.event.Event;
import de.pixelcrasher.event.HandlerList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.HitResult;

public class PlayerStartAttackEvent extends Event implements Cancellable {

    private final static HandlerList handlers = new HandlerList();

    private boolean cancelled;

    private final Player player;
    private final HitResult hitResult;
    private int missTime;

    public PlayerStartAttackEvent(HitResult result, Player player, int missTime) {
        this.player = player;
        this.hitResult = result;
        this.missTime = missTime;
    }

    public Player getPlayer() {
        return player;
    }

    public HitResult getHitResult() {
        return hitResult;
    }

    public int getMissTime() {
        return missTime;
    }

    public void setMissTime(int missTime) {
        this.missTime = missTime;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
