package cz.semenko.deeptextproperties;

import java.io.File;
import java.io.FileWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import cz.semenko.deeptextproperties.config.DynamicProperties;

@SpringBootApplication
public class Application {
	private static final Logger logger = LoggerFactory.getLogger(Application.class);
	/** Standard Spring boot file name */
	public static final String STATIC_CONFIG_FILE_NAME = "application.properties";
    
    public static void main(String[] args) {
    	createExternalConfigFile();
    	// Tell to Spring boot about external config location
    	System.setProperty("spring.config.location", DynamicProperties.EXTERNAL_DIRECTORY_PATH + File.separator);
        ApplicationContext ctx = SpringApplication.run(Application.class, args);
    }

    /**
     * For first usage comfort this metod creates a new empty external config file if not exists.<br>
     * See file location defined in {@link DynamicProperties#EXTERNAL_DIRECTORY_PATH} 
     * and file name defined in {@link Application#STATIC_CONFIG_FILE_NAME}<br>
     * Properties from this file overrides properties from local config/application.properties
     */
	private static void createExternalConfigFile() {
		File externalStaticConfigFile = new File(DynamicProperties.EXTERNAL_DIRECTORY_PATH + File.separator + STATIC_CONFIG_FILE_NAME);
		try {
			// Create external static config file if not exists
			if (!externalStaticConfigFile.exists()) {
				externalStaticConfigFile.getParentFile().mkdirs();
				externalStaticConfigFile.createNewFile();
				FileWriter fw = new FileWriter(externalStaticConfigFile);
				fw.write("# Properties from this file where loading by application in start time. These properties overrides application local application.properties file." + System.lineSeparator() + System.lineSeparator());
				fw.write("EXAMPLE_KEY = exampleValue");
				fw.flush();
				fw.close();
				logger.info("External static properties file created " + externalStaticConfigFile.getCanonicalPath());
			} else {
				logger.info("External static properties file " + externalStaticConfigFile.getCanonicalPath());
			}
		} catch (Exception e) {
			logger.warn("Creation of external configuration file failed. File: " + externalStaticConfigFile.getPath() + " Message: " + e.getMessage(), e);
		}
	}

}
