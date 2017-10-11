import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.success
import groovy.util.XmlSlurper
import groovy.util.slurpersupport.NodeChild
import java.io.File
import java.sql.DriverManager
import java.sql.SQLException
import java.sql.Statement
import java.util.ArrayList

class Goodreads {
    private val key = "TkrieIuuuWJcH1NOJIWLOw"
    private val searchParam = "all"
    private val dbUrl = "jdbc:sqlite:goodreads.db"

    fun searchForMatch(title: String, author: String): ResultBook {
        val results = mapResponse("https://www.goodreads.com/search/index.xml?q=${title}y&key=$key&search=$searchParam")
        results.forEach { resultsBook ->
            if (foundResultBook(resultsBook, title, author)) {

                return resultsBook
            }
//            else {
//                println("Mismatch:\nResultBook='${resultsBook.author}'\nLocalBook='${author}'")
//            }
        }
        val incompleteBook = ResultBook()
        incompleteBook.author = author
        incompleteBook.title = title
        incompleteBook.incomplete = true
        return incompleteBook
    }

    fun foundResultBook(resultsBook: ResultBook, title: String, author: String): Boolean {
        return when {
            resultsBook.author == author && resultsBook.title == title -> true
            resultsBook.author == author -> true
            resultsBook.author.trim() == author.trim() -> true
            resultsBook.title == title -> true
            resultsBook.title.trim() == title.trim() -> true
            else -> false
        }
    }

    fun mapResponse(url: String): ArrayList<ResultBook> {
        val data = XmlSlurper().parse(url)
        val resultsList = ArrayList<ResultBook>()
        for (node in data.depthFirst()) {
            val child = (node as NodeChild)
            if (child.name() == "work"){
                val newBook = ResultBook()
                child.children().forEach { t ->
                    val workChild = (t as NodeChild)
                    when(workChild.name()) {
                        "id" -> newBook.id = getNumeralFromString(workChild.text())
                        "book_count" -> newBook.booksCount = getNumeralFromString(workChild.text())
                        "ratings_count" -> newBook.ratingsCount = getNumeralFromString(workChild.text())
                        "text_reviews_count" -> newBook.textReviewsCount = getNumeralFromString(workChild.text())
                        "original_publication_year" -> newBook.originalPublicationYear = getNumeralFromString(workChild.text())
                        "original_publication_month" -> newBook.originalPublicationMonth = getNumeralFromString(workChild.text())
                        "original_publication_day" -> newBook.originalPublicationDay = getNumeralFromString(workChild.text())
                        "average_rating" -> newBook.averageRating = workChild.text().toDouble()
                    }
                    if (workChild.name() == "best_book") {
                        for (c in workChild.children()) {
                            val bookChild = (c as NodeChild)
                            when (bookChild.name()) {
                                "id" -> newBook.bookId = getNumeralFromString(bookChild.text())
                                "title" -> newBook.title = bookChild.text()
                                "author" -> { bookChild.children().forEach { bc ->
                                    val authorChild = (bc as NodeChild)
                                    when (authorChild.name()) {
                                        "id" -> newBook.authorId = getNumeralFromString(authorChild.text())
                                        "name" -> newBook.author = authorChild.text()
                                    }
                                    }
                                }
                                "image_url" -> newBook.imageUrl = bookChild.text()
                                "small_image_url" -> newBook.smallImageUrl = bookChild.text()
                            }

                            resultsList.add(newBook)
                        }
                    }
                }
            }

        }

        return resultsList
    }


    private fun getNumeralFromString(text: String?): Int {
        if (text != null) {
            return when {
                text == "" -> 0
                text.toInt().javaClass != Int::class.java -> 0
                else -> text.toInt()
            }
        }
        return 0
    }

    fun query(fullPath: String): String? {
        val (_, response, result) = fullPath.httpGet().responseString()
        if (response.responseMessage.equals("OK", true)) {
            var data: String? = null
            result.success { f ->
                data = f
            }
            return data
        }
        return null
    }

