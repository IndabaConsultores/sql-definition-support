package es.indaba.sqld;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryDefinitionRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueryDefinitionsStaticHolder.class);

    private Properties queries = new Properties();
    private Set<String> files = new HashSet<>();
    private Map<String, String> queries_file = new HashMap<>();

    public void fileLoaded(final String file) {
        files.add(file);
    }

    public boolean isFileProcessed(final String file) {
        return files.contains(file);
    }

    public void addQuery(final Properties aProperties, final String aSqlFileName) {
        final Enumeration<?> keys = aProperties.propertyNames();
        while (keys.hasMoreElements()) {
            final String key = (String) keys.nextElement();
            final String value = aProperties.getProperty(key);
            if (queries.containsKey(key)) {
                String duplicateKeyFile = queries_file.get(key);
                LOGGER.error("The query '{}' is duplicated. The key is present in files {} and {} ", key, aSqlFileName,
                        duplicateKeyFile);
                throw new IllegalArgumentException(
                        "The query '" + key + "' is duplicated in files " + aSqlFileName + " and " + duplicateKeyFile);
            }
            queries.setProperty(key, value);
            queries_file.put(key, aSqlFileName);
        }
    }

    public boolean containsQuery(String key) {
        assert key != null;
        return queries.containsKey(key.toLowerCase());
    }

    public String getQuery(String key) {
        assert key != null;
        return queries.getProperty(key.toLowerCase());
    }

    public void clear() {
        queries.clear();
        files.clear();
        queries_file.clear();
    }

}
