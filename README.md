# romanian-ir
A lucene based Romanian Information Retrieval system ( Indexer and Searcher )

## Compiling sources
<p><b>NOTE: Use JDK 8 to compile the sources</b></p>
Run the following maven command:<br/>
<p><code>$ mvn clean compile assembly:single</code></p>
<p>This command will generate the JAR files. The ones we need are the following:</p>

* <b>Indexer:</b> <i>romanianir-index/target/romanianir-index-1.0-SNAPSHOT-jar-with-dependencies.jar</i><br/>
* <b>Searcher:</b> <i>romanianir-search/target/romanianir-search-1.0-SNAPSHOT-jar-with-dependencies.jar</i><br/>

## Usage
1. Create an environment (folder) with the following:
  * <b>Indexer</b> jar
  * <b>Searcher</b> jar
  * index (folder)
  * documents (folder)
  * <i>stopwords-ro.txt</i> (optional: an extended romanian stopwords list)
2. Put whatever documents you want into the documents folder
3. Run indexer to index the files.
4. Run searcher and enter as many questions as you want
5. Top stop querying press <b>CTRL+C</b>
