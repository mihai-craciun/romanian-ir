/**
 * 24/03/2019
 * @author Mihai Craciun
 */
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.ro.RomanianAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {

    private static String WORKING_DIRECTORY = new File("documents").getAbsolutePath();

    public static final String FIELD_PATH = "path";
    public static final String FIELD_CONTENTS = "contents";


    public static void main(String[] args) throws IOException {
        // Create the index writer
        Analyzer analyzer = new RomanianAnalyzer();
        Directory directory = FSDirectory.open(Paths.get(WORKING_DIRECTORY));
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter indexWriter = new IndexWriter(directory, config);

        // Index all files in the working directory
        File dir = new File(WORKING_DIRECTORY);
        File[] files = dir.listFiles();
        for (File file : files) {
            Document document = new Document();

            document.add(new StringField(FIELD_PATH, file.toString(), Field.Store.YES));
            document.add(new TextField(FIELD_CONTENTS, new String(Files.readAllBytes(file.toPath())), Field.Store.YES));

            indexWriter.addDocument(document);
        }
        indexWriter.close();
    }
}
