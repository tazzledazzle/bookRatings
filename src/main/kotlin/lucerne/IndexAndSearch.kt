package lucerne

import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.TextField
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.TopDocs
import org.apache.lucene.store.FSDirectory
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.ScoreDoc
import org.apache.lucene.store.RAMDirectory
import org.apache.lucene.util.Version
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.nio.file.Paths

val INDEX_DIR = File("/Users/tschumacher/sandbox/bookRating/out/index").toURI()!!

//todo: Index source from a website
//todo: Index source from pdfs
//todo: Infer which type is being indexed
//todo: output results to csv for consumption in other mediums
//todo: find a way to extract numerical data
fun main(args:Array<String>) {

    val dir = FSDirectory.open(Paths.get(INDEX_DIR))
    val analyzer = StandardAnalyzer()
//    val writer = createWriter(dir, analyzer)
    println("querying index")
    searchAndPrintResults("internet", dir, analyzer, true)

    //todo: add complex query mechanism based upon SQL like syntax
    //todo: create a rating algorithm that considers all "common" words of a book and then rates query returns based up the common set
}

fun clearIndex(writer: IndexWriter): Long {
    // -1L is failure return
    return writer.deleteAll()
}

fun getHits(docs: TopDocs): Array<ScoreDoc> {
    return docs.scoreDocs
}

fun searchAndPrintResults(query: String, dir: FSDirectory, analyzer: StandardAnalyzer, output: Boolean) {
    println("printing results")
    val searcher = IndexSearcher(DirectoryReader.open(dir))
    val csvOut: File = File("build/resultsFor$query.csv")

    if(output){
        csvOut.writeText("title,score,doc,shardIndex")
    }

    getHits(searchFor(query, searcher, analyzer ,100)).forEach { hit ->
        val d = searcher.doc(hit.doc)
        println("Hits: ${d.get("Title")}, Score: ${hit.score}")

        if(output)
            csvOut.appendText("${d.get("Title")},${hit.score},${hit.doc},${hit.shardIndex}")
    }
    if (output) {
        getHits(searchFor(query, searcher, analyzer ,100)).forEach { hit ->
            val d = searcher.doc(hit.doc)
        }
    }
}

fun searchFor(query: String, searcher:IndexSearcher, analyser: StandardAnalyzer, hitsPerPage: Int): TopDocs {
    val parser = QueryParser("Content", analyser)
    return searcher.search(parser.parse(query),hitsPerPage)
}

fun addAllBooks(bookDir: File, writer: IndexWriter) {
    println("indexing files")
    writer.deleteAll()
    bookDir.listFiles().forEach { book ->
        val doc = createDocument(book.name.replace(".txt", ""),
                book.readText())
        writer.addDocument(doc)
    }
    writer.close()
}

fun printDocs(foundDocs: TopDocs, searcher: IndexSearcher) {
    var i = 1
    foundDocs.scoreDocs.forEach { sd ->
        val d = searcher.doc(sd.doc)
        println("$i: ${d.get("title")}")
    }
}

@Throws(IOException::class)
fun createWriter(dir: FSDirectory, analyzer: StandardAnalyzer): IndexWriter {
    val config = IndexWriterConfig(analyzer)
    return IndexWriter(dir, config)
}

fun addDocument(dir: File, writer: IndexWriter) {
    val title = dir.name.replace(".txt", "")
    val doc = createDocumentFromFile(title, dir)
    writer.addDocument(doc)
    writer.close()
}

fun createDocument(title: String, content: String): Document {
    val document = Document()
    document.add(TextField("Title", title, Field.Store.YES))
    document.add(TextField("Content", content, Field.Store.YES))
    return document
}

fun createDocumentFromFile(title: String, file: File): Document {
    val document = Document()
    document.add(TextField("title", title, Field.Store.YES))
    document.add(TextField("content", FileReader(file)))
    return document
}

@Throws(IOException::class)
fun createSearcher(): IndexSearcher {
    val dir = FSDirectory.open(Paths.get(INDEX_DIR))
    val reader = DirectoryReader.open(dir)
    return IndexSearcher(reader)
}

@Throws(Exception::class)
fun searchByTitle(title: String, searcher: IndexSearcher): TopDocs {
    val qp = QueryParser("title", StandardAnalyzer())
    val titleQuery = qp.parse(title)
    return searcher.search(titleQuery, 10)
}

@Throws(Exception::class)
fun searchByContent(content: String, searcher: IndexSearcher): TopDocs {
    val qp = QueryParser("title", StandardAnalyzer())
    val titleQuery = qp.parse(content)
    return searcher.search(titleQuery, 10)
}