import groovy.util.GroovyTestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import java.io.File

object BookTests: Spek({
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

    given("a set of books"){
        val inputSet = File("/Users/tschumacher/sandbox/bookRating/src/test/testBooks")
        val testMapTextFile = File("out/bookTextsMapTest.txt")
        testMapTextFile.writeText("")

        on("parsing a book set and saving maps to a file") {
            val bookTexts = BookTexts().parseBooksToMapsAndWriteToFile(inputSet, testMapTextFile )
            it("should create a text file with all tokenizedBooks") {
                assertTrue(testMapTextFile.exists())
            }
            it("should create a text file with contents") {
                assertTrue(testMapTextFile.readText().isNotEmpty())
            }

        }

    }
})