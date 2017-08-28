/*******************************************************************************
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version. This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details. You should have received a copy of the GNU Lesser General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>
 * 
 *******************************************************************************/
package es.indaba.sqld;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.indaba.sqld.parser.TextBlockReader;
import es.indaba.sqld.parser.YamlFileReader;

public final class QueryDefinitionsHolder {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueryDefinitionsHolder.class);

    private static final Properties QUERIES = new Properties();
    private static final Set<String> FILES = new HashSet<>();
    private static final Map<String, String> QUERIES_FILE = new HashMap<>();

    /**
     * Parses a sqld file extracting queries from file.
     * @param aInput - The stream for the file to be processed
     * @param aSqlFileName -The name of the resource to be processed
     * @throws IOException - An error is produced during the file read operation
     */
    public static synchronized void loadTextBlockFile(final InputStream aInput, final String aSqlFileName) throws IOException {
        if (FILES.contains(aSqlFileName)) {
            LOGGER.debug("The file '{}' is already loaded.", aSqlFileName);
            return;
        }
        final TextBlockReader sqlReader = new TextBlockReader(aInput, aSqlFileName);
        final Properties qFile = sqlReader.read();
        addProperties(qFile, QUERIES, aSqlFileName);
        FILES.add(aSqlFileName);
    }
    
    /**
     * Parses a yaml sqld file extracting queries from file.
     * @param aInput - The stream for the yaml file to be processed
     * @param aSqlFileName -The name of the resource to be processed
     * @throws IOException - An error is produced during the file read operation
     */
    public static synchronized void loadYamlFile(final InputStream aInput, final String aSqlFileName) throws IOException {
        if (FILES.contains(aSqlFileName)) {
            LOGGER.debug("The file '{}' is already loaded.", aSqlFileName);
            return;
        }
        final YamlFileReader sqlReader = new YamlFileReader(aInput, aSqlFileName);
        final Properties qFile = sqlReader.read();
        addProperties(qFile, QUERIES, aSqlFileName);
        FILES.add(aSqlFileName);
    }

    private static void addProperties(final Properties aProperties, final Properties aResult,
            final String aSqlFileName) {
        final Enumeration<?> keys = aProperties.propertyNames();
        while (keys.hasMoreElements()) {
            final String key = (String) keys.nextElement();
            final String value = aProperties.getProperty(key);
            if (aResult.containsKey(key)) {
                String duplicateKeyFile = QUERIES_FILE.get(key);
                LOGGER.error("The query '{}' is duplicated. The key is present in files {} and {} ", key, aSqlFileName,
                        duplicateKeyFile);
                throw new IllegalArgumentException(
                        "The query '" + key + "' is duplicated in files " + aSqlFileName + " and " + duplicateKeyFile);
            }
            aResult.setProperty(key, value);
            QUERIES_FILE.put(key, aSqlFileName);
        }
    }

    /**
     * Get the query string from the query store
     * @param queryName - The query key
     * @return - A String with the query
     */
    public static String getQueryAsString(final String queryName) {
        assert queryName!=null;
        if (!QUERIES.containsKey(queryName.toLowerCase())) {
            throw new IllegalArgumentException("The query '" + queryName + "' is not present");
        }
        return QUERIES.getProperty(queryName.toLowerCase());
    }

    
    /**
     * Clears the query store
     */
    public static synchronized void clear() {
        QUERIES.clear();
        FILES.clear();
        QUERIES_FILE.clear();

    }

    private QueryDefinitionsHolder() {
        // Avoid instantiation of a Utility
    }
}
