package de.pixelcrasher.plugin.java;

import com.google.common.base.Preconditions;
import de.pixelcrasher.plugin.InvalidPluginException;
import de.pixelcrasher.plugin.PluginDescription;
import de.pixelcrasher.plugin.SimplePluginManager;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.logging.Level;

public class PluginClassLoader extends URLClassLoader {

    private final JavaPluginLoader loader;

    private final Map<String, Class<?>> classes = new ConcurrentHashMap<>();

    private final PluginDescription description;
    private final File dataFolder;
    private final File file;
    private final JarFile jarFile;
    private final Manifest manifest;
    private final URL url;
    private ClassLoader libraryLoader;

    protected PixelCrasherAddon plugin;

    private PixelCrasherAddon init;
    private IllegalStateException state;

    private final Set<String> seenIllegalAccess = Collections.newSetFromMap(new ConcurrentHashMap<>());

    static {
        ClassLoader.registerAsParallelCapable();
    }

    public PluginClassLoader(JavaPluginLoader loader, ClassLoader parent, PluginDescription description, File dataFolder, File file) throws IOException, InvalidPluginException {
        super(new URL[] { file.toURI().toURL() }, parent);
        this.loader = loader;
        this.description = description;
        this.dataFolder = dataFolder;
        this.file = file;
        this.jarFile = new JarFile(file);
        this.manifest = this.jarFile.getManifest();
        this.url = file.toURI().toURL();
        try {
            Class<?> jarClass;
            Class<? extends PixelCrasherAddon> pluginMain;
            try {
                jarClass = Class.forName(description.getMain(), true, this);
            } catch (ClassNotFoundException e) {
                throw new InvalidPluginException("Could not find class '" + description.getMain() + "'");
            }

            try {
                pluginMain = jarClass.asSubclass(PixelCrasherAddon.class);
            } catch (ClassCastException e) {
                throw new InvalidPluginException("The main class does not extend PixelCrasherAddon");
            }

            this.plugin = pluginMain.getConstructor().newInstance();
        } catch (IllegalAccessError | NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            throw new InvalidPluginException("Abnormal plugin structure. Please check your main class!", e);

        }

    }

    public URL getResource(String name) {
        return findResource(name);
    }

    public Enumeration<URL> getResources(String name) throws IOException {
        return findResources(name);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        return loadClass0(name, resolve, true, true);
    }

    protected Class<?> loadClass0(String name, boolean resolve, boolean checkGlobal, boolean checkLibraries) throws ClassNotFoundException {
        try {
            Class<?> result = super.loadClass(name, resolve);
            if(checkGlobal || result.getClassLoader() == this) return result;
        } catch (ClassNotFoundException ignored) {}
        if(checkLibraries && this.libraryLoader != null) {
            try {
                return this.libraryLoader.loadClass(name);
            } catch (ClassNotFoundException ignored) {}
            if(checkGlobal) {
                Class<?> result = this.loader.getClassByName(name, resolve, this.description);
                if(result != null) {
                    if(result.getClassLoader() instanceof PluginClassLoader) {
                        PluginDescription provider = ((PluginClassLoader)result.getClassLoader()).description;
                        if(provider != this.description && !this.seenIllegalAccess.contains(provider.getName()) && !((SimplePluginManager)this.loader.client.getPluginManager()).isDepending(this.description, provider)) {
                            this.seenIllegalAccess.add(provider.getName());
                            if(this.plugin != null) {
                                this.plugin.getLogger().warn("Loaded class {0} from {1} which is not a depend or softdepend of this plugin.", new Object[] { name, provider.getFullName() });
                            } else {
                                this.loader.client.getLogger().warn("[{0}] Loaded class {1} from {2} which is not a depend or softdepend of this plugin.", new Object[] { this.description.getName(), name, provider.getName() });
                            }
                        }
                    }
                }
                return result;
            }
        }
        throw new ClassNotFoundException(name);
    }

    public void close() throws IOException {
        try {
            super.close();
        } finally {
            this.jarFile.close();
        }
    }

    public File getSourceFile() {
        return this.file;
    }

    Collection<Class<?>> getClasses() {
        return this.classes.values();
    }

    synchronized void initialize(PixelCrasherAddon addon) {
        Preconditions.checkArgument(addon != null, "Initializing plugin cannot be null");
        Preconditions.checkArgument(addon.getClass().getClassLoader() == this, "Cannot initialize plugin outside of this class loader");
        if(this.plugin != null || this.init != null) throw new IllegalArgumentException("Plugin already initialized!", this.state);
        this.state = new IllegalStateException("Initial initialization");
        this.init = addon;
        addon.init(this.loader, this.loader.client, this.description, this.dataFolder, this.file, this);
    }
}
