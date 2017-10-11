import org.jsoup.Jsoup
import java.io.File
import java.util.*

/**
 * Handles parsing and computations
 * @input location: Directory where books are located
 */
class BookTexts(location: String) {
    private val bookTexts: List<TokenizedBook> = ArrayList()
    private var bookDir: String = location
    private var inverseDocumentFrequency: Map<String, Float> = TreeMap()

    init {
        // todo: refactor this because it's a hack
        if (location != "-1"){
            parseBooks(bookDir, bookTexts)
            println("Number of Books: ${bookTexts.size}")
        }
    }

    /**
     * Parses Books within a directory
     * @input s: Path to the html files representing books
     * @input bookList: list of tokenized books that gets populated
     */
    private fun parseBooks(s: String, bookList: List<TokenizedBook>) {
        if(s.isNullOrBlank()){
            println("path is null or blank!!!")
            parseBooks("../testBooks", bookList)
        }

        val pathList = parseAndShuffle(s)
        pathList
//                .subList(0, 500)
            .forEach { file ->
            val countMap = TokenizedBook().parseToCountMap(File("${file.absolutePath}/${file.name}.html"))
            (bookList as ArrayList).add(TokenizedBook(file.name, countMap))
        }
    }

    private fun parseAndShuffle(s: String): List<File> {
        val pathList = File(s).listFiles().asList()
        Collections.shuffle(pathList)
        return pathList
    }

    fun parseBooksToMapsAndWriteToFile(bookDir: File, outFileLoc: File){
        if (!File("out").exists()) {
            File("out").mkdirs()
        }

        if (outFileLoc.exists()){
            if(bookTexts.isEmpty())
                (bookTexts as ArrayList<TokenizedBook>).addAll(parseMapsFromFile(outFileLoc))
        }
        else {
            val tempBookList = ArrayList<TokenizedBook>()
            parseBooks(bookDir.absolutePath, tempBookList)
            outFileLoc.writeText(formatTokenizedBooksIntoString(tempBookList))

            if(bookTexts.isEmpty())
                (bookTexts as ArrayList<TokenizedBook>).addAll(tempBookList)
        }
    }

    /**
     * utility function to create file text for bookMaps representation
     */
    private fun formatTokenizedBooksIntoString(tempBookList: ArrayList<TokenizedBook>): String {
        return tempBookList.groupBy { book ->
            "${book.name}-_-${book.tokenMap}"
        }.keys.toList().joinToString("\n")
    }

    /**
     * create tokenized books from file
     */
    fun parseMapsFromFile(mapFile: File): List<TokenizedBook> {
        val listFromFile = mapFile.readLines().groupBy { line ->
            val entry = line.split("-_-")
            TokenizedBook(entry[0], createMapFromString(entry[1]))
        }.keys.toList()
        if (bookTexts.isEmpty()){
            (bookTexts as ArrayList<TokenizedBook>).addAll(listFromFile)
        }

        return listFromFile

    }

    private fun createMapFromString(s: String): Map<String, Int> {
        // in the form {k=v, ..., kn=vn}
        val re = Regex("[{|}]")
        val collapsedMapString = s.replace(re, "")
        return collapsedMapString.split(",").groupBy { entry ->
//            println(entry)
            val keyVal = entry.split("=")
            if (keyVal.size != 2){
                (keyVal as ArrayList<String>).add(0," ")
            }
            Pair(keyVal[0], keyVal[1].toInt())
        }.keys.toMap()
    }

    fun getTokenizedMaps(): List<TokenizedBook> = bookTexts
    /**
     * Use cosine similarity to find a general similarity based on token counts
     */
    fun findSimilar(queryBook: TokenizedBook): List<TokenizedBook> {
        println("Comparing ${queryBook.name}")
        queryBook.generateTermFrequencyList()
        println("Generating term frequencies for bookText set")
        bookTexts.forEach { book ->
            book.generateTermFrequencyList()
        }
        println("Calculate euclidian distance")
        val distanceMap = calculateEuclidianDistance(queryBook)
        val results = distanceMap.toList().sortedBy { (_, value) -> value }.asReversed().toMap()
        var i = 1
        results.keys
                //.toList().subList(0, 5)
                .forEach { book ->
            println("$i : '${book.name}' with a confidence of ${results[book]}")
                    i += 1
        }
        return results.keys.toList()
    }

