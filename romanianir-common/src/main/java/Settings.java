/*
 * Copyright (c) 3/28/19 4:13 PM
 * Eugen-Mihai Craciun
 */

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.yaml.snakeyaml.Yaml;

import javax.management.QueryEval;
import java.io.*;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Settings {

    /** Documents directory calculated using a relative path */
    public static String DOCUMENTS_DIRECTORY = new File("documents").getAbsolutePath();
    /** Index directory calculated using a relative path */
    public static String INDEX_DIRECTORY = new File("index").getAbsolutePath();
    /** Configuration file */
    public static String CONFIG_FILE = new File("config.yml").getAbsolutePath();
    /** Stopwords file */
    public static String STOPWORDS_FILE = new File("stopwords-ro.txt").getAbsolutePath();

    /* Config file fields */
    public static final String CONFIG_DOCUMENTS_DIRECTORY = "documents";
    public static final String CONFIG_INDEX_DIRECTORY = "index";
    public static final String CONFIG_STOPWORDS_FILE = "stopwords";

    /* Lucene document fields */
    public static final String FIELD_PATH = "path";
    public static final String FIELD_CONTENTS = "contents";
    public static final String FIELD_FILENAME = "filename";

    /** Analyzer */
    public static final Analyzer ANALYZER;

    /** Try to initialize the analyzer with custom stopwords file */
    static {
        Yaml yaml = new Yaml();
        Map<String, Object> yamlContent = null;
        try {
            yamlContent = yaml.load(new FileInputStream(CONFIG_FILE));
            if (yamlContent == null) {
                System.err.println("Config file could not pe parsed. Using default settings");
            } else {
                String documentsDirectory = (String) yamlContent.get(CONFIG_DOCUMENTS_DIRECTORY);
                if (documentsDirectory != null) {
                    DOCUMENTS_DIRECTORY = documentsDirectory;
                }
                String indexDirectory = (String) yamlContent.get(CONFIG_INDEX_DIRECTORY);
                if (indexDirectory != null) {
                    INDEX_DIRECTORY = indexDirectory;
                }
                String stopwordsFile = (String) yamlContent.get(CONFIG_STOPWORDS_FILE);
                if (stopwordsFile != null) {
                    STOPWORDS_FILE = stopwordsFile;
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Config file not found. Using default settings");
        }
        File stopWords = new File(STOPWORDS_FILE);
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
