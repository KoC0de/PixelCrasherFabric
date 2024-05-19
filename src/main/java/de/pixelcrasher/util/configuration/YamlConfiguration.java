package de.pixelcrasher.util.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Map;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.representer.Represent;
import org.yaml.snakeyaml.representer.Representer;

public class YamlConfiguration extends ConfigurationProvider {

	public static final ConfigurationProvider provider = ConfigurationProvider.getProvider(YamlConfiguration.class);

	private static final ThreadLocal<Yaml> yaml = new ThreadLocal<Yaml>() {

		@Override
		protected Yaml initialValue() {
			DumperOptions options = new DumperOptions();
			Representer representer = new Representer(options) {
				{
					representers.put(FileConfiguration.class, new Represent() {
						@Override
						public Node representData(Object data) {
							return represent(((FileConfiguration) data).self);
						}
					});
				}
			};

			options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

			return new Yaml(new Constructor(new LoaderOptions()), representer, options);
		}
	};

	public static FileConfiguration loadConfiguration(File file) {
		
		try {
			if(!file.exists()) {
				return new FileConfiguration(new LinkedHashMap<>(), null, file);
			}

			return provider.load(file);
		} catch (IOException ignored) {}
		return null;
	}

	public static FileConfiguration loadConfiguration(Reader reader) {
		return provider.load(reader);
	}

	public static FileConfiguration loadConfiguration(InputStream inputStream) {
		return provider.load(inputStream);
	}
	public static FileConfiguration loadConfiguration(String config) {
		return provider.load(config);
	}

	@Override
	public void save(FileConfiguration config, File file) throws IOException {
		try (Writer writer = new OutputStreamWriter(new FileOutputStream(file))) {
			save(config, writer);
		}
	}

	@Override
	public void save(FileConfiguration config, Writer writer) {
		yaml.get().dump(config.self, writer);
	}

	@Override
	public FileConfiguration load(File file) throws IOException {
		return load(file, null);
	}

	@Override
	public FileConfiguration load(File file, FileConfiguration defaults) throws IOException {
		try (FileInputStream is = new FileInputStream(file)) {
			return load(file, is, defaults);
		}
	}

	@Override
	public FileConfiguration load(Reader reader) {
		return load(null, reader, null);
	}

	@Override
	public FileConfiguration load(File file, Reader reader, FileConfiguration defaults) {
		@SuppressWarnings("unchecked")
		Map<String, Object> map = yaml.get().loadAs(reader, LinkedHashMap.class);
		if (map == null) {
			map = new LinkedHashMap<>();
		}
		return new FileConfiguration(map, defaults, file);
	}

	@Override
	public FileConfiguration load(InputStream is) {
		return load(null, is, null);
	}

	@Override
	public FileConfiguration load(File file, InputStream is, FileConfiguration defaults) {
		@SuppressWarnings("unchecked")
		Map<String, Object> map = yaml.get().loadAs(is, LinkedHashMap.class);
		if (map == null) {
			map = new LinkedHashMap<>();
		}
		return new FileConfiguration(map, defaults, file);
	}

	@Override
	public FileConfiguration load(String string) {
		return load(string, null);
	}

	@Override
	public FileConfiguration load(String string, FileConfiguration defaults) {
		@SuppressWarnings("unchecked")
		Map<String, Object> map = yaml.get().loadAs(string, LinkedHashMap.class);
		if (map == null) {
			map = new LinkedHashMap<>();
		}
		return new FileConfiguration(map, defaults, null);
	}
}
