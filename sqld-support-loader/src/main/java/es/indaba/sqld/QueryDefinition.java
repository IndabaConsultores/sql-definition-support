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

import org.apache.commons.lang.StringUtils;

public class QueryDefinition {

    private String query;
    private String id;

    public QueryDefinition(String id) {
        this.id = id;
        // Dependiendo del orden de carga y ejecucion de las clases en el classLoader,
        // el query puede ser null
        this.query = QueryDefinitionsHolder.getQueryAsString(id);
    }

    public String getQueryAsString() {
        if (query != null) {
            return query;
        }
        // No se ha cargado bien al inicializar los contextos, recargamos por si acaso.
        query = QueryDefinitionsHolder.getQueryAsString(id);
        return query;
    }

    public String getQueryAsString(Object... parameters) {
        return MessageFormat.format(StringUtils.replace(getQueryAsString(), "'", "''"), parameters);
    }

    @Override
    public String toString() {
        return getQueryAsString();
    }
}
