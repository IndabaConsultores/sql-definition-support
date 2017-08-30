package es.indaba.sqld;

import es.indaba.sqld.impl.QueryDefinitionStaticImpl;

/**
 * 
 * Class defined for backward compatibility
 *
 */
public class QueryDefinition extends QueryDefinitionStaticImpl {

    public QueryDefinition(String key) {
        super(key);
    }

}
