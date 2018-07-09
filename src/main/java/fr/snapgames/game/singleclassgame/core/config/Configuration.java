/**
 * SnapGames
 * 
 * Game Development Java
 * 
 * singleclassgame
 * 
 * @year 2018
 */
package fr.snapgames.game.singleclassgame.core.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map.Entry;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.snapgames.game.singleclassgame.Game;

/**
 * This configuration object intends to manage persisting configuration
 * properties to a specific file.
 *
 * @author Frédéric Delorme
 */
public class Configuration {

	private static final Logger logger = LoggerFactory.getLogger(Configuration.class);

	private static final String UNKNOWN_CONFIG_KEY = "UNKNOWN_CONFIG_KEY";
	public static Configuration instance = new Configuration();
	Properties props;

	private Configuration() {
		props = new Properties();
		load();
	}

	/**
	 * Load configuration properties from file. By default, the configuration file
	 * is located in "/res/configuration.properties". All matching properties are
	 * loaded.
	 */
	private void load() {
		try {
			if (props == null) {
				props = new Properties();
			}
			if (new File(Game.class.getResource("/").getPath() + "configuration.properties").exists()) {
				props.load(Game.class.getResourceAsStream("/configuration.properties"));

			} else {
				props.load(Game.class.getResourceAsStream("/res/configuration.properties"));
			}
			for (Entry<Object, Object> prop : props.entrySet()) {
				logger.info(String.format("config %s : %s", prop.getKey(), prop.getValue()));
			}

		} catch (IOException e) {
			logger.error("Unable to read configuration file", e);
			System.err.println("Unable to read configuration file");
			System.exit(-1);
		}
	}

	/**
	 * request a configuration reload from file.
	 */
	public static void reload() {
		Configuration.instance.load();
	}

	/**
	 * Save configuration to configuration.properties file.
	 */
	private void store() {
		try {
			File f = new File(Game.class.getResource("/").getPath() + "/configuration.properties");
			OutputStream out = new FileOutputStream(f);
			props.store(out, "Update configuration");
		} catch (IOException e) {
			logger.error("Unable to store configuration file", e);
			System.err.println("Unable to store configuration file");
			System.exit(-1);
		}
	}

	/**
	 * request a configuration reload from file.
	 */
	public static void save() {
		Configuration.instance.store();
	}

	/**
	 * retrieve a value from configuraiton.properties file.
	 *
	 * @param key key configuration to be retrieved.
	 * @return String value
	 */
	private String getConfig(String key) {
		if (props.containsKey(key)) {
			return props.getProperty(key);
		} else {
			return UNKNOWN_CONFIG_KEY;
		}
	}

	/**
	 * retrieve a <code>int</code> value for configuration <code>key</code>.
	 *
	 * @param key
	 * @return
	 */
	public static int getInteger(String key, int defaultValue) {
		String value = Configuration.instance.getConfig(key);
		if (value.equals(UNKNOWN_CONFIG_KEY)) {
			return defaultValue;
		}
		return Integer.parseInt(value);
	}

	/**
	 * retrieve a <code>float</code> value for configuration <code>key</code>.
	 *
	 * @param key
	 * @return
	 */
	public static float getFloat(String key, float defaultValue) {
		String value = Configuration.instance.getConfig(key);
		if (value.equals(UNKNOWN_CONFIG_KEY)) {
			return defaultValue;
		}
		return Float.parseFloat(value);
	}

	/**
	 * retrieve a <code>boolean</code> value for configuration <code>key</code>.
	 *
	 * @param key
	 * @return
	 */
	public static boolean getBoolean(String key, boolean defaultValue) {
		String value = Configuration.instance.getConfig(key);
		if (value.equals(UNKNOWN_CONFIG_KEY)) {
			return defaultValue;
		}
		return Boolean.parseBoolean(value);
	}

	/**
	 * retrieve a <code>String</code> value for configuration <code>key</code>.
	 *
	 * @param key
	 * @return
	 */
	public static String get(String key, String defaultValue) {
		String value = Configuration.instance.getConfig(key);
		if (value.equals(UNKNOWN_CONFIG_KEY)) {
			return defaultValue;
		}
		return value;
	}

	private void setPropertyInt(String key, int value) {
		props.setProperty(key, "" + value);

	}

	private void setPropertyFloat(String key, float value) {
		props.setProperty(key, "" + value + "f");

	}

	private void setPropertyBoolean(String key, boolean value) {
		props.setProperty(key, "" + value);

	}

	private void setPropertyString(String key, String value) {
		props.setProperty(key, value);

	}

	public static void setInteger(String key, int value) {
		Configuration.instance.setPropertyInt(key, value);
	}

	public static void setFloat(String key, float value) {
		Configuration.instance.setPropertyFloat(key, value);
	}

	public static void setBoolean(String key, boolean value) {
		Configuration.instance.setPropertyBoolean(key, value);
	}

	public static void setString(String key, String value) {
		Configuration.instance.setPropertyString(key, value);
	}

	public static Configuration getInstance() {
		if(instance == null) {
			instance = new Configuration();
		}
		return instance;
	}

}
