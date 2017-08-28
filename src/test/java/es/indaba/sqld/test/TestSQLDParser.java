package es.indaba.sqld.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.InputStream;
import java.util.Properties;

import org.junit.Test;

import es.indaba.sqld.parser.TextBlockReader;


public class TestSQLDParser {

    @Test
    public void testBlockExtractor() throws Exception {
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("es/indaba/sqld/test/parser/test.sqld");

        TextBlockReader sqlReader = new TextBlockReader(stream, "es/indaba/sqld/test/parser/test.sqld");
        Properties blocks = sqlReader.read();

        String query1 = blocks.getProperty("query1");
        assertEquals("QUERY1_CONTENT", query1);

        String query2 = blocks.getProperty("query2");
        assertEquals("QUERY2_CONTENT", query2);

        String query3 = blocks.getProperty("contains_0_number");
        assertEquals("CONTAINS_0_NUMBER_CONTENT", query3);

        String query4 = blocks.getProperty("ends_with_number_0");
        assertEquals("ENDS_WITH_NUMBER_0_CONTENT", query4);
    }


    @Test
    public void testCaseInsensitiveKeys() throws Exception {
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("es/indaba/sqld/test/parser/test.sqld");

        TextBlockReader sqlReader = new TextBlockReader(stream, "es/indaba/sqld/test/parser/test.sqld");
        Properties blocks = sqlReader.read();
        assertNotNull(blocks);

        String query1 = blocks.getProperty("query1");
        assertEquals("QUERY1_CONTENT", query1);
        
        query1 = blocks.getProperty("QUERY1");
        assertNull(query1);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testKeyStartsWithNumber() throws Exception {
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("es/indaba/sqld/test/parser/test-illegal-key.sqld");

        TextBlockReader sqlReader = new TextBlockReader(stream, "es/indaba/sqld/test/parser/test-illegal-key.sqld");
        sqlReader.read();
    }

    @Test
    public void testEmptyFile() throws Exception {
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("es/indaba/sqld/test/parser/test-empty.sqld");

        TextBlockReader sqlReader = new TextBlockReader(stream, "es/indaba/sqld/test/parser/test-empty.sqld");
        Properties blocks = sqlReader.read();
        assertEquals(0, blocks.size());
    }

    @Test
    public void testEmptyBlock() throws Exception {
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("es/indaba/sqld/test/parser/test-empty-block.sqld");

        TextBlockReader sqlReader = new TextBlockReader(stream, "es/indaba/sqld/test/parser/test-empty-block.sqld");
        Properties blocks = sqlReader.read();
        assertEquals(1, blocks.size());
        String content = blocks.getProperty("empty_block");
        org.junit.Assert.assertEquals("", content);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testDuplicatedKey() throws Exception {
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("es/indaba/sqld/test/parser/test-duplicated-key.sqld");

        TextBlockReader sqlReader = new TextBlockReader(stream, "es/indaba/sqld/test/parser/test-duplicated-key.sqld");
        sqlReader.read();
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testIllegalBlock() throws Exception {
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("es/indaba/sqld/test/parser/test-illegal-block.sqld");

        TextBlockReader sqlReader = new TextBlockReader(stream, "es/indaba/sqld/test/parser/test-illegal-block.sqld");
        sqlReader.read();
    }
    
    
    @Test(expected = IllegalArgumentException.class)
    public void testEmptyKey() throws Exception {
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("es/indaba/sqld/test/parser/test-empty-key.sqld");

        TextBlockReader sqlReader = new TextBlockReader(stream, "es/indaba/sqld/test/parser/test-empty-key.sqld");
        sqlReader.read();
    }
}

