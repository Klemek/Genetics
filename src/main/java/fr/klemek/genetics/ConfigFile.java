package fr.klemek.genetics;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigFile {

    private final Properties properties = new Properties();
    private final String resourceName;

    public ConfigFile(String resourceName) {
        this.resourceName = resourceName;
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourceName)) {
            this.properties.load(is);
        } catch (IOException e) {
            throw new RuntimeException(resourceName + ": unable to read property file: " + e.getMessage());
        }
    }

    public String get(String key) {
        if (this.properties.containsKey(key)) {
            return this.properties.getProperty(key);
        } else {
            throw new RuntimeException(resourceName + ": key '" + key + "' not found");
        }
    }

    public int getInt(String key) {
        try {
            return Integer.parseInt(this.get(key));
        } catch (NumberFormatException e) {
            throw new RuntimeException(resourceName + ": invalid integer at key '" + key + "'");
        }
    }

    public short getShort(String key) {
        try {
            return Short.parseShort(this.get(key));
        } catch (NumberFormatException e) {
            throw new RuntimeException(resourceName + ": invalid short at key '" + key + "'");
        }
    }

    public float getFloat(String key) {
        try {
            return Float.parseFloat(this.get(key));
        } catch (NumberFormatException e) {
            throw new RuntimeException(resourceName + ": invalid float at key '" + key + "'");
        }
    }

    public boolean getBoolean(String key) {
        try {
            return Boolean.parseBoolean(this.get(key));
        } catch (NumberFormatException e) {
            throw new RuntimeException(resourceName + ": invalid boolean at key '" + key + "'");
        }
    }

    public Color getColor(String key) {
        String value = this.get(key);
        if (value.matches("^#[A-Fa-f0-9]{6}$")) {
            return Utils.colorFromHex(this.get(key));
        } else {
            throw new RuntimeException(resourceName + ": invalid hexadecimal color at key '" + key + "'");
        }
    }

}