    fun printRatingsForCollection(localBooks: List<Book>) {
        val incomplete = ArrayList<ResultBook>()
        localBooks.forEach { book ->
            val found = searchForMatch(book.title, book.author)
            if (!found.incomplete) {
                println(found)
            }
            else {
                incomplete.add(found)
            }
        }
        println("${incomplete.size} incomplete books out of ${localBooks.size}")
    }


    fun getBooksFromDir(path: String): List<Book> {
        val bookList = ArrayList<Book>()
        File(path).listFiles().forEach { f ->
            val filename = f.name.split(" - ")
            if (filename.size == 2 || filename.size == 3) {
                val book = Book(filename[0], filename[filename.size - 1].replace(".mobi", ""))
                if (filename.size == 3) {
                    book.series = filename[1]
                }
                bookList.add(book)
            }
        }
        return bookList
    }

    fun createBooksDB() {
        val sql = """CREATE TABLE IF NOT EXISTS Books (
            | id integer PRIMARY KEY AUTOINCREMENT,
            | title text NOT NULL,
            | bookId integer,
            | author text NOT NULL,
            | authorId integer,
            | booksCount integer,
            | ratingsCount integer,
            | textReviewsCount integer,
            | originalPublicationYear integer,
            | originalPublicationMonth integer,
            | originalPublicationDay integer,
            | averageRating real,
            | imageUrl text,
            | smallImageUrl text,
            | incomplete integer,
            | read integer
            """.trimMargin()
        executeStatement(sql)
    }

    private fun executeStatement(sql: String) {
        val stmt = createStmt()
        try {
            stmt.execute(sql)
        } catch (e: SQLException) {
            println(e.message)
        }
    }

    fun insertResultBookIntoDB(book: ResultBook) {
        val sql = """INSERT INTO Books (
            |title,
            |bookId,
            |author,
            |authorId,
            |booksCount,
            |ratingsCount,
            |textReviewsCount,
            |originalPublicationYear,
            |originalPublicationMonth,
            |originalPublicationDay,
            |averageRating,
            |imageUrl,
            |smallImageUrl,
            |incomplete,
            |read
            |) values (
            | '${book.title}',
            | ${book.bookId}
            | '${book.author}',
            | ${book.authorId}
            | ${book.booksCount}
            | ${book.ratingsCount}
            | ${book.textReviewsCount}
            | ${book.originalPublicationYear}
            | ${book.originalPublicationMonth}
            | ${book.originalPublicationDay}
            | ${book.averageRating}
            | '${book.imageUrl}',
            | '${book.smallImageUrl}',
            | ${book.incomplete}
            | 0
            | """.trimMargin()

        executeStatement(sql)
    }

    fun selectAllFromDB(): String {
        val sql = "SELECT * FROM Books"
        var results = ""
        try {
            val stmt = createStmt()
            val rs = stmt.executeQuery(sql)
            while(rs.next()) {
                val id = rs.getInt("id")
                val name = rs.getString("name")
                results += "Id : $id, Name: $name"
            }
            return results
        }
        catch (e: SQLException) {
            println(e.message)
        }

        return "No Entries Found or DB Error!!"
    }

    private fun createStmt(): Statement {
        return DriverManager.getConnection(dbUrl).createStatement()
    }

    fun runSearchOnDirectory() {    //todo: Pass in a string with the directory rather than hardcode this.
        val fDir = File("/Users/tschumacher/Documents/KindlePack")
        println(fDir.isDirectory)
        val bookList = ArrayList<Book>()
        fDir.listFiles().forEach { f ->
            val filename = f.name.split(" - ")
            if (filename.size == 2 || filename.size == 3) {
                val book = Book(filename[0], filename[filename.size - 1].replace(".mobi", ""))
                if (filename.size == 3) {
                    book.series = filename[1]
                }
                bookList.add(book)
            }
        }
        for (book in bookList) {
            println("${book.title} by ${book.author}")
        }
    }
}
