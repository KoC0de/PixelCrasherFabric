package de.pixelcrasher.client.packs.repository;

import com.mojang.logging.LogUtils;
import de.pixelcrasher.PixelCrasher;
import de.pixelcrasher.plugin.Plugin;
import de.pixelcrasher.plugin.PluginManager;
import de.pixelcrasher.plugin.java.PluginClassLoader;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class PCPackRepository implements RepositorySource {

    private static final Logger LOGGER = LogUtils.getLogger();

    private final PixelCrasher pluginProvider;
    private final PackType packType;
    private final PackSource packSource;

    public PCPackRepository(PixelCrasher pluginProvider, PackType packType, PackSource packSource) {
        this.pluginProvider = pluginProvider;
        this.packType = packType;
        this.packSource = packSource;
    }

    @Override
    public void loadPacks(@NotNull Consumer<Pack> consumer) {
        PluginManager pluginManager = this.pluginProvider.getPluginManager();
        try {
            discoverPacks(pluginManager, true, (plugin, supplier) -> {
                Pack pack = Pack.readMetaAndCreate(plugin.getName(), Component.literal(plugin.getName()), true, supplier, this.packType, Pack.Position.TOP, this.packSource);
                if(pack != null) consumer.accept(pack);
            });
        } catch (IOException e) {
            LOGGER.warn("Failed to load addon resources", e);
        }

    }

    public static void discoverPacks(PluginManager pluginManager, boolean isBuiltIn, BiConsumer<Plugin, Pack.ResourcesSupplier> consumer) throws IOException {
        Arrays.stream(pluginManager.getPlugins()).forEach(plugin -> {
            Pack.ResourcesSupplier resourcesSupplier = detectPluginResources(plugin, isBuiltIn);
            if(resourcesSupplier != null) consumer.accept(plugin, resourcesSupplier);
        });
    }

    public static Pack.ResourcesSupplier detectPluginResources(Plugin plugin, boolean isBuiltIn) {
        final PluginClassLoader pluginClassLoader;
        if(plugin.getClass().getClassLoader() instanceof PluginClassLoader) {
            pluginClassLoader = (PluginClassLoader) plugin.getClass().getClassLoader();
        } else {
            pluginClassLoader = null;
        }

        if(pluginClassLoader != null && pluginClassLoader.getResource("pack.mcmeta") != null) {
            return (name) -> {
              return new JarPackResources(name, pluginClassLoader.getSourceFile(), isBuiltIn);
            };
        }
        return null;
    }

}
