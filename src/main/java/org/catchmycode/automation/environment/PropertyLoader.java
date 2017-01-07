package org.catchmycode.automation.environment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

import static org.testng.Assert.fail;

/**
 * Property loader class for loading test framework dynamic properties based on environment.
 */
public class PropertyLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropertyLoader.class);
    private static final String ENV_LABEL = "env";
    private static final String FILE_NAME = "automation.properties";
    private static PropertyLoader loader = null;

    private Properties properties = null;

    /**
     * Private constructor
     */
    private PropertyLoader() {
        try {
            String env = System.getProperty(ENV_LABEL);
            properties = new Properties();

            if (env == null) {
                InputStream propertyStream = PropertyLoader.class.getClassLoader().getResourceAsStream(FILE_NAME);
                properties.load(propertyStream);
            } else {
                LOGGER.info("Environment is " + env);
                InputStream propertyStream = PropertyLoader.class.getClassLoader().getResourceAsStream(env + "." + FILE_NAME);
                if (propertyStream == null) {
                    propertyStream = this.getClass().getClassLoader().getResourceAsStream(env + "." + FILE_NAME);
                }
                properties.load(propertyStream);
            }
        } catch (Exception e) {
            LOGGER.error("Error initializing Property Loader for environment.", e);
            fail();
        }
    }

    /**
     * Gets the instance of this class
     *
     * @return the instance
     */
    public static PropertyLoader getInstance() {
        if (loader == null) {
            loader = new PropertyLoader();
        }
        return loader;
    }

    /**
     * Gets a property value from the environment-specific configuration as supplied by the user or
     * the overridden version as supplied in the environment variable
     *
     * @param propertyName the name of the property
     * @return the value of the property
     */
    public String getProperty(String propertyName) {

        if (propertyName == null) {
            fail("Missing property name");
        }

        if (System.getProperty(propertyName) != null) {
            LOGGER.info("Overriding stored value '" + propertyName + "'with the following: " + System.getProperty(propertyName));
            return System.getProperty(propertyName);
        }

        if (properties.getProperty(propertyName) == null) {
            fail("Property " + propertyName + " is missing from the configuration");
        }

        return properties.getProperty(propertyName);
    }

    /**
     * Gets a property value from the environment-specific configuration as supplied by the user or
     * the overridden version as supplied in the environment variable
     *
     * @param propertyName the name of the property
     * @return the value of the property
     */
    public Integer getPropertyAsInteger(String propertyName) {
        String value = getProperty(propertyName);

        if (value != null && !value.isEmpty()) {
            try {
                return Integer.parseInt(value);
            } catch (Exception ex) {
                fail("Cannot convert " + propertyName + " to an integer value");
                return null;
            }
        }

        return null;
    }


    /**
     * Get the environment parameter (if any)
     *
     * @return the environment name
     */
    public String getEnvironment() {

        if (System.getProperty(ENV_LABEL) != null) {
            return System.getProperty(ENV_LABEL);
        }
        return null;
    }

}
