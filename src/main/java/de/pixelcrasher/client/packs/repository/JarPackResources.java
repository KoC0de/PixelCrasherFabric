package de.pixelcrasher.client.packs.repository;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.IoSupplier;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class JarPackResources extends AbstractPackResources {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final Splitter SPLITTER = Splitter.on('/').omitEmptyStrings().limit(3);
    private final File file;
    @Nullable
    private JarFile jarFile;
    private boolean failedToLoad;

    public JarPackResources(String p_256076_, File p_255707_, boolean p_256556_) {
        super(p_256076_, p_256556_);
        this.file = p_255707_;
    }

    @Nullable
    private JarFile getOrCreateJarFile() {
        if (this.failedToLoad) {
            return null;
        } else {
            if (this.jarFile == null) {
                try {
                    this.jarFile = new JarFile(this.file);
                } catch (IOException ioexception) {
                    LOGGER.error("Failed to open pack {}", this.file, ioexception);
                    this.failedToLoad = true;
                    return null;
                }
            }

            return this.jarFile;
        }
    }

    private static String getPathFromLocation(PackType p_250585_, ResourceLocation p_251470_) {
        return String.format(Locale.ROOT, "%s/%s/%s", p_250585_.getDirectory(), p_251470_.getNamespace(), p_251470_.getPath());
    }

    @Nullable
    public IoSupplier<InputStream> getRootResource(String @NotNull ... p_248514_) {
        return this.getResource(String.join("/", p_248514_));
    }

    public IoSupplier<InputStream> getResource(@NotNull PackType p_249605_, @NotNull ResourceLocation p_252147_) {
        return this.getResource(getPathFromLocation(p_249605_, p_252147_));
    }

    @Nullable
    private IoSupplier<InputStream> getResource(String p_251795_) {
        JarFile jarFile = this.getOrCreateJarFile();
        if (jarFile == null) {
            return null;
        } else {
            ZipEntry zipentry = jarFile.getEntry(p_251795_);
            return zipentry == null ? null : IoSupplier.create(jarFile, zipentry);
        }
    }

    public @NotNull Set<String> getNamespaces(@NotNull PackType p_10238_) {
        JarFile JarFile = this.getOrCreateJarFile();
        if (JarFile == null) {
            return Set.of();
        } else {
            Enumeration<? extends ZipEntry> enumeration = JarFile.entries();
            Set<String> set = Sets.newHashSet();

            while(enumeration.hasMoreElements()) {
                ZipEntry zipentry = enumeration.nextElement();
                String s = zipentry.getName();
                if (s.startsWith(p_10238_.getDirectory() + "/")) {
                    List<String> list = Lists.newArrayList(SPLITTER.split(s));
                    if (list.size() > 1) {
                        String s1 = list.get(1);
                        if (s1.equals(s1.toLowerCase(Locale.ROOT))) {
                            set.add(s1);
                        } else {
                            LOGGER.warn("Ignored non-lowercase namespace: {} in {}", s1, this.file);
                        }
                    }
                }
            }

            return set;
        }
    }

    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }

    public void close() {
        if (this.jarFile != null) {
            IOUtils.closeQuietly((Closeable)this.jarFile);
            this.jarFile = null;
        }

    }

    public void listResources(@NotNull PackType packType, @NotNull String p_249598_, @NotNull String p_251613_, PackResources.ResourceOutput p_250655_) {
        JarFile jarFile = this.getOrCreateJarFile();
        if (jarFile != null) {
            Enumeration<? extends ZipEntry> enumeration = jarFile.entries();
            String s = packType.getDirectory() + "/" + p_249598_ + "/";
            String s1 = s + p_251613_ + "/";

            while(enumeration.hasMoreElements()) {
                ZipEntry zipentry = enumeration.nextElement();
                if (!zipentry.isDirectory()) {
                    String zipentryName = zipentry.getName();
                    if (zipentryName.startsWith(s1)) {
                        String s3 = zipentryName.substring(s.length());
                        ResourceLocation resourcelocation = ResourceLocation.tryBuild(p_249598_, s3);
                        if (resourcelocation != null) {
                            p_250655_.accept(resourcelocation, IoSupplier.create(jarFile, zipentry));
                        } else {
                            LOGGER.warn("Invalid path in datapack: {}:{}, ignoring", p_249598_, s3);
                        }
                    }
                }
            }

        }
    }
}