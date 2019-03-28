/*
 * Copyright (c) 3/28/19 4:13 PM
 * Eugen-Mihai Craciun
 */

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.classic.QueryParser;

import java.io.File;

public class Settings {

    /** Documents directory calculated using a relative path */
    public static String WORKING_DIRECTORY = new File("documents").getAbsolutePath();
    /** Index directory calculated using a relative path */
    public static String INDEX_DIRECTORY = new File("index").getAbsolutePath();

    /* Lucene document fields */
    public static final String FIELD_PATH = "path";
    public static final String FIELD_CONTENTS = "contents";
    public static final String FIELD_FILENAME = "filename";

    /** Analyzer */
    public static final Analyzer ANALYZER = new RomanianCustomAnalyzer();
    /** Query match type */
    public static final QueryParser.Operator OPERATOR = QueryParser.Operator.AND;

    /** Main method */
}
