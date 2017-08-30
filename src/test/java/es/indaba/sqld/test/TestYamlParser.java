package es.indaba.sqld.test;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

import es.indaba.sqld.impl.parser.YamlFileReader;


public class TestYamlParser {

    @Test
    public void testYamlParser() throws Exception {
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("es/indaba/sqld/test/yaml/test.ysqld");

        YamlFileReader sqlReader = new YamlFileReader(stream, "es/indaba/sqld/test/yaml/test.ysqld");
        Properties blocks = sqlReader.read();

        String query1 = blocks.getProperty("query1");
        assertEquals("QUERY1_CONTENT\n", query1);

        String query2 = blocks.getProperty("query2");
        assertEquals("QUERY2_CONTENT\n", query2);
    }

    @Test
    public void testCaseSensitiveKeys() throws Exception {
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("es/indaba/sqld/test/yaml/test.ysqld");

        YamlFileReader sqlReader = new YamlFileReader(stream, "es/indaba/sqld/test/yaml/test.ysqld");
        Properties blocks = sqlReader.read();

        String query1 = blocks.getProperty("query1");
        assertEquals("QUERY1_CONTENT\n", query1);

        query1 = blocks.getProperty("QUERY1");
        Assert.assertNull(query1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDuplicatedKey() throws Exception {
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("es/indaba/sqld/test/yaml/test2.ysqld");

        YamlFileReader sqlReader = new YamlFileReader(stream, "es/indaba/sqld/test/yaml/test2.ysqld");
        sqlReader.read();
    }

    @Test
    public void testEmptyFile() throws Exception {
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("es/indaba/sqld/test/yaml/empty.ysqld");

        YamlFileReader sqlReader = new YamlFileReader(stream, "es/indaba/sqld/test/yaml/empty.ysqld");
        sqlReader.read();
        Properties blocks = sqlReader.read();
        assertEquals(0, blocks.size());
    }

    
/*
    @Test(expected = IllegalArgumentException.class)
    public void testKeyStartsWithNumber() throws Exception {
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("es/indaba/sqld/test/parser/test-illegal-key.sqld");

        TextBlockReader sqlReader = new TextBlockReader(stream, "es/indaba/sqld/test/parser/test-illegal-key.sqld");
        sqlReader.read();
    }

   
    @Test
    public void testEmptyBlock() throws Exception {
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("es/indaba/sqld/test/parser/test-empty-block.sqld");

        TextBlockReader sqlReader = new TextBlockReader(stream, "es/indaba/sqld/test/parser/test-empty-block.sqld");
        Properties blocks = sqlReader.read();
        assertEquals(1, blocks.size());
        String content = blocks.getProperty("EMPTY_BLOCK");
        org.junit.Assert.assertEquals("", content);
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
   */
}

