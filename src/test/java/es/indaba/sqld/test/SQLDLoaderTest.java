/*******************************************************************************
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version. This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details. You should have received a copy of the GNU Lesser General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>
 * 
 *******************************************************************************/
package es.indaba.sqld.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import es.indaba.sqld.QueryDefinitionsStaticHolder;
import es.indaba.sqld.api.QueryDefinition;

public class SQLDLoaderTest {

    @Test
    public void testSqldLoad() {
        QueryDefinitionsStaticHolder.loadQueryDefinitions("es.indaba.sqld.test.loader.test.package1");
        // Check loader
        QueryDefinition query1 = QueryDefinitionsStaticHolder.getQueryDefinition("QUERY1");
        assertEquals("QUERY1_CONTENT", query1.getQueryAsString());
        QueryDefinition query2 = QueryDefinitionsStaticHolder.getQueryDefinition("QUERY2");
        assertEquals("QUERY2_CONTENT", query2.getQueryAsString());

        QueryDefinition querySubs = QueryDefinitionsStaticHolder.getQueryDefinition("QUERY_SUBSTITUTION");
        assertEquals("QUERY_SUBSTITUTION {0},{1},{2}", querySubs.getQueryAsString());
        assertEquals(querySubs.getQueryAsString(), querySubs.toString());
        assertEquals("QUERY_SUBSTITUTION a,''b'',3", querySubs.getQueryAsString("a", "'b'", 3));

        QueryDefinition queryYaml = QueryDefinitionsStaticHolder.getQueryDefinition("QUERY_YAML");
        assertEquals("QUERY_YAML_CONTENT\n", queryYaml.getQueryAsString());

        // Check not loaded
        QueryDefinition query3 = QueryDefinitionsStaticHolder.getQueryDefinition("QUERY3");
        boolean thrown = false;
        try {
            query3.getQueryAsString();
        } catch (IllegalArgumentException e) {
            thrown = true;
        }
        assertTrue(thrown);

        // Loads upper prefix
        QueryDefinitionsStaticHolder.loadQueryDefinitions("es.indaba.sqld.test.loader.test");
        query3 = QueryDefinitionsStaticHolder.getQueryDefinition("QUERY3");
        assertEquals("QUERY3_CONTENT", query3.getQueryAsString());
        // template not loaded
        QueryDefinition template1 = QueryDefinitionsStaticHolder.getQueryDefinition("TEMPLATE1");
        thrown = false;
        try {
            template1.getQueryAsString();
        } catch (IllegalArgumentException e) {
            thrown = true;
        }

        QueryDefinitionsStaticHolder.clear();

        query3 = QueryDefinitionsStaticHolder.getQueryDefinition("QUERY3");
        thrown = false;
        try {
            query3.getQueryAsString();
        } catch (IllegalArgumentException e) {
            thrown = true;
        }
        assertTrue(thrown);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testDuplicatedKey() {
        QueryDefinitionsStaticHolder.loadQueryDefinitions("es.indaba.sqld.test.loader");
    }

}
