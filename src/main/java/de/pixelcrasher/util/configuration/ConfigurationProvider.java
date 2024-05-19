package de.pixelcrasher.util.configuration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public abstract class ConfigurationProvider {

	private static final Map<Class<? extends ConfigurationProvider>, ConfigurationProvider> providers = new HashMap<>();

	static {
		providers.put(YamlConfiguration.class, new YamlConfiguration());
	}

	public static ConfigurationProvider getProvider(Class<? extends ConfigurationProvider> provider) {
		return providers.get(provider);
	}

	/*------------------------------------------------------------------------*/
	public abstract void save(FileConfiguration config, File file) throws IOException;

	public abstract void save(FileConfiguration config, Writer writer);

	public abstract FileConfiguration load(File file) throws IOException;

	public abstract FileConfiguration load(File file, FileConfiguration defaults) throws IOException;

	public abstract FileConfiguration load(Reader reader);

	public abstract FileConfiguration load(File file, Reader reader, FileConfiguration defaults);

	public abstract FileConfiguration load(InputStream is);

	public abstract FileConfiguration load(String string);

	public abstract FileConfiguration load(String string, FileConfiguration defaults);

	public abstract FileConfiguration load(File file, InputStream string, FileConfiguration defaults);
}
