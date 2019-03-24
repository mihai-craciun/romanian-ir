/**
 * 24/03/2019
 * @author Mihai Craciun
 */

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Scanner;

/** Application main class */
public class Main {

    /** Documents directory calculated using a relative path */
    private static String WORKING_DIRECTORY = new File("documents").getAbsolutePath();
    /** Index directory calculated using a relative path */
    private static String INDEX_DIRECTORY = new File("index").getAbsolutePath();

    /* Lucene document fields */
    private static final String FIELD_PATH = "path";
    private static final String FIELD_CONTENTS = "contents";
    private static final String FIELD_FILENAME = "filename";

    /** Analyzer */
    private static final Analyzer ANALYZER = new RomanianCustomAnalyzer();
    /** Query match type */
    private static final QueryParser.Operator OPERATOR = QueryParser.Operator.AND;

    /** Main method */
    public static void main(String[] args) throws IOException, ParseException {
        createIndex();
        queryIndex();
    }

    /** Analyzes the documents and creates an indexer */
    private static void createIndex() throws IOException {
        // Create the index writer
        prepareEnvironment();
        Directory directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY));
        IndexWriterConfig config = new IndexWriterConfig(ANALYZER);
        IndexWriter indexWriter = new IndexWriter(directory, config);

        // Index all files in the working directory
        File dir = new File(WORKING_DIRECTORY);
        File[] files = dir.listFiles();

        System.out.println("Indexing files..");
        System.out.println();
        for (File file : files) {
            Document document = new Document();
            String fileText = fileParser(file);

            document.add(new StringField(FIELD_PATH, file.toString(), Field.Store.YES));
            document.add(new StringField(FIELD_FILENAME, file.getName(), Field.Store.YES));
            document.add(new TextField(FIELD_CONTENTS, fileText, Field.Store.YES));

            indexWriter.addDocument(document);
        }
        System.out.println();
        indexWriter.close();

    }

    /** Inputs query strings from command line and dumps the resulting file names */
    private static void queryIndex() throws IOException, ParseException {
        // Query the index
        final Scanner keyboard = new Scanner(System.in);
        Directory directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY));
        DirectoryReader directoryReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(directoryReader);
        String queryString;

        QueryParser queryParser = new QueryParser(FIELD_CONTENTS, ANALYZER);
        queryParser.setDefaultOperator(OPERATOR);

        while (true) {
            System.out.println("Enter a query string: ");
            queryString = keyboard.nextLine();
            Query query = queryParser.parse(queryString);

            TopDocs topDocs = indexSearcher.search(query, Integer.MAX_VALUE);
            ScoreDoc[] hits = topDocs.scoreDocs;

            System.out.println("Found " + hits.length + " hits:");
            for (ScoreDoc hit: hits) {
                int docId = hit.doc;
                Document doc = indexSearcher.doc(docId);
                System.out.println(doc.get(FIELD_FILENAME) + " (" + doc.get(FIELD_PATH) + ")");
            }
        }
    }

    private static String fileParser(File file) throws IOException {
        Tika tika = new Tika();
        System.out.println(file.getName() + " (" + tika.detect(file) + ")");

        if (!file.exists() || file.length() == 0) {
            System.err.println("No content detected");
            return "";
        }
        String filecontent = null;
        try {
            filecontent = tika.parseToString(file);
        } catch (TikaException e) {
            System.err.println("Failed to parse file with tika");
            e.printStackTrace();
        }
        return filecontent;
    }

    /** Create unexisting folders and remove old index data fi exists */
    private static void prepareEnvironment() {
        File working_dir = new File(WORKING_DIRECTORY);
        File index_dir = new File(INDEX_DIRECTORY);
        if (!working_dir.exists()) {
            working_dir.mkdirs();
        }
        if (index_dir.exists()) {
            for(File file: index_dir.listFiles())
                file.delete();
        } else {
            index_dir.mkdirs();
        }
    }
}
