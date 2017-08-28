package es.indaba.sqld.parser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Map;
import java.util.Properties;

import org.yaml.snakeyaml.Yaml;

public class YamlFileReader {

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
        final Yaml yaml = new Yaml();
        final Map<String, String> result = (Map<String, String>) yaml.load(fReader);
        for (final String key : result.keySet()) {
            properties.setProperty(key, result.get(key));
        }
        return properties;
    }

}
