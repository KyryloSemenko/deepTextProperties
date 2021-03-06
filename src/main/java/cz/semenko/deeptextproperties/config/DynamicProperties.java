package cz.semenko.deeptextproperties.config;

import java.io.File;
import java.io.FileWriter;

import javax.annotation.PostConstruct;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Infrastructure {@link Service} to load properties dynamically<br>
 * First properties file loaded from project folder config/dynamic.properties file.<br>
 * Second file loaded from user.home/deepTextProperties/dynamic.properties file and overloads first file properties.<br>
 * In case when application changes properties, it will save in the second file.
 * 
 * @author Kyrylo Semenko
 */
@Service
public class DynamicProperties {
	private static final Logger logger = LoggerFactory.getLogger(DynamicProperties.class);
	
	private PropertiesConfiguration configuration;
	
	/** Configuration directory outside of project. If exists, properties from this directory overloads project file properties. */
	public static final String EXTERNAL_DIRECTORY_PATH = System.getProperty("user.home") + File.separator + "deepTextProperties" + File.separator + "config";

	/** Dynamic properties file name */
	public static final String DYNAMIC_FILE_NAME = "dynamic.properties";

	/** File in project. Its properties are defaults. */
	public static final String PROJECT_FILE_PATH = "config" + File.separator + DYNAMIC_FILE_NAME;
	
	/** How often to reload properties */
	public static final int REFRESH_DELAY = 50000; // 5 sec
	
	@PostConstruct
	private void init() {
		
		try {
			logger.info("Loading properties file: " + PROJECT_FILE_PATH);
			configuration = new PropertiesConfiguration(PROJECT_FILE_PATH);
			
			String externalFilePath = EXTERNAL_DIRECTORY_PATH + File.separator + DYNAMIC_FILE_NAME;
			logger.info("Loading properties file: " + externalFilePath);
			File externalFile = new File(externalFilePath);
			if (!externalFile.exists()) {
				externalFile.getParentFile().mkdirs();
				externalFile.createNewFile();
				FileWriter fw = new FileWriter(externalFile);
				fw.write("# Properties from this file where dynamically loading by application in run time and can be changed by application. These properties overrides application local dynamic.properties file." + System.lineSeparator() + System.lineSeparator());
				fw.write("EXAMPLE_KEY = exampleValue");
				fw.flush();
				fw.close();
				logger.info("External dynamic properties file created " + externalFile.getCanonicalPath());
			}
			PropertiesConfiguration externalProperties = new PropertiesConfiguration(externalFile);
			
			configuration.copy(externalProperties);
			
			configuration = externalProperties;
			
			// Create new FileChangedReloadingStrategy to reload the properties file based on the given time interval
			FileChangedReloadingStrategy fileChangedReloadingStrategy = new FileChangedReloadingStrategy();
			fileChangedReloadingStrategy.setRefreshDelay(REFRESH_DELAY);
			configuration.setReloadingStrategy(fileChangedReloadingStrategy);

		} catch (Exception e) {
			logger.error("Initialisation of application properties failed. " + e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	public String getProperty(String key) {
		return (String) configuration.getProperty(key);
	}

	/** Set property without saving to disk */
	public void setProperty(String key, Object value) {
		configuration.setProperty(key, value);
	}

	/** Set property and save to disk */
	public void setAndSave(String key, Object value) {
		setProperty(key, value);
		save();
	}

	/** Save properties file */
	public void save() {
		try {
			configuration.save();
		} catch (ConfigurationException e) {
			logger.error("Configuration file not saved. FilePath: " + EXTERNAL_DIRECTORY_PATH, e);
		}
	}

}
