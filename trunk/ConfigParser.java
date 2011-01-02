import java.io.*;
import java.util.*;

public class ConfigParser {

	//private final static String MODULE_NAME = "ConfigParser";
	private Map<String, String> m_configMap;
	
	private static final Tracer tracer = Tracer.getTracerForThisClass();

	/**
	 * A configuration wrapper. cannot accept '=' as a value.
	 * 
	 * @param configFileString
	 *            path of the config file
	 * @throws ConfigException
	 *             If there is an error parsing the configuration file
	 */
	public ConfigParser(String configFileString) throws ConfigException {
		// trace
		tracer.TraceToConsole("Config Parser started");

		// Set a new map (dictionary like object)
		m_configMap = new HashMap<String, String>();

		try {
			// Open the file that is the first command line parameter
			FileInputStream fstream = new FileInputStream(configFileString);

			// Get the object of DataInputStream
			DataInputStream inDIS = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(inDIS));
			String stringLine;

			tracer.TraceToConsole("Iterating configuration file");
			// Read File Line By Line
			while ((stringLine = br.readLine()) != null) {
				// Print the content on the console (trace)
				tracer.TraceToConsole(stringLine);

				String[] parts = stringLine.split("=");

				// if no key, throw
				if (parts[0].trim().length() == 0) {
					throw new ConfigException("Key cannot be blank");
				}

				// if no value for key
				if (parts.length == 1 || parts[1].trim().length() == 0) {
					System.out.println("No value was entered for key "
							+ parts[0]);
					m_configMap.put(parts[0].trim(), "");
				} else {
					m_configMap.put(parts[0].trim(), parts[1].trim());
				}
			}

			// Close the input stream
			inDIS.close();

		} 
		// we want to wrap all exceptions from this class as a config exception
		catch (ConfigException e) {
			throw e;
		} catch (Exception e) {
			throw new ConfigException("Generic error in configuration, see inner exception",  e);
		}

		// trace
		tracer.TraceToConsole("Config Parser finished");
	}

	/**
	 * Gets a config value
	 * 
	 * @param configKey
	 *            the key of that value
	 * @return the value, or null of the key does not exist
	 */
	public String GetValue(String configKey) {
		// trace
		tracer.TraceToConsole(String.format("Getting configuration value for key '%s'", configKey));
		
		return m_configMap.get(configKey);
	}
}
