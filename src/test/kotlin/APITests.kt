import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.getAs
import com.github.kittinunf.result.success
import com.sun.deploy.xml.XMLParser
import groovy.util.XmlSlurper
import groovy.util.slurpersupport.GPathResult
import groovy.util.slurpersupport.Node
import groovy.util.slurpersupport.NodeChild
import org.junit.*
import org.junit.Assert.*
import java.sql.Connection
import java.sql.Driver
import java.sql.DriverManager
import java.sql.SQLException
import java.util.*

class APITests {
    @Test
    fun testInit() {
        assertTrue(true)
    }

    @Test
    fun testApiWorks() {
        val fullPath = "https://www.goodreads.com/search/index.xml?q=The%20witches&key=TkrieIuuuWJcH1NOJIWLOw&search=all"
        val data = Goodreads().query(fullPath)
        if (data != null) {
            assertTrue(data.isNotEmpty())
        }
    }
    @Test
    fun testMapSearchResults() {
        val resultsList = Goodreads().mapResponse("https://www.goodreads.com/search/index.xml?q=The%20witches&key=TkrieIuuuWJcH1NOJIWLOw&search=all")
        assertNotNull(resultsList)
        assertEquals(ResultBook().javaClass, resultsList[0].javaClass)
        println(resultsList.size)
        for (book in resultsList) {
            println("Book: ${book.title}, Author: ${book.author}")
        }
    }

    // only match title
    @Test
    fun testBookCompareTrueTitle() {
        val book = ResultBook()
        book.title = "A Book"
        book.author = "Some Author"
        assertTrue(Goodreads().foundResultBook(book, "A Book", ""))
    }

    // only match author
    @Test
    fun testBookCompareTrueAuthor() {
        val book = ResultBook()
        book.title = "A Book"
        book.author = "Some Author"
        assertTrue(Goodreads().foundResultBook(book, "Another Book", "Some Author"))
    }

    // only match title
    @Test
    fun testBookCompareTrueTitleWithSpace() {
        val book = ResultBook()
        book.title = "A Book"
        book.author = "Some Author"
        assertTrue(Goodreads().foundResultBook(book, " A Book ", ""))
    }

    // only match author
    @Test
    fun testBookCompareTrueAuthorWithSpace() {
        val book = ResultBook()
        book.title = "A Book"
        book.author = "Some Author"
        assertTrue(Goodreads().foundResultBook(book, "Another Book", " Some Author "))
    }

    // slightly different title
    @Test
    fun testBookCompareFalse() {
        val book = ResultBook()
        book.title = "A Book"
        book.author = "Some Author"
        assertFalse(Goodreads().foundResultBook(book, "As Book", ""))
    }

    @Test
    fun testQueryGoodreads() {
        val title = "The witches"
        val author = "Roald Dahl"
        // grab list of books on hand
        // Search for each
        val result = Goodreads().searchForMatch(title, author)
        assertNotNull(result)
        println(result)
        assertEquals("The Witches by Roald Dahl -- Rating: 4.17", result.toString())

    }
    @Test
    fun testQuerySingleNameAuthor() {
        val title = "Aesop's Fables"
        val name = "Aesop"
        val result = Goodreads().searchForMatch(title, name)
        assertNotNull(result.author)
        assertNotNull(result.title)
    }
    @Test
    fun testGetRatingsForLocalBookCollection() {
        ///Users/tschumacher/Documents/KindlePack
        val localBooks = Goodreads().getBooksFromDir("/Users/tschumacher/Documents/KindlePack")
        Goodreads().printRatingsForCollection(localBooks)
    }

    @Test
    fun testDBConnection() {
        var conn: Connection? = null
        try {
            val url = "jdbc:sqlite:test.db"
            conn = DriverManager.getConnection(url)
            assertNotNull(conn)
        } catch (e: SQLException) {
            println(e.message)
            fail()
        }
        finally {
            try {
                if (conn != null) {
                    conn.close()
                    assertTrue(conn.isClosed)
                }
            } catch (ex: SQLException) {
                println(ex.message)
                fail()
            }
        }
    }

    @Test
    fun testCreateTableInDB() {
        val url = "jdbc:sqlite:test.db"
        val sql = """|CREATE TABLE IF NOT EXISTS bookTests (
            | id integer PRIMARY KEY,
            | name text NOT NULL,
            | capacity real
            | );
            """.trimMargin()

        try {
            val conn = DriverManager.getConnection(url)
            val stmt = conn.createStatement()
            stmt.execute(sql)
        }
        catch (e: SQLException) {
            println(e.message)
            fail()
        }
    }

    @Test
    fun testInsertStatementInDB() {
        val url = "jdbc:sqlite:test.db"
        val sql = "INSERT INTO bookTests (name) values('weeble')"
        try {
            val conn = DriverManager.getConnection(url)
            val stmt = conn.createStatement()
            stmt.execute(sql)
        }
        catch (e: SQLException) {
            println(e.message)
            fail()
        }

    }
//    BEGIN
    //    IF NOT EXISTS (SELECT * FROM EmailsRecebidos
    //    WHERE De = @_DE
        //    AND Assunto = @_ASSUNTO
        //    AND Data = @_DATA)
//    BEGIN
    //    INSERT INTO EmailsRecebidos (De, Assunto, Data)
        //    VALUES (@_DE, @_ASSUNTO, @_DATA)
    //    END
//    END
    @Test
    fun testSelectStatementInDB() {
        val resultString = Goodreads().selectAllFromDB()
        assertNotNull(resultString)
        assertEquals(String::class.java, resultString.javaClass)

    }

    @Test
    fun testInsertFromBook() {
        val url = "jdbc:sqlite:test.db"
//        val resultBook = Goodreads().searchForMatch("Thinking, Fast and Slow", "Daniel Kahneman")
//        val sql = "INSERT INTO bookTests(name) values ('${resultBook.title}')"
        val sql = "SELECT * FROM bookTests"

        try {
            val conn = DriverManager.getConnection(url)
            val stmt = conn.createStatement()
//            stmt.execute(sql)
            val rs = stmt.executeQuery(sql)
            while(rs.next()) {
                val id = rs.getInt("id")
                val name = rs.getString("name")
                println("Id : $id, Name: $name")

            }
        }
        catch (e: SQLException) {
            println(e.message)
            fail()
        }

    }

    @Test
    fun testCreateBookDB() {
        Goodreads().createBooksDB()
    }

    @Test
    fun testInsertIntoBookDB() {
        val result = Goodreads().searchForMatch("Pines", "Blake Crouch")
        Goodreads().insertResultBookIntoDB(result)
    }

}

