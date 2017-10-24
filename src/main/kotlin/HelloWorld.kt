import org.jsoup.Jsoup
import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.util.*
import javax.xml.bind.DatatypeConverter
import kotlin.collections.HashMap
import kotlin.experimental.and

/*
    key: TkrieIuuuWJcH1NOJIWLOw
    secret: XNBMLH1ADkqKdFerygwxNaCp3isnhuETJeW7NYoEjM
*/

/*
    SearchUrl: https://www.goodreads.com/search/index.xml?
    Parameters:
    q: The query text to match against book title, author, and ISBN fields. Supports boolean operators and phrase searching.
    page: Which page to return (default 1, optional)
    key: Developer key (required).
    search[field]: Field to search, one of 'title', 'author', or 'all' (default is 'all')
*/

fun main(args:Array<String>) {
//    parseHtmlBooksToTextFiles(File("/Users/tschumacher/sandbox/bookRating/bookTexts2"))
    val books = BookTexts()
    books.addDocsFromLocation(File("/Users/tschumacher/sandbox/bookRating/bookTexts"))
    books.queryDocs("woman")
}

fun parseHtmlBooksToTextFiles(locDir: File) {
    val htmlTextFiles = locDir.listFiles()
    htmlTextFiles.forEach { f ->
        val text = Jsoup.parse(File("${f.absolutePath}/${f.name}.html").readText()).text()
        File("${f.absolutePath}/${f.name}.txt").writeText(text)
    }

}

