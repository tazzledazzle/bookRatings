import groovy.util.GroovyTestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.api.dsl.xgiven
import org.junit.rules.TemporaryFolder
import java.io.File
import java.util.*

object BookTextTests : Spek({
    given("a book object") {
        on("creation of a general book object"){
            val book = Book("Silverstein, Shel", "Where the Sidewalk Ends")

            it("Should set the author correctly"){
                assertEquals("Shel Silverstein", book.author)
            }
            it("Should have the proper title") {
                assertEquals("Where the Sidewalk Ends", book.title)
            }
        }
    }

    xgiven("a set of books"){
        val inputSet = File("/Users/tschumacher/sandbox/bookRating/src/test/testBooks")
        val testMapTextFile = File("out/bookTextsMapTest.txt")

        on("parsing a book set and saving maps to a file") {
            BookTexts().parseBooksToMapsAndWriteToFile(inputSet, testMapTextFile )

            it("should create a text file with all tokenizedBooks") {
                assertTrue(testMapTextFile.exists())
            }

            it("should create a text file with contents") {
                assertTrue(testMapTextFile.readText().isNotEmpty())
            }

            it("should be possible to create a map object from the created file"){
                val bookObj = BookTexts()
                val parsedMapFromFile = bookObj.parseMapsFromFile(testMapTextFile)
                val originalBooks = bookObj.getTokenizedMaps()
                assertEquals(originalBooks.size, parsedMapFromFile.size)
            }
        }
    }

    given("a different set of books") {
        val inputSet = File("/Users/tschumacher/sandbox/bookRating/bookTexts")
        val mapToTextFile = File("out/bookTextsMap.txt")

        on("parsing books from ${inputSet.absolutePath}") {
            val bookTexts = BookTexts()
            bookTexts.parseBooksToMapsAndWriteToFile(inputSet, mapToTextFile)

            it("should create a text file with all tokenizedBooks") {
                assertTrue(mapToTextFile.exists())
            }
        }

    }

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

})