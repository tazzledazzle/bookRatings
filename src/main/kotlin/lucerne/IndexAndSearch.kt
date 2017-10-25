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
import org.apache.lucene.store.RAMDirectory
import org.apache.lucene.util.Version
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.nio.file.Paths

val INDEX_DIR = File("/Users/tschumacher/sandbox/bookRating/out/index").toURI()!!

fun main(args:Array<String>) {

    //todo: split into method
//    val writer = createWriter()
//
//    val files = arrayOf(
//        File("/Users/tschumacher/sandbox/bookRating/textfiles/Clarke, Arthur C. - 2001 A Space Odyssey.txt"),
//        File("/Users/tschumacher/sandbox/bookRating/textfiles/Dick, Philip K. - Do Androids Dream of Electric Sheep.txt"),
//        File("/Users/tschumacher/sandbox/bookRating/textfiles/Dick, Philip K. - Flow My Tears, The Policeman Said.txt"),
//        File("/Users/tschumacher/sandbox/bookRating/textfiles/Dickens, Charles - A Tale of Two Cities.txt")
//    )
//
//    writer.deleteAll()
//    files.forEach { content ->
//        val title = content.name.replace(".txt", "")
//        val doc = createDocumentFromFile(title, content)
//        writer.addDocument(doc)
//
//    }
//    writer.close()
//
//    // search
//    val searcher = createSearcher()
//    val foundDocs = searchByTitle("Androids", searcher)
//    println("Total Results::${foundDocs.totalHits}")
//    printDocs(foundDocs, searcher)
//    val foundContentDocs = searchByContent("Android", searcher)
//    println("Total Results::${foundContentDocs.totalHits}")
//    printDocs(foundContentDocs, searcher)
    val analyzer = StandardAnalyzer()
//    val dir = RAMDirectory()
    val dir = FSDirectory.open(Paths.get(INDEX_DIR))

    val config = IndexWriterConfig(analyzer)
    val writer = IndexWriter(dir, config)

    println("indexing Files")
//    val files = File("/Users/tschumacher/sandbox/bookRating/textfiles").listFiles()
//    files.forEach { content ->
//        val title = content.name.replace(".txt", "")
//        val docLoop = createDocument(title, content.readText())
//        writer.addDocument(docLoop)
//
//    }
//
//    writer.close()
    //todo: split into method

    println("querying index")
    val reader = DirectoryReader.open(dir)
    val searcher = IndexSearcher(reader)
    val parser = QueryParser("Content", analyzer)
    val query = parser.parse("internet")
    val hitsPerPage = 100
    val docs = searcher.search(query,hitsPerPage)
    println("printing results")
    val hits = docs.scoreDocs
    val end = Math.min(docs.totalHits.toInt(), hitsPerPage)
    hits.forEach { hit ->
        val d = searcher.doc(hit.doc)
        println("Hits: ${d.get("Title")}, Score: ${hit.score}")
    }
    //todo: add complex query mechanism based upon SQL like syntax
}

fun addAllBooks(bookDir: File, writer: IndexWriter) {
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
fun createWriter(): IndexWriter {

    val dir = FSDirectory.open(Paths.get(INDEX_DIR))
    val config = IndexWriterConfig(StandardAnalyzer())
    return IndexWriter(dir, config)
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