    private fun calculateEuclidianDistance(queryBook: TokenizedBook) : HashMap<TokenizedBook, Float> {
        val distanceMap = HashMap<TokenizedBook, Float>()
        var distance = 0.0f
        bookTexts.forEach { compareBook ->
            queryBook.tokenMap.keys.forEach { term ->

                distance += euclidianDistance(queryBook.tokenMap[term]!!, compareBook.tokenMap[term]!!)
                // only consider like terms
            }
            distanceMap.put(compareBook, Math.sqrt(distance.toDouble()).toFloat())
        }
        return distanceMap
    }

    fun euclidianDistance(xi: Int, xj:Int): Float {
        return Math.pow((xi - xj).toDouble(), 2.0).toFloat()
    }

    fun generateInverseDocumentFrequency() {
        val allDocumentTokenList = generateTokenListsForAllDocs()
        allDocumentTokenList.keys.forEach { doc ->
            val documentTokenList = allDocumentTokenList[doc]
            documentTokenList!!.forEach { term ->
                var numberOfDocumentsContainingTerm = 0
                allDocumentTokenList.keys.forEach { document ->
                    if (allDocumentTokenList[document]!!.contains(term)){
                        numberOfDocumentsContainingTerm += 1
                    }
                }
                if (numberOfDocumentsContainingTerm > 0) {
                    (inverseDocumentFrequency as TreeMap<String, Float>).put(term, (1.0f + Math.log(1.0 * bookTexts.size) / numberOfDocumentsContainingTerm).toFloat())
                }
                else {
                    (inverseDocumentFrequency as TreeMap<String, Float>).put(term, 1.0f)
                }
            }
        }

    }

    private fun generateTokenListsForAllDocs(): TreeMap<String, List<String>> {
        val allDocumentTokenList = TreeMap<String, List<String>>()
        bookTexts.forEach { book ->
            allDocumentTokenList.put(book.name, book.tokenMap.keys.toList())
        }
        return allDocumentTokenList
    }

    private fun generateTokenCountForAllDocs(): TreeMap<String, Int> {
        val allDocumentTokenMap = TreeMap<String, Int>()
        bookTexts.forEach { book ->
            book.tokenMap.keys.forEach { token ->
                if (allDocumentTokenMap.containsKey(token)) {
                    allDocumentTokenMap[token] = (allDocumentTokenMap[token]!!.plus(book.tokenMap[token]!!))
                    println("Updated '$token''s count to ${allDocumentTokenMap[token]}")
                }
                else {
                    allDocumentTokenMap.put(token, book.tokenMap[token]!!)
                }

            }
        }

        return allDocumentTokenMap
    }

    constructor() : this(location = "-1"){

    }

    //todo: nGram comparisons on documents for more refined search

}

class TokenizedBook(name: String, tokenMap: Map<String, Int>) {
    var name: String = name
    var tokenMap: Map<String, Int> = tokenMap
    var termFrequencyList: Map<String, Float> = TreeMap()

    constructor() : this(name = "", tokenMap = TreeMap<String, Int>())
    constructor(name: String): this(name, tokenMap = TreeMap<String, Int>())
    constructor(file: File): this() {
        this.name = file.name.replace(".html", "")
        this.tokenMap = parseToCountMap(file)
    }

    fun parseToCountMap(textFile: File): Map<String, Int> {
        var textFromFile = Jsoup.parse(textFile.readText()).text()
        val re = Regex("[^A-Za-z0-9|\\s+]")
        textFromFile = re.replace(textFromFile, "")
        val tokens = textFromFile.split(" ")
        val tokenMap = TreeMap<String, Int>()
        for (token in tokens) {
            if (tokenMap.containsKey(token)) {
                tokenMap[token] = tokenMap[token]!! + 1
            }
            else {
                tokenMap.put(token, 1)
            }
        }

        return tokenMap
    }

    fun generateTermFrequencyList() {
//        println("Generating term Frequency for $name")
        val documentTokenCount: Float = tokenMap.keys.sumBy { tokenMap[it]!! } * 1.0f
        tokenMap.keys.forEach { token ->
            val frequency: Float = tokenMap[token]!! / documentTokenCount
//            println("Token: $token, Frequency: $frequency")
            (termFrequencyList as TreeMap<String, Float>).put(token, frequency)
        }
    }


}
