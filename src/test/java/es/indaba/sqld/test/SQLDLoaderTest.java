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

import es.indaba.sqld.QueryDefinition;
import es.indaba.sqld.QueryDefinitionsHolder;
import es.indaba.sqld.loader.SQLDClassPathLoader;

public class SQLDLoaderTest {

    @Test
    public void testSqldLoad() {
        SQLDClassPathLoader.loadSqlds("es.indaba.sqld.test.loader.test.package1");
        // Check loader
        QueryDefinition query1 = new QueryDefinition("QUERY1");
        assertEquals("QUERY1_CONTENT", query1.getQueryAsString());
        QueryDefinition query2 = new QueryDefinition("QUERY2");
        assertEquals("QUERY2_CONTENT", query2.getQueryAsString());

        QueryDefinition querySubs = new QueryDefinition("QUERY_SUBSTITUTION");
        assertEquals("QUERY_SUBSTITUTION {0},{1},{2}", querySubs.getQueryAsString());
        assertEquals(querySubs.getQueryAsString(), querySubs.toString());
        assertEquals("QUERY_SUBSTITUTION a,''b'',3", querySubs.getQueryAsString("a", "'b'",3));

        QueryDefinition queryYaml = new QueryDefinition("QUERY_YAML");
        assertEquals("QUERY_YAML_CONTENT\n", queryYaml.getQueryAsString());
        
        // Check not loaded
        QueryDefinition query3 = new QueryDefinition("QUERY3");
        boolean thrown = false;
        try {
            query3.getQueryAsString();
        } catch (IllegalArgumentException e) {
            thrown = true;
        }
        assertTrue(thrown);

        // Loads upper prefix
        SQLDClassPathLoader.loadSqlds("es.indaba.sqld.test.loader.test");
        query3 = new QueryDefinition("QUERY3");
        assertEquals("QUERY3_CONTENT", query3.getQueryAsString());
        // template not loaded
        QueryDefinition template1 = new QueryDefinition("TEMPLATE1");
        thrown = false;
        try {
            template1.getQueryAsString();
        } catch (IllegalArgumentException e) {
            thrown = true;
        }

        QueryDefinitionsHolder.clear();
        
        query3 = new QueryDefinition("QUERY3");
        thrown = false;
        try {
            query3.getQueryAsString();
        } catch (IllegalArgumentException e) {
            thrown = true;
        }
        assertTrue(thrown);
    }

    @Test
    public void testExtension() {
        SQLDClassPathLoader.loadBlockFiles("es.indaba.sqld.test.loader.test", "template");
        QueryDefinition query3 = new QueryDefinition("QUERY3");
        boolean thrown = false;
        try {
            query3.getQueryAsString();
        } catch (IllegalArgumentException e) {
            thrown = true;
        }
        assertTrue(thrown);
        QueryDefinition template1 = new QueryDefinition("TEMPLATE1");
        assertEquals("TEMPLATE1_CONTENT", template1.getQueryAsString());

        QueryDefinitionsHolder.clear();
    }
    
    @Test
    public void testLazyLoad() {
        QueryDefinition template1 = new QueryDefinition("TEMPLATE1");
        boolean thrown = false;
        try {
            template1.getQueryAsString();
        } catch (IllegalArgumentException e) {
            thrown = true;
        }
        assertTrue(thrown);
        SQLDClassPathLoader.loadBlockFiles("es.indaba.sqld.test.loader.test", "template");
        assertEquals("TEMPLATE1_CONTENT", template1.getQueryAsString());

        QueryDefinitionsHolder.clear();
        
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testDuplicatedKey() {
        SQLDClassPathLoader.loadSqlds("es.indaba.sqld.test.loader");
    }

}
