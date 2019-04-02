/*
 * Copyright (c) 3/28/19 4:12 PM
 * Eugen-Mihai Craciun
 */

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) throws IOException {
        // Create the index writer
        prepareEnvironment();
        Directory directory = FSDirectory.open(Paths.get(Settings.INDEX_DIRECTORY));
        IndexWriterConfig config = new IndexWriterConfig(Settings.ANALYZER);
        IndexWriter indexWriter = new IndexWriter(directory, config);

        // Index all files in the working directory
        File dir = new File(Settings.DOCUMENTS_DIRECTORY);
        File[] files = dir.listFiles();

        System.out.println("Indexing files..");
        System.out.println();
        for (File file : files) {
            Document document = new Document();
            String fileText = fileParser(file);

            document.add(new StringField(Settings.FIELD_PATH, file.toString(), Field.Store.YES));
            document.add(new StringField(Settings.FIELD_FILENAME, file.getName(), Field.Store.YES));
            document.add(new TextField(Settings.FIELD_CONTENTS, fileText, Field.Store.YES));

            indexWriter.addDocument(document);
        }
        System.out.println();
        indexWriter.close();

    }

    /** Create unexisting folders and remove old index data fi exists */
    private static void prepareEnvironment() {
        File working_dir = new File(Settings.DOCUMENTS_DIRECTORY);
        File index_dir = new File(Settings.INDEX_DIRECTORY);
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
}
