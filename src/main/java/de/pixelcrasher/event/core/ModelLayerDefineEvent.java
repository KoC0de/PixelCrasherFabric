package de.pixelcrasher.event.core;

import com.google.common.collect.ImmutableMap;
import de.pixelcrasher.event.Event;
import de.pixelcrasher.event.HandlerList;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;

public class ModelLayerDefineEvent extends Event {

    private final static HandlerList handlers = new HandlerList();

    private final ImmutableMap.Builder<ModelLayerLocation, LayerDefinition> definitionBuilder;

    public ModelLayerDefineEvent(ImmutableMap.Builder<ModelLayerLocation, LayerDefinition> builder) {
        this.definitionBuilder = builder;
    }

    public ImmutableMap.Builder<ModelLayerLocation, LayerDefinition> getDefinitionBuilder() {
        return definitionBuilder;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
