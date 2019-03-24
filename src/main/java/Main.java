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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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
        deleteOldIndex();
        Directory directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY));
        IndexWriterConfig config = new IndexWriterConfig(ANALYZER);
        IndexWriter indexWriter = new IndexWriter(directory, config);

        // Index all files in the working directory
        File dir = new File(WORKING_DIRECTORY);
        File[] files = dir.listFiles();
        for (File file : files) {
            Document document = new Document();

            document.add(new StringField(FIELD_PATH, file.toString(), Field.Store.YES));
            document.add(new StringField(FIELD_FILENAME, file.getName(), Field.Store.YES));
            document.add(new TextField(FIELD_CONTENTS, new String(Files.readAllBytes(file.toPath())), Field.Store.YES));

            indexWriter.addDocument(document);
        }
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

    /** Clears the index workspace by deleting all existent files */
    private static void deleteOldIndex() {
        File dir = new File(INDEX_DIRECTORY);
        for(File file: dir.listFiles())
                file.delete();
    }
}
