package de.pixelcrasher.event.render;

import com.mojang.blaze3d.font.GlyphProvider;
import de.pixelcrasher.event.Event;
import de.pixelcrasher.event.HandlerList;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FontManagerApplyEvent extends Event {

    private final static HandlerList handlers = new HandlerList();

    private final FontSet missingFontSet;
    private final List<GlyphProvider> providersToClose;
    private final Map<ResourceLocation, FontSet> fontSets;
    private final TextureManager textureManager;

    public FontManagerApplyEvent(FontSet missingFontSet, List<GlyphProvider> providersToClose, Map<ResourceLocation, FontSet> fontSets, TextureManager textureManager) {
        this.missingFontSet = missingFontSet;
        this.providersToClose = providersToClose;
        this.fontSets = fontSets;
        this.textureManager = textureManager;
    }

    public FontSet getMissingFontSet() {
        return missingFontSet;
    }

    public List<GlyphProvider> getProvidersToClose() {
        return providersToClose;
    }

    public Map<ResourceLocation, FontSet> getFontSets() {
        return fontSets;
    }

    public TextureManager getTextureManager() {
        return textureManager;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
