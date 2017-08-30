/*******************************************************************************
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version. This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details. You should have received a copy of the GNU Lesser General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>
 * 
 *******************************************************************************/
package es.indaba.sqld.api;

/**
 * This is a Query Definition Proxy. It retrieves the SQL query from the queries store.
 *
 */
public interface QueryDefinition {

    /**
     * Returns the query as a String
     * 
     * @return the query proxied by this object
     */
    public String getQueryAsString();

    /**
     * Returns the query as an interpolated String with the provided parameters
     * 
     * @param parameters parameters to be interpolated into the query
     * @return the query interpolated with the parameters
     */
    public String getQueryAsString(Object... parameters);


}
