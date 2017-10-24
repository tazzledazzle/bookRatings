import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.TextField
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.store.RAMDirectory
import org.jsoup.Jsoup
import java.io.File
import java.io.FileReader
import java.io.StringReader
import java.util.*
import kotlin.collections.HashMap

/**
 * Handles parsing and computations
 * @input location: Directory where books are located
 */
class BookTexts() {
    var analyzer = StandardAnalyzer()
    val index = RAMDirectory()
    val config = IndexWriterConfig(analyzer)
    val w = IndexWriter(index, config)
    val query = QueryParser("contents", analyzer)

    fun addDoc(w: IndexWriter, title:String, loc:String){
        val doc = Document()
        doc.add(TextField("title", title, Field.Store.YES))
        doc.add(TextField("contents", StringReader(Jsoup.parse(File(loc).readText()).text())))
    }

    fun addDocsFromLocation(dirLocation: File){
        dirLocation.listFiles().forEach { f ->
            val re = Regex("\\.[a-zA-Z]+")
            val docTitle = f.name.replace(re, "")
//            println("--> File named $docTitle")
            addDoc(w, docTitle, "${f.absolutePath}/${f.name}.txt")
        }
        w.close()
    }

    fun queryDocs(q: String) {
        val qResult = query.parse(q)
        val reader = DirectoryReader.open(index)
        val searcher = IndexSearcher(reader)
        val docs = searcher.search(qResult, 200)
        val hits = docs.scoreDocs

        // display results
        println("Found ${hits.size} hits")
        var i = 1
        hits.forEach { hit ->
            val docId = hit.doc
            val d = searcher.doc(docId)
            println("$i . ${d.get("title")}")   //todo: is there's more information needed here?
            i += 1
        }
        reader.close()
    }

}
