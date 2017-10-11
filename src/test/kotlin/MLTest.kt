import groovy.util.GroovyTestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.api.dsl.xgiven
import org.junit.rules.TemporaryFolder
import java.io.File
import java.util.*

object MLTest: Spek({
    //todo: skip
    xgiven("an Html text file") {
        val tempDir = TemporaryFolder()
        tempDir.create()
        val textFile = File(tempDir.root,"testFile.html")
        textFile.writeText("""<html><body>This is a test with testing and tests. I test</body></html>
            """)
        it("should exist as a file"){
            assertTrue(textFile.exists())
        }

        on("parsing of the textFile"){
            val tokenCountMap = BookTokenizer().parseToCountMap(textFile)
            it("should return a count map") {
                assertEquals(TreeMap<String, Int>().javaClass, tokenCountMap.javaClass)
            }


        }
    }
    //todo: skip
    xgiven("a set of books"){
//        val bookTree = BookTexts("/Users/tschumacher/sandbox/bookRating/src/test/testBooks").getTokenizedMaps()
//        xit("Should only contain one book") {
//            assertEquals(1, bookTree.size)
//        }
//        xit ("Should have a map of tokens associated with it") {
//            val tokenList = bookTree[0].tokenMap
//            assertEquals(TreeMap<String, Int>()::class.java, tokenList::class.java)
//        }
        on("passing a specific book in to see similar books"){
            val anotherBookTree = BookTexts("/Users/tschumacher/sandbox/bookRating/bookTexts")
//            val anotherBookTree = BookTexts("/Users/tschumacher/sandbox/bookRating/src/test/testBooks")
//            val queryBook = TokenizedBook(File("/Users/tschumacher/sandbox/bookRating/bookTexts/Crichton, Michael - Prey/Crichton, Michael - Prey.html"))
            val queryBook = TokenizedBook(File("/Users/tschumacher/Downloads/revolution.html"))

            it ("should have the same name as the book passed in") {
                assertEquals("Crichton, Michael - Prey", queryBook.name)
            }
            it ("should have tokenized content") {
                assertEquals(TreeMap<String, Int>()::class.java, queryBook.tokenMap::class.java)
            }
            it("should return five similar books"){
                val resultsList = anotherBookTree.findSimilar(queryBook)
                assertEquals(500, resultsList.size)
            }
        }
        on("passing a book in that doesn't have similar books") {
            it("should provide one book that is semi similar"){
                
            }
        }
    }
}) {
}
