package de.pixelcrasher.event.world;

import de.pixelcrasher.event.Event;
import de.pixelcrasher.event.HandlerList;
import net.minecraft.server.level.ServerLevel;

public class CustomSpawnerTickEvent extends Event {

    private final static HandlerList handlers = new HandlerList();

    private final ServerLevel level;
    private final boolean spawnEnemies;
    private final boolean spawnFriendly;

    public CustomSpawnerTickEvent(ServerLevel level, boolean spawnEnemies, boolean spawnFriendly) {
        this.level = level;
        this.spawnEnemies = spawnEnemies;
        this.spawnFriendly = spawnFriendly;
    }

    public ServerLevel getLevel() {
        return level;
    }

    public boolean isSpawnEnemies() {
        return spawnEnemies;
    }

    public boolean isSpawnFriendly() {
        return spawnFriendly;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
