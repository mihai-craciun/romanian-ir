/*
 * Copyright (c) 3/28/19 4:23 PM
 * Eugen-Mihai Craciun
 */

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws ParseException, IOException {
        // Query the index
        final Scanner keyboard = new Scanner(System.in);
        Directory directory = FSDirectory.open(Paths.get(Settings.INDEX_DIRECTORY));
        DirectoryReader directoryReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(directoryReader);
        String queryString;

        QueryParser queryParser = new QueryParser(Settings.FIELD_CONTENTS, Settings.ANALYZER);
        queryParser.setDefaultOperator(Settings.OPERATOR);

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
                System.out.println(doc.get(Settings.FIELD_FILENAME) + " (" + doc.get(Settings.FIELD_PATH) + ")");
            }
        }
    }
}
