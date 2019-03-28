/*
 * Copyright (c) 3/28/19 4:13 PM
 * Eugen-Mihai Craciun
 */

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.queryparser.classic.QueryParser;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

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
    public static final Analyzer ANALYZER;
    /** Query match type */
    public static final QueryParser.Operator OPERATOR = QueryParser.Operator.AND;

    /** Try to initialize the analyzer with custom stopwords file */
    static {
        File stopWords = new File("stopwords-ro.txt");
        List<String> stopWordsList = new ArrayList<>();
        BufferedReader reader = null;
        CharArraySet stopWordsSet = null;
        try {
            reader = new BufferedReader(new FileReader(stopWords));
            String word;
            while ((word = reader.readLine()) != null)
                stopWordsList.add(StringUtils.stripAccents(word));
            stopWordsSet = new CharArraySet(stopWordsList, false);
        } catch (FileNotFoundException e) {
            System.err.println("Could not open stop words file for read");
        } catch (IOException e) {
            System.err.println("Could not read from file");
        }

        if (stopWordsSet != null) {
            System.out.println("Using stopwords file");
            ANALYZER = new RomanianCustomAnalyzer(stopWordsSet);
        } else {
            ANALYZER = new RomanianCustomAnalyzer();
        }
    }
}
