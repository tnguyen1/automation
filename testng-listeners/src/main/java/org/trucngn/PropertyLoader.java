package org.trucngn;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Class that extracts properties from the prop file.
 * 
 * @author Truc Nguyen
 */
public final class PropertyLoader {

    /** Map of already loaded properties. */
    private static final Map<String, Properties> PROPERTIES_MAP = new HashMap<String, Properties>();

    /**
     * Private constructor for utility class.
     */
    private PropertyLoader() {
    }

    /**
     * Load a property value.
     * 
     * @param pBasename
     *            properties file basename (eg. test)
     * @param pName
     *            property key
     * @return the value of System property pName if set or the value of property pName from properties file or an emtpy String if no value has been found
     */
    private static String loadProperty(final String pBasename, final String pName) {

        String value = "";

        // lazy loading
        if (PROPERTIES_MAP.get(pBasename) == null) {
            final Properties props = new Properties();
            try {
                props.load(PropertyLoader.class.getResourceAsStream("/" + pBasename + ".properties"));
                PROPERTIES_MAP.put(pBasename, props);
            } catch (final IOException e) {
                e.printStackTrace();
                return value;
            }
        }

        // system property overrides property in configuration file
        if (pName != null) {
            value = System.getProperty(pName);
            if (value == null) {
                value = PROPERTIES_MAP.get(pBasename).getProperty(pName);
            }
        }

        return value;
    }

    /**
     * Load property from test properties.
     * 
     * @param pName
     * @return
     */
    public static String loadTestProperty(final String pName) {
        return loadProperty("test", pName);
    }

}
