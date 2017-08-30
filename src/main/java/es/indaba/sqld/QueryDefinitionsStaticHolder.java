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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.indaba.sqld.api.QueryDefinition;
import es.indaba.sqld.api.QueryDefinitionRepository;
import es.indaba.sqld.impl.QueryDefinitionStaticImpl;
import es.indaba.sqld.impl.loader.QueryDefinitionClassPathLoader;

public final class QueryDefinitionsStaticHolder {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueryDefinitionsStaticHolder.class);

    private static final QueryDefinitionRepository QUERY_REPOSITORY = new QueryDefinitionRepository();

    public static synchronized void loadQueryDefinitions(String prefix) {
        QueryDefinitionClassPathLoader.loadQueryDefinitionFiles(prefix, QUERY_REPOSITORY);
    }

    /**
     * Get the query string from the query store
     * 
     * @param queryName - The query key
     * @return - A String with the query
     */
    public static String getQueryAsString(final String queryName) {
        assert queryName != null;
        if (!QUERY_REPOSITORY.containsQuery(queryName)) {
            LOGGER.error("The query with key '{}' is not present.", queryName);
            throw new IllegalArgumentException("The query '" + queryName + "' is not present");
        }
        return QUERY_REPOSITORY.getQuery(queryName);
    }

    /**
     * Get the QueryDefinition object for the query
     * 
     * @param queryName - The query key
     * @return - A QueryDefinition object
     */
    public static QueryDefinition getQueryDefinition(final String queryName) {
        assert queryName != null;
        return new QueryDefinitionStaticImpl(queryName);
    }

    /**
     * Clears the query store
     */
    public static synchronized void clear() {
        QUERY_REPOSITORY.clear();
    }



    private QueryDefinitionsStaticHolder() {
        // Avoid instantiation of a Utility
    }
}
