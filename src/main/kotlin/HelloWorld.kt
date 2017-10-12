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
    val bookTexts = BookTexts()
    val inputSet = File("/Users/tschumacher/sandbox/bookRating/bookTexts")
    val mapToTextFile = File("out/bookTextsMap.txt")
//    bookTexts.parseBooksToMapsAndWriteToFile(inputSet, mapToTextFile)
    val map = bookTexts.parseMapsFromFile(mapToTextFile)
    val queryBook = TokenizedBook(File("/Users/tschumacher/Downloads/revolution.html"))
//    queryBook.tokenMap.toList().sortedBy { (_,value) -> value }.asReversed().toMap().forEach { k, v ->
//        println("$k :: $v")
//    }
    bookTexts.findSimilar(queryBook)
}


