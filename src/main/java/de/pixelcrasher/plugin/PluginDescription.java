package de.pixelcrasher.plugin;

import de.pixelcrasher.util.configuration.FileConfiguration;
import de.pixelcrasher.util.configuration.YamlConfiguration;

import java.io.InputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class PluginDescription {

    private static final Pattern VALID_NAME = Pattern.compile("^[A-Za-z0-9 _.-]+$");

    private String name;
    private String main;
    private String version;
    private String website;
    private String prefix;
    private String apiVersion;

    private List<String> depend;
    private List<String> softDepend;
    private List<String> loadBefore;
    private List<String> authors;
    private List<String> contributors;

    private Map<String, Map<String, Object>> commands;

    public PluginDescription(InputStream inputStream) throws InvalidDescriptionException {
        this.load(YamlConfiguration.loadConfiguration(inputStream));
    }

    public PluginDescription(Reader inputStream) throws InvalidDescriptionException {
        this.load(YamlConfiguration.loadConfiguration(inputStream));
    }

    public String getName() {
        return name;
    }

    public String getFullName() {
        return this.getName() + " v" + this.getVersion();
    }

    public String getMain() {
        return main;
    }

    public String getVersion() {
        return version;
    }

    public String getWebsite() {
        return website;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public List<String> getDepend() {
        return depend;
    }

    public List<String> getSoftDepend() {
        return softDepend;
    }

    public List<String> getLoadBefore() {
        return loadBefore;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public List<String> getContributors() {
        return contributors;
    }

    public Map<String, Map<String, Object>> getCommands() {
        return commands;
    }

    public void load(FileConfiguration configuration) throws InvalidDescriptionException {

        this.name = configuration.getString("name");
        if(this.name == null) throw new InvalidDescriptionException("The name is not defined!");
        if(!VALID_NAME.matcher(this.name).matches()) throw new InvalidDescriptionException("The name '" + this.name + "' contains invalid characters!");

        this.version = configuration.getString("version");
        if(this.version == null) throw new InvalidDescriptionException("The version is not defined!");

        this.main = configuration.getString("main");
        if(this.main == null) throw new InvalidDescriptionException("The main class is not defined!");
        if(this.main.startsWith("de.pixelcrasher")) throw new InvalidDescriptionException("Plugins may not use the de.pixelcrasher package name");

        FileConfiguration commands = configuration.getSection("commands");
        if(commands != null) {
            this.commands = new HashMap<>();
            for(String command : commands.getKeys()) {
                String usage = commands.getString(command + ".usage");
                String description = commands.getString(command + ".description");
                List<String> aliases = commands.getStringList(command + ".aliases");
                Map<String, Object> attributes = new HashMap<>();
                attributes.put("usage", usage);
                attributes.put("description", description);
                attributes.put("aliases", aliases);
                this.commands.put(command, attributes);
            }
        }

        this.depend = configuration.getStringList("depend");
        this.softDepend = configuration.getStringList("softDepend");
        this.loadBefore = configuration.getStringList("loadBefore");

        this.website = configuration.getString("website");
        this.authors = configuration.getStringList("authors");
        this.contributors = configuration.getStringList("contributors");

        String author = configuration.getString("author");
        if(author != null) this.authors.add(author);

        this.apiVersion = configuration.getString("apiVersion");

    }
}
