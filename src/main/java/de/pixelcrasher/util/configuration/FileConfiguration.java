package de.pixelcrasher.util.configuration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public final class FileConfiguration
{

    private static final char SEPARATOR = '.';
    final Map<String, Object> self;
    private FileConfiguration defaults;

    private final File file;
    
    public FileConfiguration()
    {
        this( null, null );
    }

    public FileConfiguration(FileConfiguration defaults, File file)
    {
        this( new LinkedHashMap<String, Object>(), defaults, file );

    }
    
    public FileConfiguration(File file)
    {
        this( null, file );
    }
    
    FileConfiguration(Map<?, ?> map, FileConfiguration defaults, File file)
    {
        this.self = new LinkedHashMap<>();
        this.defaults = defaults;
        
        this.file = file;

        for ( Map.Entry<?, ?> entry : map.entrySet() )
        {
            String key = ( entry.getKey() == null ) ? "null" : entry.getKey().toString();

            if ( entry.getValue() instanceof Map )
            {
                this.self.put( key, new FileConfiguration( (Map<?, ?>) entry.getValue(), ( defaults == null ) ? null : defaults.getSection( key ), file ) );
            } else
            {
                this.self.put( key, entry.getValue() );
            }
        }
    }

    private FileConfiguration getSectionFor(String path)
    {
        int index = path.indexOf( SEPARATOR );
        if ( index == -1 )
        {
            return this;
        }

        String root = path.substring( 0, index );
        Object section = self.get( root );
        if ( section == null || section instanceof String )
        {
            section = new FileConfiguration( ( defaults == null ) ? null : defaults.getSection( root ), null );
            self.put( root, section );
        }

        return (FileConfiguration) section;
    }

    private String getChild(String path)
    {
        int index = path.indexOf( SEPARATOR );
        return ( index == -1 ) ? path : path.substring( index + 1 );
    }

    /*------------------------------------------------------------------------*/
	public <T> T get(String path, T def)
    {
        FileConfiguration section = getSectionFor( path );
        Object val;
        if ( section == this )
        {
            val = self.get( path );
        } else
        {
            val = section.get( getChild( path ), def );
        }

        if ( val == null && def instanceof FileConfiguration )
        {
            self.put( path, def );
        }

        return ( val != null ) ? (T) val : def;
    }

    public boolean isSet(String path)
    {
        return get( path, null ) != null;
    }

    public Object get(String path)
    {
        return get( path, getDefault( path ) );
    }

    public Object getDefault(String path)
    {
        return ( defaults == null ) ? null : defaults.get( path );
    }

    public void set(String path, Object value)
    {
        if ( value instanceof Map )
        {
            value = new FileConfiguration( (Map<?, ?>) value, ( defaults == null ) ? null : defaults.getSection( path ), null );
        }

        FileConfiguration section = getSectionFor( path );
        if ( section == this )
        {
            if ( value == null )
            {
                self.remove( path );
            } else
            {
                self.put( path, value );
            }
        } else
        {
            section.set( getChild( path ), value );
        }
    }

    /*------------------------------------------------------------------------*/
    public FileConfiguration getSection(String path)
    {
        Object def = getDefault( path );
        return (FileConfiguration) get( path, ( def instanceof FileConfiguration ) ? def : new FileConfiguration( ( defaults == null ) ? null : defaults.getSection( path ), null ) );
    }

    /**
     * Gets keys, not deep by default.
     *
     * @return top level keys for this section
     */
    public Collection<String> getKeys()
    {
        return new LinkedHashSet<>( self.keySet() );
    }

    /*------------------------------------------------------------------------*/
    public byte getByte(String path)
    {
        Object def = getDefault( path );
        return getByte( path, ( def instanceof Number ) ? ( (Number) def ).byteValue() : 0 );
    }

    public byte getByte(String path, byte def)
    {
        Object val = get( path, def );
        return ( val instanceof Number ) ? ( (Number) val ).byteValue() : def;
    }

    public List<Byte> getByteList(String path)
    {
        List<?> list = getList( path );
        List<Byte> result = new ArrayList<>();

        for ( Object object : list )
        {
            if ( object instanceof Number )
            {
                result.add( ( (Number) object ).byteValue() );
            }
        }

        return result;
    }

    public short getShort(String path)
    {
        Object def = getDefault( path );
        return getShort( path, ( def instanceof Number ) ? ( (Number) def ).shortValue() : 0 );
    }

    public short getShort(String path, short def)
    {
        Object val = get( path, def );
        return ( val instanceof Number ) ? ( (Number) val ).shortValue() : def;
    }

    public List<Short> getShortList(String path)
    {
        List<?> list = getList( path );
        List<Short> result = new ArrayList<>();

        for ( Object object : list )
        {
            if ( object instanceof Number )
            {
                result.add( ( (Number) object ).shortValue() );
            }
        }

        return result;
    }

    public int getInt(String path)
    {
        Object def = getDefault( path );
        return getInt( path, ( def instanceof Number ) ? ( (Number) def ).intValue() : 0 );
    }

    public int getInt(String path, int def)
    {
        Object val = get( path, def );
        return ( val instanceof Number ) ? ( (Number) val ).intValue() : def;
    }

    public List<Integer> getIntList(String path)
    {
        List<?> list = getList( path );
        List<Integer> result = new ArrayList<>();

        for ( Object object : list )
        {
            if ( object instanceof Number )
            {
                result.add( ( (Number) object ).intValue() );
            }
        }

        return result;
    }

    public long getLong(String path)
    {
        Object def = getDefault( path );
        return getLong( path, ( def instanceof Number ) ? ( (Number) def ).longValue() : 0 );
    }

    public long getLong(String path, long def)
    {
        Object val = get( path, def );
        return ( val instanceof Number ) ? ( (Number) val ).longValue() : def;
    }

    public List<Long> getLongList(String path)
    {
        List<?> list = getList( path );
        List<Long> result = new ArrayList<>();

        for ( Object object : list )
        {
            if ( object instanceof Number )
            {
                result.add( ( (Number) object ).longValue() );
            }
        }

        return result;
    }

    public float getFloat(String path)
    {
        Object def = getDefault( path );
        return getFloat( path, ( def instanceof Number ) ? ( (Number) def ).floatValue() : 0 );
    }

    public float getFloat(String path, float def)
    {
        Object val = get( path, def );
        return ( val instanceof Number ) ? ( (Number) val ).floatValue() : def;
    }

    public List<Float> getFloatList(String path)
    {
        List<?> list = getList( path );
        List<Float> result = new ArrayList<>();

        for ( Object object : list )
        {
            if ( object instanceof Number )
            {
                result.add( ( (Number) object ).floatValue() );
            }
        }

        return result;
    }

    public double getDouble(String path)
    {
        Object def = getDefault( path );
        return getDouble( path, ( def instanceof Number ) ? ( (Number) def ).doubleValue() : 0 );
    }

    public double getDouble(String path, double def)
    {
        Object val = get( path, def );
        return ( val instanceof Number ) ? ( (Number) val ).doubleValue() : def;
    }

    public List<Double> getDoubleList(String path)
    {
        List<?> list = getList( path );
        List<Double> result = new ArrayList<>();

        for ( Object object : list )
        {
            if ( object instanceof Number )
            {
                result.add( ( (Number) object ).doubleValue() );
            }
        }

        return result;
    }

    public boolean getBoolean(String path)
    {
        Object def = getDefault( path );
        return getBoolean( path, ( def instanceof Boolean ) ? (Boolean) def : false );
    }

    public boolean getBoolean(String path, boolean def)
    {
        Object val = get( path, def );
        return ( val instanceof Boolean ) ? (Boolean) val : def;
    }

    public List<Boolean> getBooleanList(String path)
    {
        List<?> list = getList( path );
        List<Boolean> result = new ArrayList<>();

        for ( Object object : list )
        {
            if ( object instanceof Boolean )
            {
                result.add( (Boolean) object );
            }
        }

        return result;
    }

    public char getChar(String path)
    {
        Object def = getDefault( path );
        return getChar( path, ( def instanceof Character ) ? (Character) def : '\u0000' );
    }

    public char getChar(String path, char def)
    {
        Object val = get( path, def );
        return ( val instanceof Character ) ? (Character) val : def;
    }

    public List<Character> getCharList(String path)
    {
        List<?> list = getList( path );
        List<Character> result = new ArrayList<>();

        for ( Object object : list )
        {
            if ( object instanceof Character )
            {
                result.add( (Character) object );
            }
        }

        return result;
    }

    public String getString(String path)
    {
        Object def = getDefault( path );
        return getString( path, ( def instanceof String ) ? (String) def : null );
    }

    public String getString(String path, String def)
    {
        String val = get( path, def );
        return (val != null) ? val : def;
    }

    public List<String> getStringList(String path)
    {
        List<?> list = getList( path );
        List<String> result = new ArrayList<>();

        for ( Object object : list )
        {
            if ( object instanceof String )
            {
                result.add( (String) object );
            }
        }

        return result;
    }

    /*------------------------------------------------------------------------*/
    public List<?> getList(String path)
    {
        Object def = getDefault( path );
        return getList( path, ( def instanceof List<?> ) ? (List<?>) def : Collections.EMPTY_LIST );
    }

    public List<?> getList(String path, List<?> def)
    {
        Object val = get( path, def );
        return ( val instanceof List<?> ) ? (List<?>) val : def;
    }
    
    public void addDefault(String path, Object value) {
        if(this.defaults == null) this.defaults = YamlConfiguration.loadConfiguration("");
    	this.defaults.set(path, value);
    }
    public void setDefaults(FileConfiguration defaults) {
        this.defaults = defaults;
    }
    
    public void save() throws IOException {
    	if(this.file == null) throw new NullPointerException("File can not be null");
    	YamlConfiguration.provider.save(this, this.file);
    }
    
    public void save(File file) throws IOException {
        if(!file.getParentFile().exists()) file.getParentFile().mkdirs();
    	YamlConfiguration.provider.save(this, file);
    }

}
