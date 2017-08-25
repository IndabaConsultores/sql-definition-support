package es.indaba.sqld.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import es.indaba.sqld.QueryDefinition;
import es.indaba.sqld.QueryDefinitionsHolder;
import es.indaba.sqld.loader.SQLDClassPathLoader;

public class SqldLoaderTest {

    @Test
    public void testSqldLoad() {
        SQLDClassPathLoader.loadSqlds("es.indaba.sqld.test.sqld");
        // Check loader
        QueryDefinition query1 = new QueryDefinition("QUERY1");
        assertEquals("QUERY1_CONTENT", query1.getQueryAsString());
        QueryDefinition query2 = new QueryDefinition("QUERY2");
        assertEquals("QUERY2_CONTENT", query2.getQueryAsString());

        QueryDefinition querySubs = new QueryDefinition("QUERY_SUBSTITUTION");
        assertEquals("QUERY_SUBSTITUTION {0},{1}", querySubs.getQueryAsString());
        assertEquals(querySubs.getQueryAsString(), querySubs.toString());
        assertEquals("QUERY_SUBSTITUTION a,''b''", querySubs.getQueryAsString("a", "'b'"));


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
        SQLDClassPathLoader.loadSqlds("es.indaba");
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
        SQLDClassPathLoader.loadBlockFiles("es.indaba.sqld.test.sqld", "template");
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



}
