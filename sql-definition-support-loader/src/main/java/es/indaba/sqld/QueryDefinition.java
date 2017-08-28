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

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This is a Query Definition Proxy. It retrieves the SQL query from the queries store.
 *
 */
public class QueryDefinition {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueryDefinition.class);

    private String query;
    private String id;

    public QueryDefinition(String id) {
        this.id = id;
        try {
            this.query = QueryDefinitionsHolder.getQueryAsString(id);
        } catch (IllegalArgumentException e) {
            // Log and wait for a laizy loading
            LOGGER.warn(
                    "Query {} has been request but is not loaded yet. Usually this an error as the query is not defined",
                    id);
        }
    }

    
    /**
     * Returns the query as a String
     * @return the query proxied by this object
     */
    public String getQueryAsString() {
        if (query != null) {
            return query;
        }
        query = QueryDefinitionsHolder.getQueryAsString(id);
        return query;
    }

    /**
     * Returns the query as an interpolated String with the provided parameters
     * @param parameters parameters to be interpolated into the query
     * @return the query interpolated with the parameters 
     */
    public String getQueryAsString(Object... parameters) {
        final List<Object> parameterList = Arrays.asList(parameters);
        final List<Object> escapedParameterList =
                parameterList.stream().map(x -> x instanceof String ? StringUtils.replace((String) x, "'", "''") : x)
                        .collect(Collectors.toList());

        return MessageFormat.format(getQueryAsString(), escapedParameterList.toArray());
    }

    @Override
    public String toString() {
        return getQueryAsString();
    }
}
