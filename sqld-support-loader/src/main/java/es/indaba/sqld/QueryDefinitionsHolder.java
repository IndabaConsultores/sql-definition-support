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
import java.util.Properties;

import es.indaba.sqld.parser.TextBlockReader;

public final class QueryDefinitionsHolder {

    private static final Properties QUERIES = new Properties();

    public static void loadTextBlockFile(final InputStream aInput, final String aSqlFileName) throws IOException {
        final TextBlockReader sqlReader = new TextBlockReader(aInput, aSqlFileName);
        final Properties qFile = sqlReader.read();
        addProperties(qFile, QUERIES);
    }

    private static void addProperties(final Properties aProperties, final Properties aResult) {
        final Enumeration<?> keys = aProperties.propertyNames();
        while (keys.hasMoreElements()) {
            final String key = (String) keys.nextElement();
            final String value = aProperties.getProperty(key);
            aResult.setProperty(key, value);
        }
    }

    public static String getQueryAsString(final String queryName) {
        return QUERIES.getProperty(queryName);
    }

    public static void clear() {
        QUERIES.clear();
    }

    private QueryDefinitionsHolder() {
        // Avoid instantiation of a Utility
    }
}
