package sql;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import es.indaba.sqld.QueryDefinition;
import es.indaba.sqld.loader.SQLDClassPathLoader;

public class SqldLoaderTest {
    
    @Test
    public void testSqldLoad() {
        SQLDClassPathLoader.loadSqlds("com.lks.test.sql");
        // Check loader
        QueryDefinition query1 = new QueryDefinition("QUERY1");
        assertEquals("QUERY1_CONTENT", query1.getQueryAsString());
        QueryDefinition query2 = new QueryDefinition("QUERY2");
        assertEquals("QUERY2_CONTENT", query2.getQueryAsString());
        // Check not loaded
        QueryDefinition query3 = new QueryDefinition("QUERY3");
        assertNull(query3.getQueryAsString());

        // Loads upper prefix
        SQLDClassPathLoader.loadSqlds("com.lks");
        query3 = new QueryDefinition("QUERY3");
        assertEquals("QUERY3_CONTENT", query3.getQueryAsString());
        // template not loaded
        QueryDefinition template1 = new QueryDefinition("TEMPLATE1");
        assertNull(template1.getQueryAsString());
    }

    public void testExtension() {
        SQLDClassPathLoader.loadBlockFiles("com.lks.test.sql", "template");
        QueryDefinition query3 = new QueryDefinition("QUERY3");
        assertNull(query3.getQueryAsString());
        QueryDefinition template1 = new QueryDefinition("TEMPLATE1");
        assertEquals("TEMPLATE1_CONTENT", template1.getQueryAsString());
    }

}
