package es.indaba.sqld.impl.parser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;

public class YamlFileReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(YamlFileReader.class);

    private final LineNumberReader fReader;

    private final String fConfigFileName;

    /**
     * @param aInput has an underlying <tt>TextBlock</tt> file as source
     * @param aConfigFileName the underlying source file name
     */
    public YamlFileReader(final InputStream aInput, final String aConfigFileName) {
        fReader = new LineNumberReader(new InputStreamReader(aInput));
        fConfigFileName = aConfigFileName;
    }

    @SuppressWarnings("unchecked")
    public Properties read() {
        final Properties properties = new Properties();
        final LoaderOptions options = new LoaderOptions();
        options.setAllowDuplicateKeys(false);
        final Yaml yaml = new Yaml(options);
        try {
            final Map<String, String> result = (Map<String, String>) yaml.load(fReader);
            for (final String key : result.keySet()) {
                if (properties.containsKey(key.toLowerCase())) {
                    LOGGER.error("DUPLICATE Value found - 'Duplicate key {}' in {}", key, fConfigFileName);
                    throw new IllegalArgumentException("DUPLICATE Value found for this key '" + key + "'");
                }
                properties.setProperty(key.toLowerCase(), result.get(key));
            }
        } catch (IllegalStateException e) {
            String message = e.getMessage();
            if (message.startsWith("duplicate")) {
                LOGGER.error("DUPLICATE Value found - '{}' in {}", message, fConfigFileName);
                throw new IllegalArgumentException(e.getMessage(), e);
            } else {
                throw e;
            }
        }

        return properties;
    }

}
